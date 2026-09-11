package com.blog.ServicesImpl;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.blog.Entity.Blog;
import com.blog.Services.EventPublisher;
import com.events.Blog.BlogCreatedEvent;
import com.events.Blog.BlogDeletedEvent;
import com.events.Feed.PostCreatedEvent;
import com.events.Feed.PostDeletedEvent;
import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
@SuppressWarnings("unused")
public class BlogDomainEventPublisherImpl implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BlogDomainEventPublisherImpl.class);
    private static final String TOPIC = "blog-events";
    private static final String POST_TOPIC = "post-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailedNotificationRepository failedNotificationRepository;

    public BlogDomainEventPublisherImpl(KafkaTemplate<String, Object> kafkaTemplate, FailedNotificationRepository failedNotificationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedNotificationRepository = failedNotificationRepository;
    }

    @Override
    @CircuitBreaker(name = "blogDomainEventCircuitBreaker", fallbackMethod = "blogCreatedFallback")
    @Retry(name = "blogDomainEventRetry")
    public void publishBlogCreated(Blog blog) {
       
        BlogCreatedEvent event = BlogCreatedEvent.builder()
                .id(blog.getId())
                .authorId(blog.getAuthorId())
                .authorProfilePic(blog.getAuthorProfilePic())
                .authorName(blog.getAuthorName())
                .title(blog.getTitle())
                .content(blog.getContent())
                .caption(blog.getCaption())
                .category(blog.getCategory())
                .commentCount(blog.getCommentCount())
                .likeCount(blog.getLikeCount())
                .imageUrls(blog.getImageUrls())
                .cloudinaryPublicIds(blog.getCloudinaryPublicIds())
                .createdAt(blog.getCreatedAt())
                .build();
        publish(blog.getId(), event);
        publishPostCreatedEvent(blog);
    }
    
    @Override
    @CircuitBreaker(name = "blogDomainEventCircuitBreaker", fallbackMethod = "blogDeletedFallback")
    @Retry(name = "blogDomainEventRetry")
    public void publishBlogDeleted(String blogId, String authorId) {
        // Implement BlogDeletedEvent and publish it similarly to BlogCreatedEvent

        BlogDeletedEvent event = BlogDeletedEvent.builder()
                .blogId(blogId)
                .authorId(authorId)
                .build();
        publish(blogId,event);
        publishPostDeletedEvent(blogId, authorId);
    }

     private void publish(String key, Object event) {
        publish(TOPIC, key, event);
    }

    private void publish(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish trip domain event", e);
        }
    }

    private void publishPostCreatedEvent(Blog blog) {
        try {
            PostCreatedEvent postEvent = PostCreatedEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .resourceType("BLOG")
                    .authorId(blog.getAuthorId())
                    .authorName(blog.getAuthorName())
                    .authorProfilePic(blog.getAuthorProfilePic())
                    .resourceId(blog.getId())
                    .caption(blog.getCaption())
                    .images(blog.getImageUrls())
                    .visibility("PUBLIC")
                    .createdAt(blog.getCreatedAt())
                    .build();
            publish(POST_TOPIC, blog.getId(), postEvent);
        } catch (Exception ex) {
            logger.error("Failed to publish PostCreatedEvent for blogId {}: {}", blog.getId(), ex.getMessage());
            saveFailedPostCreatedEvent(blog, ex);
        }
    }

    private void publishPostDeletedEvent(String blogId, String authorId) {
        try {
            PostDeletedEvent postDeletedEvent = PostDeletedEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .resourceType("BLOG")
                    .resourceId(blogId)
                    .authorId(authorId)
                    .build();
            publish(POST_TOPIC, blogId, postDeletedEvent);
        } catch (Exception ex) {
            logger.error("Failed to publish PostDeletedEvent for blogId {}: {}", blogId, ex.getMessage());
            saveFailedPostDeletedEvent(blogId, authorId, ex);
        }
    }

    private void saveFailedPostCreatedEvent(Blog blog, Exception ex) {
        PostCreatedEvent postEvent = PostCreatedEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .resourceType("BLOG")
                .authorId(blog.getAuthorId())
                .authorName(blog.getAuthorName())
                .authorProfilePic(blog.getAuthorProfilePic())
                .resourceId(blog.getId())
                .caption(blog.getCaption())
                .images(blog.getImageUrls())
                .visibility("PUBLIC")
                .createdAt(blog.getCreatedAt())
                .build();
        saveFailedEvent(POST_TOPIC, blog.getId(), postEvent, ex);
    }

    private void saveFailedPostDeletedEvent(String blogId, String authorId, Exception ex) {
        PostDeletedEvent postDeletedEvent = PostDeletedEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .resourceType("BLOG")
                .resourceId(blogId)
                .authorId(authorId)
                .build();
        saveFailedEvent(POST_TOPIC, blogId, postDeletedEvent, ex);
    }

    public void blogCreatedFallback(Blog blog, Exception ex)
    {
        logger.error("Failed to publish BlogCreatedEvent for blogId {}: {}", blog.getId(), ex.getMessage());
        BlogCreatedEvent event = BlogCreatedEvent.builder()
                .id(blog.getId())
                .authorId(blog.getAuthorId())
                .authorProfilePic(blog.getAuthorProfilePic())
                .authorName(blog.getAuthorName())
                .title(blog.getTitle())
                .content(blog.getContent())
                .caption(blog.getCaption())
                .category(blog.getCategory())
                .commentCount(blog.getCommentCount())
                .likeCount(blog.getLikeCount())
                .imageUrls(blog.getImageUrls())
                .cloudinaryPublicIds(blog.getCloudinaryPublicIds())
                .createdAt(blog.getCreatedAt())
                .build();
        saveFailedEvent(TOPIC, blog.getId(), event, ex);
        saveFailedPostCreatedEvent(blog, ex);
    }

    public void blogDeletedFallback(String blogId, String authorId, Exception ex)
    {
      logger.error("Failed to publish BlogDeletedEvent for blogId {}: {}", blogId, ex.getMessage());
       BlogDeletedEvent event = BlogDeletedEvent.builder()
                .blogId(blogId)
                .authorId(authorId)
                .build();
        saveFailedEvent(TOPIC, blogId, event, ex);
        saveFailedPostDeletedEvent(blogId, authorId, ex);
    }

     private void saveFailedEvent(String topic, String key, Object event, Exception ex) {
        FailedNotification failed = FailedNotification.builder()
                .topic(topic)
                .key(key)
                .event(event)
                .retryCount(0)
                .createdAt(Instant.now())
                .lastRetryAt(null)
                .failureReason(ex.getMessage())
                .build();
        failedNotificationRepository.save(failed);
    }
}
