package com.feed.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.events.Feed.PostCreatedEvent;
import com.events.Feed.PostDeletedEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feed.DTO.FeedItemResponse;
import com.feed.DTO.BlogEngagementResponse;
import com.feed.DTO.DiscoveryBlogResponse;
import com.feed.Entity.FeedItem;
import com.feed.Repository.FeedItemRepository;

@Service
public class FeedService {

    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    private final FeedItemRepository feedItemRepository;
    private final UserConnectionService userConnectionService;
    private final BlogDiscoveryService blogDiscoveryService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${feed.redis.latest-size:20}")
    private int latestFeedSize;

    @Value("${feed.redis.latest-ttl-minutes:30}")
    private long latestFeedTtlMinutes;

    @Value("${feed.dedupe.ttl-hours:24}")
    private long dedupeTtlHours;

    public FeedService(
            FeedItemRepository feedItemRepository,
            UserConnectionService userConnectionService,
            BlogDiscoveryService blogDiscoveryService,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            SimpMessagingTemplate messagingTemplate) {
        this.feedItemRepository = feedItemRepository;
        this.userConnectionService = userConnectionService;
        this.blogDiscoveryService = blogDiscoveryService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public void handlePostCreated(PostCreatedEvent event) {
        if (event == null || event.getAuthorId() == null) {
            return;
        }

        List<String> viewers = userConnectionService.getFollowers(event.getAuthorId());
        if (viewers.isEmpty()) {
            return;
        }

        List<FeedItem> itemsToInsert = new ArrayList<>();
        
        for (String viewerId : viewers) {
            if (viewerId == null || viewerId.isBlank()) {
                continue;
            }

            if (isDuplicate(event.getEventId(), event.getResourceId(), viewerId)) {
                continue;
            }

            FeedItem item = FeedItem.builder()
                    .viewerId(viewerId)
                    .eventId(event.getEventId())
                    .resourceType(event.getResourceType())
                    .likesCount(event.getLikesCount())
                    .commentsCount(event.getCommentsCount())
                    .viewsCount(event.getViewCount())
                    .authorId(event.getAuthorId())
                    .authorName(event.getAuthorName())
                    .authorProfilePic(event.getAuthorProfilePic())
                    .resourceId(event.getResourceId())
                    .caption(event.getCaption())
                    .images(event.getImages() == null ? List.of() : event.getImages())
                    .visibility(event.getVisibility())
                    .createdAt(event.getCreatedAt())
                    .thumbnailUrl(firstImage(event.getImages()))
                    .active(true)
                    .build();
            itemsToInsert.add(item);
        }

        if (itemsToInsert.isEmpty()) {
            return;
        }

        try {
            List<FeedItem> saved = feedItemRepository.saveAll(itemsToInsert);
            for (FeedItem savedItem : saved) {
                FeedItemResponse response = toResponse(savedItem);
                updateLatestFeedCache(savedItem.getViewerId(), response);
                pushToWebSocket(savedItem.getViewerId(), response);
            }
        } catch (Exception ex) {
            logger.error("Failed to persist feed items for eventId={}: {}", event.getEventId(), ex.getMessage());
        }
    }

    public void handlePostDeleted(PostDeletedEvent event) {
        if (event == null || event.getResourceId() == null) {
            return;
        }

        List<FeedItem> items = feedItemRepository.findByResourceIdAndResourceTypeAndActiveTrue(
                event.getResourceId(), event.getResourceType());

        if (items.isEmpty()) {
            return;
        }

        for (FeedItem item : items) {
            item.setActive(false);
        }

        try {
            feedItemRepository.saveAll(items);
        } catch (Exception ex) {
            logger.error("Failed to mark feed items inactive for resourceId={}: {}", event.getResourceId(),
                    ex.getMessage());
        }

        for (FeedItem item : items) {
            evictLatestFeedCache(item.getViewerId());
        }
    }

    public List<FeedItemResponse> getFeedForUser(String userId, int page, int limit) {
        return getFeedForUser(userId, page, limit, null);
    }

    public List<FeedItemResponse> getFeedForUser(String userId, int page, int limit, String authorization) {

        if (userId == null || userId.isBlank()) {
            return Collections.emptyList();
        }

        int safePage = Math.max(page, 0);
        int safeLimit = Math.min(Math.max(limit, 1), 100);

        // Number of blogs we need to generate to reach the requested page
        int requiredBlogs = (safePage + 1) * safeLimit;

        List<FeedItemResponse> personalized = getPersonalizedFeed(userId, requiredBlogs);

        List<FeedItemResponse> discovery = Collections.emptyList();

        if (personalized.size() < requiredBlogs) {

            int remaining = requiredBlogs - personalized.size();

            List<String> publicAuthors = userConnectionService.getPublicUserIds(
                    Math.min(100, Math.max(remaining * 10, 20)));

            discovery = blogDiscoveryService
                    .getDiscoveryBlogs(publicAuthors, remaining)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        List<FeedItemResponse> merged = mergeAndDeduplicate(
                personalized,
                discovery,
                requiredBlogs);

        int fromIndex = safePage * safeLimit;

        if (fromIndex >= merged.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + safeLimit, merged.size());

        List<FeedItemResponse> pageItems = new ArrayList<>(merged.subList(fromIndex, toIndex));
        hydrateEngagement(pageItems, authorization);
        return pageItems;
    }

    public void backfillConnection(String viewerId, String authorId) {
        if (viewerId == null || viewerId.isBlank() || authorId == null || authorId.isBlank()) {
            return;
        }
        List<DiscoveryBlogResponse> blogs = blogDiscoveryService.getBlogsByAuthor(authorId, 100);
        for (DiscoveryBlogResponse blog : blogs) {
            if (blog.getResourceId() == null
                    || feedItemRepository.findByResourceIdAndViewerId(blog.getResourceId(), viewerId).isPresent()) {
                continue;
            }
            FeedItem item = FeedItem.builder()
                    .viewerId(viewerId)
                    .eventId("blog:" + blog.getResourceId())
                    .resourceType("BLOG")
                    .authorId(blog.getAuthorId())
                    .authorName(blog.getAuthorName())
                    .authorProfilePic(blog.getAuthorProfilePic())
                    .resourceId(blog.getResourceId())
                    .caption(blog.getCaption())
                    .images(blog.getImages() == null ? List.of() : blog.getImages())
                    .visibility("PUBLIC")
                    .createdAt(blog.getCreatedAt())
                    .thumbnailUrl(firstImage(blog.getImages()))
                    .active(true)
                    .build();
            feedItemRepository.save(item);
        }
        evictLatestFeedCache(viewerId);
    }

    public void removeConnection(String viewerId, String authorId) {
        List<FeedItem> items = feedItemRepository.findByViewerIdAndAuthorIdAndActiveTrue(viewerId, authorId);
        if (items.isEmpty()) {
            return;
        }
        items.forEach(item -> item.setActive(false));
        feedItemRepository.saveAll(items);
        evictLatestFeedCache(viewerId);
    }

    public void initializeUser(String userId) {
        redisTemplate.opsForValue().setIfAbsent("feed:initialized:" + userId, "1");
        evictLatestFeedCache(userId);
    }

    private List<FeedItemResponse> getPersonalizedFeed(String userId, int limit) {
        List<FeedItemResponse> cached = getLatestFeedFromCache(userId, limit);
        if (cached.size() >= limit) {
            return cached;
        }

        List<FeedItem> items = feedItemRepository
                .findByViewerIdAndActiveTrueOrderByCreatedAtDesc(userId, PageRequest.of(0, limit))
                .getContent();
        List<FeedItemResponse> responses = items.stream().map(this::toResponse).toList();
        if (!responses.isEmpty()) {
            cacheLatestFeed(userId, responses);
        }
        return responses;
    }

    private List<FeedItemResponse> mergeAndDeduplicate(
            List<FeedItemResponse> personalized,
            List<FeedItemResponse> discovery,
            int limit) {
        Map<String, FeedItemResponse> unique = new LinkedHashMap<>();
        personalized.forEach(item -> unique.put(item.getResourceId(), item));
        discovery.forEach(item -> unique.putIfAbsent(item.getResourceId(), item));
        return unique.values().stream()
                .sorted(Comparator.comparing(FeedItemResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }

    private boolean isDuplicate(String eventId, String resourceId, String viewerId) {
        if (viewerId == null) {
            return true;
        }
        if (resourceId != null && feedItemRepository.findByResourceIdAndViewerId(resourceId, viewerId).isPresent()) {
            return true;
        }
        if (eventId == null) {
            return false;
        }
        String key = "feed:dedupe:" + eventId + ":" + viewerId;
        Boolean first = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofHours(dedupeTtlHours));
        return first != null && !first;
    }

    private void updateLatestFeedCache(String viewerId, FeedItemResponse response) {
        try {
            if (latestFeedSize <= 0) {
                return;
            }
            String key = "feed:latest:" + viewerId;
            String payload = objectMapper.writeValueAsString(response);
            ListOperations<String, String> ops = redisTemplate.opsForList();
            ops.leftPush(key, payload);
            ops.trim(key, 0, latestFeedSize - 1);
            redisTemplate.expire(key, Duration.ofMinutes(latestFeedTtlMinutes));
        } catch (Exception ex) {
            logger.warn("Failed to update latest feed cache for viewerId={}: {}", viewerId, ex.getMessage());
        }
    }

    private void cacheLatestFeed(String viewerId, List<FeedItemResponse> responses) {
        try {
            if (latestFeedSize <= 0) {
                return;
            }
            String key = "feed:latest:" + viewerId;
            redisTemplate.delete(key);
            ListOperations<String, String> ops = redisTemplate.opsForList();
            for (FeedItemResponse response : responses) {
                ops.rightPush(key, objectMapper.writeValueAsString(response));
            }
            ops.trim(key, 0, latestFeedSize - 1);
            redisTemplate.expire(key, Duration.ofMinutes(latestFeedTtlMinutes));
        } catch (Exception ex) {
            logger.warn("Failed to cache latest feed for viewerId={}: {}", viewerId, ex.getMessage());
        }
    }

    private List<FeedItemResponse> getLatestFeedFromCache(String viewerId, int limit) {
        try {
            String key = "feed:latest:" + viewerId;
            ListOperations<String, String> ops = redisTemplate.opsForList();
            List<String> cached = ops.range(key, 0, limit - 1);
            if (cached == null || cached.isEmpty()) {
                return Collections.emptyList();
            }
            List<FeedItemResponse> responses = new ArrayList<>();
            for (String payload : cached) {
                responses.add(objectMapper.readValue(payload, new TypeReference<FeedItemResponse>() {
                }));
            }
            return responses;
        } catch (Exception ex) {
            logger.warn("Failed to read latest feed cache for viewerId={}: {}", viewerId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    private void evictLatestFeedCache(String viewerId) {
        if (viewerId == null || viewerId.isBlank()) {
            return;
        }
        redisTemplate.delete("feed:latest:" + viewerId);
    }

    private void pushToWebSocket(String viewerId, FeedItemResponse response) {
        try {
            messagingTemplate.convertAndSendToUser(viewerId, "/queue/feed", response);
        } catch (Exception ex) {
            logger.warn("Failed to push feed item to websocket for viewerId={}: {}", viewerId, ex.getMessage());
        }
    }

    private String firstImage(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(0);
    }

    private FeedItemResponse toResponse(FeedItem item) {
        return FeedItemResponse.builder()
                .eventId(item.getEventId())
                .resourceType(item.getResourceType())
                .authorId(item.getAuthorId())
                .authorName(item.getAuthorName())
                .authorProfilePic(item.getAuthorProfilePic())
                .resourceId(item.getResourceId())
                .caption(item.getCaption())
                .images(item.getImages() == null ? List.of() : item.getImages())
                .visibility(item.getVisibility())
                .createdAt(item.getCreatedAt())
                .likesCount(item.getLikesCount())
                .commentsCount(item.getCommentsCount())
                .viewsCount(item.getViewsCount())
                .thumbnailUrl(item.getThumbnailUrl())
                .build();
    }

    private FeedItemResponse toResponse(DiscoveryBlogResponse blog) {
        return FeedItemResponse.builder()
                .eventId("discovery:" + blog.getResourceId())
                .resourceType("BLOG")
                .authorId(blog.getAuthorId())
                .authorName(blog.getAuthorName())
                .authorProfilePic(blog.getAuthorProfilePic())
                .resourceId(blog.getResourceId())
                .caption(blog.getCaption())
                .images(blog.getImages() == null ? List.of() : blog.getImages())
                .visibility("PUBLIC")
                .createdAt(blog.getCreatedAt())
                .thumbnailUrl(firstImage(blog.getImages()))
                .build();
    }

    private void hydrateEngagement(List<FeedItemResponse> items, String authorization) {
        List<String> blogIds = items.stream()
                .filter(item -> "BLOG".equalsIgnoreCase(item.getResourceType()))
                .map(FeedItemResponse::getResourceId)
                .filter(resourceId -> resourceId != null && !resourceId.isBlank())
                .distinct()
                .toList();
        if (blogIds.isEmpty() || authorization == null || authorization.isBlank()) {
            return;
        }

        Map<String, BlogEngagementResponse> engagementByBlogId = blogDiscoveryService
                .getEngagement(blogIds, authorization)
                .stream()
                .collect(Collectors.toMap(BlogEngagementResponse::getBlogId, engagement -> engagement));

        items.stream()
                .filter(item -> item.getResourceId() != null)
                .forEach(item -> {
                    BlogEngagementResponse engagement = engagementByBlogId.get(item.getResourceId());
                    if (engagement != null) {
                        item.setLikesCount(engagement.getLikesCount());
                        item.setCommentsCount(engagement.getCommentsCount());
                        item.setViewsCount(engagement.getViewsCount());
                        item.setLikedByMe(engagement.isLikedByMe());
                    }
                });
    }
}
