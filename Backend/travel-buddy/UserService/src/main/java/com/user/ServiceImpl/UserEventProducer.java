package com.user.ServiceImpl;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;
import com.events.User.UserCreatedEvent;
import com.events.User.UserDeletedEvent;
import com.events.User.UserUpdatedEvent;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class UserEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailedNotificationRepository failedNotificationRepo;

    private static final String USER_EVENTS_TOPIC = "user-events";

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
            FailedNotificationRepository failedNotificationRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedNotificationRepo = failedNotificationRepo;
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "publishUserCreatedFallback")
    @Retry(name = "notificationServiceRetry")
    public void publishUserCreated(String userId, String username, String email) {

        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .userId(userId)
                    .username(username)
                    .email(email)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaTemplate.send(USER_EVENTS_TOPIC, userId, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("UserCreatedEvent published successfully for userId=" + userId);
                        } else {
                            System.err.println("Failed to publish UserCreatedEvent: " + ex.getMessage());
                        }
                    }).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void publishUserCreatedFallback(String userId, String username, String email, Exception ex) {

        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .timestamp(System.currentTimeMillis())
                .build();

        saveFailedEvent(USER_EVENTS_TOPIC, userId, event);

    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "publishUserDeletedFallback")
    @Retry(name = "notificationServiceRetry")
    public void publishUserDeleted(String userId) {

        try {
            UserDeletedEvent event = UserDeletedEvent.builder()
                    .userId(userId)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaTemplate.send(USER_EVENTS_TOPIC, userId, event).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void publishUserDeletedFallback(String userId, Exception ex) {

        UserDeletedEvent event = UserDeletedEvent.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .build();

        saveFailedEvent(USER_EVENTS_TOPIC, userId, event);
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "publishUserUpdatedFallback")
    @Retry(name = "notificationServiceRetry")
    public void publishUserUpdated(String userId, java.util.Map<String, Object> updatedFields) {

        try {
            UserUpdatedEvent event = UserUpdatedEvent.builder()
                    .userId(userId)
                    .updatedFields(updatedFields)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaTemplate.send(USER_EVENTS_TOPIC, userId, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("UserUpdatedEvent published successfully for userId=" + userId);
                        } else {
                            System.err.println("Failed to publish UserUpdatedEvent: " + ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void publishUserUpdatedFallback(String userId, Map<String, Object> updatedFields, Exception ex) {

        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId(userId)
                .updatedFields(updatedFields)
                .timestamp(System.currentTimeMillis())
                .build();

        saveFailedEvent(USER_EVENTS_TOPIC, userId, event);
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
