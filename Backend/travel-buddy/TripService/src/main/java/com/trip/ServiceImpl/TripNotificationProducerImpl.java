package com.trip.ServiceImpl;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Entity.NotificationEvent;
import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;
import com.trip.Services.TripNotificationProducer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class TripNotificationProducerImpl implements TripNotificationProducer {

    private static final Logger logger = LoggerFactory.getLogger(TripNotificationProducerImpl.class);
    private static final String NOTIFICATION_TOPIC = "notification-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailedNotificationRepository failedNotificationRepository;

    public TripNotificationProducerImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            FailedNotificationRepository failedNotificationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedNotificationRepository = failedNotificationRepository;
    }

    @Override
    @CircuitBreaker(name = "tripNotificationCircuitBreaker", fallbackMethod = "sendNewTriptNotificationFallback")
    @Retry(name = "tripNotificationRetry")
    public void sendNewTriptNotification(String senderId, String tripId, String tripName) {
        NotificationEvent event = new NotificationEvent(
                "TRIP_CREATED",
                senderId,
                "SYSTEM",
                "has planned a new trip " + tripName,
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());

        sendEvent(senderId, event);
    }

    @Override
    @CircuitBreaker(name = "tripNotificationCircuitBreaker", fallbackMethod = "sendTripRequestFallback")
    @Retry(name = "tripNotificationRetry")
    public void sendTripRequestNotification(String senderId, String receiverId, String tripId, String tripName) {
        NotificationEvent event = new NotificationEvent(
                "TRIP_REQUEST_SENT",
                senderId,
                receiverId,
                "has requested to join your trip " + tripName,
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());

        sendEvent(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "tripNotificationCircuitBreaker", fallbackMethod = "acceptTripRequestFallback")
    @Retry(name = "tripNotificationRetry")
    public void acceptTripRequestNotification(String senderId, String receiverId, String tripId, String tripName) {
        NotificationEvent event = new NotificationEvent(
                "TRIP_REQUEST_ACCEPTED",
                senderId,
                receiverId,
                "has accepted your trip request",
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());

        sendEvent(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "tripNotificationCircuitBreaker", fallbackMethod = "tripStartReminderFallback")
    @Retry(name = "tripNotificationRetry")
    public void sendTripStartReminderNotification(String receiverId, String tripId, String tripName) {
        NotificationEvent event = new NotificationEvent(
                "TRIP_START_REMINDER",
                "SYSTEM",
                receiverId,
                "Reminder: Your trip starts tomorrow!",
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());

        sendEvent(receiverId, event);
    }

    @Override
    @CircuitBreaker(name = "tripNotificationCircuitBreaker", fallbackMethod = "tripEndReminderFallback")
    @Retry(name = "tripNotificationRetry")
    public void sendTripEndReminderNotification(String receiverId, String tripId, String tripName) {
        NotificationEvent event = new NotificationEvent(
                "TRIP_END_REMINDER",
                "SYSTEM",
                receiverId,
                "Reminder: Your trip ends today!",
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis());

        sendEvent(receiverId, event);
    }

    public void sendNewTriptNotificationFallback(
            String senderId,
            String tripId,
            String tripName,
            Exception ex) {
        logger.error("Failed to publish new trip notification for senderId={} tripId={}", senderId, tripId, ex);
        saveFailedNotification(senderId, new NotificationEvent(
                "TRIP_CREATED",
                senderId,
                "SYSTEM",
                "has planned a new trip " + tripName,
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis()), ex);
    }
    public void sendTripRequestFallback(
            String senderId,
            String receiverId,
            String tripId,
            String tripName,
            Exception ex) {
        logger.error("Failed to publish trip request notification for receiverId={} tripId={}", receiverId, tripId, ex);
        saveFailedNotification(receiverId, new NotificationEvent(
                "TRIP_REQUEST_SENT",
                senderId,
                receiverId,
                "has requested to join your trip " + tripName,
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis()), ex);
    }

    public void acceptTripRequestFallback(
            String senderId,
            String receiverId,
            String tripId,
            String tripName,
            Exception ex) {
        logger.error("Failed to publish trip request accepted notification for receiverId={} tripId={}", receiverId, tripId, ex);
        saveFailedNotification(receiverId, new NotificationEvent(
                "TRIP_REQUEST_ACCEPTED",
                senderId,
                receiverId,
                "has accepted your trip request",
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis()), ex);
    }

    public void tripStartReminderFallback(
            String receiverId,
            String tripId,
            String tripName,
            Exception ex) {
        logger.error("Failed to publish trip start reminder for receiverId={} tripId={}", receiverId, tripId, ex);
        saveFailedNotification(receiverId, new NotificationEvent(
                "TRIP_START_REMINDER",
                "SYSTEM",
                receiverId,
                "Reminder: Your trip starts tomorrow!",
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis()), ex);
    }

    public void tripEndReminderFallback(
            String receiverId,
            String tripId,
            String tripName,
            Exception ex) {
        logger.error("Failed to publish trip end reminder for receiverId={} tripId={}", receiverId, tripId, ex);
        saveFailedNotification(receiverId, new NotificationEvent(
                "TRIP_END_REMINDER",
                "SYSTEM",
                receiverId,
                "Reminder: Your trip ends today!",
                "TRIP",
                tripId,
                Map.of("tripId", tripId, "tripName", tripName),
                System.currentTimeMillis()), ex);
    }

    private void sendEvent(String receiverId, NotificationEvent event) {
        try {
            kafkaTemplate.send(NOTIFICATION_TOPIC, receiverId, event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish Kafka notification event", e);
        }
    }

    private void saveFailedNotification(String key, NotificationEvent event, Exception ex) {
        FailedNotification failed = FailedNotification.builder()
                .topic(NOTIFICATION_TOPIC)
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
