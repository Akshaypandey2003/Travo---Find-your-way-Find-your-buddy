package com.feed.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feed.DTO.DiscoveryBlogResponse;
import com.feed.DTO.BlogEngagementResponse;
import com.feed.Entity.FeedItem;
import com.feed.Repository.FeedItemRepository;

class FeedServiceTest {

    @Mock
    private FeedItemRepository feedItemRepository;
    @Mock
    private UserConnectionService userConnectionService;
    @Mock
    private BlogDiscoveryService blogDiscoveryService;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private FeedService feedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feedService = new FeedService(
                feedItemRepository,
                userConnectionService,
                blogDiscoveryService,
                redisTemplate,
                new ObjectMapper(),
                messagingTemplate);
    }

    @Test
    void returnsPersonalizedItemsWithoutQueryingDiscoveryWhenLimitIsMet() {
        when(feedItemRepository.findByViewerIdAndActiveTrueOrderByCreatedAtDesc("viewer", org.springframework.data.domain.PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(item("one"), item("two"))));

        assertThat(feedService.getFeedForUser("viewer", 0, 2))
                .extracting("resourceId")
                .containsExactly("one", "two");
        verifyNoInteractions(blogDiscoveryService, userConnectionService);
    }

    @Test
    void fillsPartialPersonalizedFeedFromDiscovery() {
        when(feedItemRepository.findByViewerIdAndActiveTrueOrderByCreatedAtDesc("viewer", org.springframework.data.domain.PageRequest.of(0, 3)))
                .thenReturn(new PageImpl<>(List.of(item("one"))));
        when(userConnectionService.getPublicUserIds(20)).thenReturn(List.of("author"));
        when(blogDiscoveryService.getDiscoveryBlogs(List.of("author"), 2))
                .thenReturn(List.of(discovery("two"), discovery("three")));

        assertThat(feedService.getFeedForUser("viewer", 0, 3))
                .extracting("resourceId")
                .containsExactly("three", "two", "one");
        verify(blogDiscoveryService).getDiscoveryBlogs(List.of("author"), 2);
    }

    @Test
    void removesTheSameResourceWhenItAppearsInBothSources() {
        when(feedItemRepository.findByViewerIdAndActiveTrueOrderByCreatedAtDesc("viewer", org.springframework.data.domain.PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(item("same"))));
        when(userConnectionService.getPublicUserIds(20)).thenReturn(List.of("author"));
        when(blogDiscoveryService.getDiscoveryBlogs(List.of("author"), 1))
                .thenReturn(List.of(discovery("same")));

                assertThat(feedService.getFeedForUser("viewer", 0, 2))
                .extracting("resourceId")
                .containsExactly("same");
    }

        @Test
        void hydratesReturnedBlogsWithViewerEngagementInOneBatch() {
                when(feedItemRepository.findByViewerIdAndActiveTrueOrderByCreatedAtDesc("viewer", org.springframework.data.domain.PageRequest.of(0, 1)))
                                .thenReturn(new PageImpl<>(List.of(item("blog-1"))));
                BlogEngagementResponse engagement = new BlogEngagementResponse();
                engagement.setBlogId("blog-1");
                engagement.setLikesCount(8);
                engagement.setCommentsCount(3);
                engagement.setViewsCount(42);
                engagement.setLikedByMe(true);
                when(blogDiscoveryService.getEngagement(List.of("blog-1"), "Bearer token"))
                                .thenReturn(List.of(engagement));

                var result = feedService.getFeedForUser("viewer", 0, 1, "Bearer token");

                assertThat(result).singleElement().satisfies(post -> {
                        assertThat(post.getLikesCount()).isEqualTo(8);
                        assertThat(post.getCommentsCount()).isEqualTo(3);
                        assertThat(post.getViewsCount()).isEqualTo(42);
                        assertThat(post.isLikedByMe()).isTrue();
                });
                verify(blogDiscoveryService).getEngagement(List.of("blog-1"), "Bearer token");
        }

    private FeedItem item(String resourceId) {
        return FeedItem.builder()
                .viewerId("viewer")
                .resourceId(resourceId)
                .eventId("event-" + resourceId)
                .resourceType("BLOG")
                .authorId("author")
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    private DiscoveryBlogResponse discovery(String resourceId) {
        DiscoveryBlogResponse blog = new DiscoveryBlogResponse();
        blog.setResourceId(resourceId);
        blog.setAuthorId("author");
        blog.setCreatedAt(Instant.parse(
                "three".equals(resourceId) ? "2026-01-03T00:00:00Z" : "2026-01-02T00:00:00Z"));
        return blog;
    }
}