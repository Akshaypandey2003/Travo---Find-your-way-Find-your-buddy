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
import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
@SuppressWarnings("unused")
public class BlogDomainEventPublisherImpl implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BlogDomainEventPublisherImpl.class);
    private static final String TOPIC = "blog-events";

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
    }

     private void publish(String key, Object event) {
        try {
            kafkaTemplate.send(TOPIC, key, event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish trip domain event", e);
        }
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
    }

    public void blogDeletedFallback(String blogId, String authorId, Exception ex)
    {
      logger.error("Failed to publish BlogDeletedEvent for blogId {}: {}", blogId, ex.getMessage());
       BlogDeletedEvent event = BlogDeletedEvent.builder()
                .blogId(blogId)
                .authorId(authorId)
                .build();
        saveFailedEvent(TOPIC, blogId, event, ex);
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
