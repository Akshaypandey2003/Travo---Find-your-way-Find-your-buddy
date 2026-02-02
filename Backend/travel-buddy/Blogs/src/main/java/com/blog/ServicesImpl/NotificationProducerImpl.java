package com.blog.ServicesImpl;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.blog.Services.NotificationProducer;
import com.events.Entity.NotificationEvent;

@Service
public class NotificationProducerImpl implements NotificationProducer {

     private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

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

        kafkaTemplate.send("notification-events", blogId, event);
    }

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

        kafkaTemplate.send("notification-events", blogId, event);
    }
    
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

        kafkaTemplate.send("notification-events", blogId, event);
    }

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

        kafkaTemplate.send("notification-events", blogId, event);
    }


}
