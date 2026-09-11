package com.feed.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.feed.Entity.FeedItem;
import com.feed.Repository.FeedItemRepository;

@Service
public class FeedService {

    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    private final FeedItemRepository feedItemRepository;
    private final UserConnectionService userConnectionService;
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
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            SimpMessagingTemplate messagingTemplate) {
        this.feedItemRepository = feedItemRepository;
        this.userConnectionService = userConnectionService;
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

            if (isDuplicate(event.getEventId(), viewerId)) {
                continue;
            }

            FeedItem item = FeedItem.builder()
                    .viewerId(viewerId)
                    .eventId(event.getEventId())
                    .resourceType(event.getResourceType())
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
            logger.error("Failed to mark feed items inactive for resourceId={}: {}", event.getResourceId(), ex.getMessage());
        }

        for (FeedItem item : items) {
            evictLatestFeedCache(item.getViewerId());
        }
    }

    public List<FeedItemResponse> getFeedForUser(String userId, int limit) {
        if (userId == null || userId.isBlank()) {
            return Collections.emptyList();
        }

        int safeLimit = Math.min(Math.max(limit, 1), 100);
        List<FeedItemResponse> cached = getLatestFeedFromCache(userId, safeLimit);
        if (!cached.isEmpty()) {
            return cached;
        }

        List<FeedItem> items = feedItemRepository
                .findByViewerIdAndActiveTrueOrderByCreatedAtDesc(userId, PageRequest.of(0, safeLimit))
                .getContent();

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<FeedItemResponse> responses = items.stream()
                .map(this::toResponse)
                .toList();

        cacheLatestFeed(userId, responses);

        return responses;
    }

    private boolean isDuplicate(String eventId, String viewerId) {
        if (eventId == null || viewerId == null) {
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
                responses.add(objectMapper.readValue(payload, new TypeReference<FeedItemResponse>() {}));
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
                .thumbnailUrl(item.getThumbnailUrl())
                .build();
    }
}
