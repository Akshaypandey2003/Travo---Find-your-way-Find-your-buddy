package com.user.ServiceImpl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Notification.NotificationEvent;
import com.user.Entity.FailedNotification;
import com.user.Repository.FailedNotificationRepo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class UserNotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final FailedNotificationRepo failedNotificationRepo;

    private final Logger logger = LoggerFactory.getLogger(UserNotificationProducer.class);

    private final String NOTIFICATION_TOPIC="notification-events";

    public UserNotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate,
            FailedNotificationRepo failedNotificationRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedNotificationRepo = failedNotificationRepo;
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "sendFriendRequestFallback")
    @Retry(name = "notificationServiceRetry")
    public void friendRequestSend(String senderId, String receiverId) {

        try {
            NotificationEvent event = new NotificationEvent(
                    "FRIEND_REQUEST_SENT",
                    senderId,
                    receiverId,
                    "has sent you a friend request.",
                    "CONNECTION",
                    null,
                    null,
                    System.currentTimeMillis());

            kafkaTemplate.send(NOTIFICATION_TOPIC, receiverId, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println(
                                    "Kafka send success. Topic=" +
                                            result.getRecordMetadata().topic() +
                                            ", Partition=" +
                                            result.getRecordMetadata().partition() +
                                            ", Offset=" +
                                            result.getRecordMetadata().offset());
                        } else {
                            System.out.println("Kafka send failed: " + ex.getMessage());
                        }
                    }).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void sendFriendRequestFallback(
            String senderId,
            String receiverId,
            Exception ex) {

        logger.error(
                "FAILED to send friend request notification for userId={}. Reason={}",
                receiverId,
                ex.getMessage());
        NotificationEvent event = new NotificationEvent(
                "FRIEND_REQUEST_SENT",
                senderId,
                receiverId,
                "has sent you a friend request.",
                "CONNECTION",
                null,
                null,
                System.currentTimeMillis());

        saveFailedEvent(NOTIFICATION_TOPIC, receiverId, event);
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "acceptFriendRequestFallback")
    @Retry(name = "notificationServiceRetry")
    public void friendRequestAccept(String senderId, String receiverId) {

        try {
            NotificationEvent event = new NotificationEvent(
                    "FRIEND_REQUEST_ACCEPTED",
                    senderId,
                    receiverId,
                    "has accepted your friend request.",
                    "CONNECTION",
                    null,
                    null,
                    System.currentTimeMillis());

            kafkaTemplate.send(NOTIFICATION_TOPIC, receiverId, event).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void acceptFriendRequestFallback(
            String senderId,
            String receiverId,
            Exception ex) {

        logger.error(
                "FAILED to send friend request notification for userId={}. Reason={}",
                receiverId,
                ex.getMessage());
        NotificationEvent event = new NotificationEvent(
                "FRIEND_REQUEST_ACCEPTED",
                senderId,
                receiverId,
                "has accepted your friend request.",
                "CONNECTION",
                null,
                null,
                System.currentTimeMillis());

        saveFailedEvent(NOTIFICATION_TOPIC, receiverId, event);
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "resetPasswordFallback")
    @Retry(name = "notificationServiceRetry")
    public void sendPasswordResetNotification(
            String userId,
            String email,
            String name,
            String resetLink) {

        try {
            NotificationEvent event = new NotificationEvent(
                    "PASSWORD_RESET",
                    "SYSTEM",
                    userId,
                    "",
                    "USER",
                    userId,
                    Map.of(
                            "name", name,
                            "email", email,
                            "resetLink", resetLink),
                    System.currentTimeMillis());

            kafkaTemplate.send(NOTIFICATION_TOPIC, userId, event).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void resetPasswordFallback(
            String userId,
            String email,
            String name,
            String resetLink,
            Exception ex) {

        logger.error(
                "FAILED to send password reset notification for userId={}",
                userId,
                ex);
        NotificationEvent event = new NotificationEvent(
                "PASSWORD_RESET",
                "SYSTEM",
                userId,
                "",
                "USER",
                userId,
                Map.of(
                        "name", name,
                        "email", email,
                        "resetLink", resetLink),
                System.currentTimeMillis());

         saveFailedEvent(NOTIFICATION_TOPIC, userId, event);
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "welcomeFallback")
    @Retry(name = "notificationServiceRetry")
    public void sendWelcomeNotification(
            String userId,
            String name,
            String email) {

        try {
            NotificationEvent event = new NotificationEvent(
                    "WELCOME",
                    "SYSTEM",
                    userId,
                    "",
                    "USER",
                    userId,
                    Map.of("name", name,
                            "email", email),
                    System.currentTimeMillis());

            kafkaTemplate.send(NOTIFICATION_TOPIC, userId, event).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void welcomeFallback(
            String userId,
            String name,
            String email,
            Exception ex) {

        logger.error(
                "FAILED to send welcome notification for userId={}. Reason={} Saving for retry. userId={}",
                userId,
                ex.getMessage());

        logger.error("Kafka failed. Saving for retry. userId={}", userId);

        NotificationEvent event = new NotificationEvent(
                "WELCOME",
                "SYSTEM",
                userId,
                "",
                "USER",
                userId,
                Map.of("name", name, "email", email),
                System.currentTimeMillis());

        saveFailedEvent(NOTIFICATION_TOPIC, userId, event);

        // Optional: store in DB for retry later
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "resetSuccessFallback")
    @Retry(name = "notificationServiceRetry")
    public void sendPasswordResetSuccessNotification(
            String userId,
            String name,
            String email) {

        try {
            NotificationEvent event = new NotificationEvent(
                    "PASSWORD_RESET_SUCCESS",
                    "SYSTEM",
                    userId,
                    "",
                    "USER",
                    userId,
                    Map.of("name", name, "email", email),
                    System.currentTimeMillis());

            kafkaTemplate.send(NOTIFICATION_TOPIC, userId, event).get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void resetSuccessFallback(
            String userId,
            String name,
            String email,
            Exception ex) {

        logger.error(
                "FAILED to send reset success notification for userId={}",
                userId,
                ex);
        NotificationEvent event = new NotificationEvent(
                "PASSWORD_RESET_SUCCESS",
                "SYSTEM",
                userId,
                "",
                "USER",
                userId,
                Map.of("name", name, "email", email),
                System.currentTimeMillis());

        saveFailedEvent(NOTIFICATION_TOPIC, userId, event);
    }
      private void saveFailedEvent(String topic, String key, Object event) {

        FailedNotification failed = FailedNotification.builder()
                .topic(topic)
                .key(key)
                .event(event)
                .retryCount(0)
                .createdAt(System.currentTimeMillis())
                .build();

        failedNotificationRepo.save(failed);
    }
}
