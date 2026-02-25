package com.feedback.ServiceImpl;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Notification.FailedNotification;
import com.events.Notification.NotificationEvent;
import com.events.Repositories.FailedNotificationRepository;
import com.feedback.Service.NotificationProducer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class NotificationProducerImpl implements NotificationProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationProducerImpl.class);
    private static final String TOPIC = "notification-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailedNotificationRepository failedOutboundEventRepo;

    public NotificationProducerImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            FailedNotificationRepository failedOutboundEventRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedOutboundEventRepo = failedOutboundEventRepo;
    }

    @Override
    @CircuitBreaker(name = "feedbackNotificationCircuitBreaker", fallbackMethod = "sendCompanionReviewNotificationFallback")
    @Retry(name = "feedbackNotificationRetry")
    public void sendCompanionReviewNotification(String senderId, String receiverId, String tripId, String tripName) {
        NotificationEvent event = new NotificationEvent(
                "COMPANION_REVIEW_SUBMITTED",
                senderId,
                receiverId,
                "has shared a companion review after trip " + tripName,
                "FEEDBACK",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());
        publish(receiverId, event);
    }

    public void sendCompanionReviewNotificationFallback(
            String senderId,
            String receiverId,
            String tripId,
            String tripName,
            Exception ex) {
        logger.error("Failed to send companion-review notification senderId={} receiverId={} tripId={}",
                senderId, receiverId, tripId, ex);

        NotificationEvent event = new NotificationEvent(
                "COMPANION_REVIEW_SUBMITTED",
                senderId,
                receiverId,
                "has shared a companion review after trip " + tripName,
                "FEEDBACK",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());
        saveFailed(TOPIC, receiverId, event, ex.getMessage());
    }

    private void publish(String key, Object payload) {
        try {
            kafkaTemplate.send(TOPIC, key, payload).get();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish feedback notification", ex);
        }
    }

    private void saveFailed(String topic, String key, Object payload, String reason) {
        FailedNotification failed = FailedNotification.builder()
                .topic(topic)
                .key(key)
                .event(payload)
                .retryCount(0)
                .createdAt(Instant.now())
                .lastRetryAt(null)
                .failureReason(reason)
                .build();
        failedOutboundEventRepo.save(failed);
    }
}
