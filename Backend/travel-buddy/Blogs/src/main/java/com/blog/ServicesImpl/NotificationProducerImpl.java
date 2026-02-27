package com.blog.ServicesImpl;

import java.time.Instant;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.blog.Services.NotificationProducer;
import com.events.Notification.FailedNotification;
import com.events.Notification.NotificationEvent;
import com.events.Repositories.FailedNotificationRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class NotificationProducerImpl implements NotificationProducer {

     private final KafkaTemplate<String, com.events.Notification.NotificationEvent> kafkaTemplate;
     private final String TOPIC = "notification-events";
     private final FailedNotificationRepository failedNotificationRepo;

    public NotificationProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate,FailedNotificationRepository failedNotificationRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedNotificationRepo=failedNotificationRepo;
    }
    
    @Override
    @CircuitBreaker(name = "blogNotificationCircuitBreaker", fallbackMethod = "blogPostedFallback")
    @Retry(name = "blogNotificationRetry")
    public void postBlogNotification(String senderId, String receiverId, String blogId, String blogTitle) {
        NotificationEvent event = new NotificationEvent(
            "BLOG_CREATED",
            senderId,
            receiverId,
            "has posted a new blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );

        kafkaTemplate.send(TOPIC, blogId, event);
    }
    
    @Override
    @CircuitBreaker(name = "blogNotificationCircuitBreaker", fallbackMethod = "likeBlogFallback")
    @Retry(name = "blogNotificationRetry")
    public void likeBlogNotification(String senderId, String receiverId, String blogId, String blogTitle) {
        NotificationEvent event = new NotificationEvent(
            "BLOG_LIKED",
            senderId,
            receiverId,
            "has liked your blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );

        kafkaTemplate.send(TOPIC, blogId, event);
    }
    
    @Override
     @CircuitBreaker(name = "blogNotificationCircuitBreaker", fallbackMethod = "blogCommentFallback")
    @Retry(name = "blogNotificationRetry")
    public void sendCommentNotification(String senderId, String receiverId, String blogId, String blogTitle) {
        NotificationEvent event = new NotificationEvent(
            "COMMENT_ADDED",
            senderId,
            receiverId,
            "has commented on your blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );

        kafkaTemplate.send(TOPIC, blogId, event);
    }

    @Override
    @CircuitBreaker(name = "blogNotificationCircuitBreaker", fallbackMethod = "commentLikeFallback")
    @Retry(name = "blogNotificationRetry")
    public void sendCommentLikeNotification(String senderId, String receiverId, String blogId, String blogTitle) {
        NotificationEvent event = new NotificationEvent(
            "COMMENT_LIKED",
            senderId,
            receiverId,
            "has liked your comment on blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );

        kafkaTemplate.send(TOPIC, blogId, event);
    }

    public void blogPostedFallback(String senderId, String receiverId, String blogId, String blogTitle,Exception ex)
    {
          NotificationEvent event = new NotificationEvent(
            "BLOG_CREATED",
            senderId,
            receiverId,
            "has posted a new blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );

        saveFailedEvent(TOPIC, blogId, event, ex);
    }

    public void likeBlogFallback(String senderId, String receiverId, String blogId, String blogTitle, Exception ex)
    {
         NotificationEvent event = new NotificationEvent(
            "BLOG_LIKED",
            senderId,
            receiverId,
            "has liked your blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );
        saveFailedEvent(TOPIC, blogId, event, ex);
    }
    public void blogCommentFallback(String senderId, String receiverId, String blogId, String blogTitle,Exception ex)
    {
        NotificationEvent event = new NotificationEvent(
            "COMMENT_ADDED",
            senderId,
            receiverId,
            "has commented on your blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );
        saveFailedEvent(TOPIC, blogId, event, ex);
    }

    public void commentLikeFallback(String senderId, String receiverId, String blogId, String blogTitle, Exception ex) {
        NotificationEvent event = new NotificationEvent(
            "COMMENT_LIKED",
            senderId,
            receiverId,
            "has liked your comment on blog " + blogTitle,
            "BLOG",
            blogId,
            Map.of("blogId", blogId, "blogTitle", blogTitle),
            System.currentTimeMillis()
        );
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
        failedNotificationRepo.save(failed);
    }

}
