package com.chat.ServiceImpl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.chat.Service.NotificationProducer;
import com.events.Notification.FailedNotification;
import com.events.Entity.NotificationEvent;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class ChatNotificationProducer implements NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(ChatNotificationProducer.class);
    private static final String NOTIFICATION_TOPIC = "notification-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MongoTemplate mongoTemplate;

    public ChatNotificationProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            MongoTemplate mongoTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @CircuitBreaker(name = "chatNotificationCircuitBreaker", fallbackMethod = "messageSentFallback")
    @Retry(name = "chatNotificationRetry")
    public void messageSent(String senderId, String receiverId, String chatId, String chatName) {

        String message = (chatName != null && !chatName.isEmpty())
            ? "sent you a message in " + chatName
            : "sent you a message";

        Map<String, String> metadata = new HashMap<>();
        metadata.put("chatId", chatId);

        if (chatName != null) {
        metadata.put("chatName", chatName);
    }

        NotificationEvent event = new NotificationEvent(
            "MESSAGE_SENT",
            senderId,
            receiverId,
            message,
            "CHAT",
            chatId,
            metadata,
            System.currentTimeMillis()
        );

        publish(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "chatNotificationCircuitBreaker", fallbackMethod = "groupCreatedFallback")
    @Retry(name = "chatNotificationRetry")
    public void groupCreated(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_CREATED",
            senderId,
            receiverId,
            "added you to " + chatName,
            "CHAT",
            chatId,
            metadata(chatId, chatName),
            System.currentTimeMillis()
        );

        publish(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "chatNotificationCircuitBreaker", fallbackMethod = "groupUpdatedFallback")
    @Retry(name = "chatNotificationRetry")
    public void groupUpdated(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_UPDATED",
            senderId,
            receiverId,
            "has updated the group " + chatName,
            "CHAT",
            chatId,
            metadata(chatId, chatName),
            System.currentTimeMillis()
        );

        publish(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "chatNotificationCircuitBreaker", fallbackMethod = "addGroupMemberFallback")
    @Retry(name = "chatNotificationRetry")
    public void addGroupMember(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_MEMBER_ADDED",
            senderId,
            receiverId,
            "has added you to the group " + chatName,
            "CHAT",
            chatId,
            metadata(chatId, chatName),
            System.currentTimeMillis()
        );

        publish(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "chatNotificationCircuitBreaker", fallbackMethod = "removeGroupMemberFallback")
    @Retry(name = "chatNotificationRetry")
    public void removeGroupMember(String senderId, String receiverId, String chatId,String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_MEMBER_REMOVED",
            senderId,
            receiverId,
            "has removed you from the group " + chatName,
            "CHAT",
            chatId,
            metadata(chatId, chatName),
            System.currentTimeMillis()
        );

        publish(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "chatNotificationCircuitBreaker", fallbackMethod = "deleteGroupFallback")
    @Retry(name = "chatNotificationRetry")
    public void deleteGroup(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_DELETED",
            senderId,
            receiverId,
            "has deleted the group " + chatName,
            "CHAT",
            chatId,
            metadata(chatId, chatName),
            System.currentTimeMillis()
        );

        publish(receiverId, event);
    }

    public void messageSentFallback(String senderId, String receiverId, String chatId, String chatName, Exception ex) {
        saveFailedNotification(receiverId, new NotificationEvent(
                "MESSAGE_SENT",
                senderId,
                receiverId,
                (chatName != null && !chatName.isEmpty()) ? "sent you a message in " + chatName : "sent you a message",
                "CHAT",
                chatId,
                metadata(chatId, chatName),
                System.currentTimeMillis()), ex);
    }

    public void groupCreatedFallback(String senderId, String receiverId, String chatId, String chatName, Exception ex) {
        saveFailedNotification(receiverId, new NotificationEvent(
                "GROUP_CREATED",
                senderId,
                receiverId,
                "added you to " + chatName,
                "CHAT",
                chatId,
                metadata(chatId, chatName),
                System.currentTimeMillis()), ex);
    }

    public void groupUpdatedFallback(String senderId, String receiverId, String chatId, String chatName, Exception ex) {
        saveFailedNotification(receiverId, new NotificationEvent(
                "GROUP_UPDATED",
                senderId,
                receiverId,
                "has updated the group " + chatName,
                "CHAT",
                chatId,
                metadata(chatId, chatName),
                System.currentTimeMillis()), ex);
    }

    public void addGroupMemberFallback(String senderId, String receiverId, String chatId, String chatName, Exception ex) {
        saveFailedNotification(receiverId, new NotificationEvent(
                "GROUP_MEMBER_ADDED",
                senderId,
                receiverId,
                "has added you to the group " + chatName,
                "CHAT",
                chatId,
                metadata(chatId, chatName),
                System.currentTimeMillis()), ex);
    }

    public void removeGroupMemberFallback(String senderId, String receiverId, String chatId, String chatName, Exception ex) {
        saveFailedNotification(receiverId, new NotificationEvent(
                "GROUP_MEMBER_REMOVED",
                senderId,
                receiverId,
                "has removed you from the group " + chatName,
                "CHAT",
                chatId,
                metadata(chatId, chatName),
                System.currentTimeMillis()), ex);
    }

    public void deleteGroupFallback(String senderId, String receiverId, String chatId, String chatName, Exception ex) {
        saveFailedNotification(receiverId, new NotificationEvent(
                "GROUP_DELETED",
                senderId,
                receiverId,
                "has deleted the group " + chatName,
                "CHAT",
                chatId,
                metadata(chatId, chatName),
                System.currentTimeMillis()), ex);
    }

    private void publish(String key, NotificationEvent event) {
        try {
            kafkaTemplate.send(NOTIFICATION_TOPIC, key, event).get();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish chat notification event", ex);
        }
    }

    private void saveFailedNotification(String key, NotificationEvent event, Exception ex) {
        log.error("Failed to publish chat notification event for key={}", key, ex);
        FailedNotification failed = FailedNotification.builder()
                .topic(NOTIFICATION_TOPIC)
                .key(key)
                .event(event)
                .retryCount(0)
                .createdAt(Instant.now())
                .lastRetryAt(null)
                .failureReason(ex.getMessage())
                .build();
        mongoTemplate.save(failed);
    }

    private Map<String, String> metadata(String chatId, String chatName) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("chatId", chatId);
        metadata.put("chatName", chatName == null ? "" : chatName);
        return metadata;
    }
}
