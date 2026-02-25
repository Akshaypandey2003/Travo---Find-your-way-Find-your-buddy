package com.trip.ServiceImpl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Notification.NotificationDeleteEvent;
import com.events.Trip.TripCreatedEvent;
import com.events.Trip.TripDeletedEvent;
import com.events.Trip.TripRequestAcceptedEvent;
import com.events.Trip.TripRequestCancelledEvent;
import com.events.Trip.TripRequestCreatedEvent;
import com.events.Trip.TripRequestRejectedEvent;
import com.events.Trip.TripUpdatedEvent;
import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;
import com.trip.Entity.Trip;
import com.trip.Services.TripDomainEventPublisher;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class TripDomainEventPublisherImpl implements TripDomainEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TripDomainEventPublisherImpl.class);
    private static final String TOPIC = "trip-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailedNotificationRepository failedNotificationRepository;

    public TripDomainEventPublisherImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            FailedNotificationRepository failedNotificationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedNotificationRepository = failedNotificationRepository;
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripCreatedFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripCreated(Trip trip) {
        TripCreatedEvent event = TripCreatedEvent.builder()
                .tripId(trip.getTripId())
                .ownerUserId(trip.getTripOwnerId())
                .tripName(trip.getTripName())
                .tripStartDate(trip.getTripStartDate())
                .tripEndDate(trip.getTripEndDate())
                .memberCount(trip.getTripMembers() == null ? 0 : trip.getTripMembers().size())
                .pendingRequestCount(trip.getPendingRequestCount())
                .timestamp(System.currentTimeMillis())
                .build();
        publish(trip.getTripId(), event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripUpdatedFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripUpdated(Trip trip, Map<String, Object> updatedFields) {
        TripUpdatedEvent event = TripUpdatedEvent.builder()
                .tripId(trip.getTripId())
                .ownerUserId(trip.getTripOwnerId())
                .updatedFields(updatedFields)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(trip.getTripId(), event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripDeletedFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripDeleted(String tripId, String ownerUserId) {
        TripDeletedEvent event = TripDeletedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(tripId, event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripRequestCreatedFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripRequestCreated(String tripId, String ownerUserId, String requesterUserId) {
        TripRequestCreatedEvent event = TripRequestCreatedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(tripId, event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripRequestAcceptedFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripRequestAccepted(String tripId, String ownerUserId, String requesterUserId) {
        TripRequestAcceptedEvent event = TripRequestAcceptedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(tripId, event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripRequestRejectedFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripRequestRejected(String tripId, String ownerUserId, String requesterUserId) {
        TripRequestRejectedEvent event = TripRequestRejectedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(tripId, event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "tripRequestCancelledFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishTripRequestCancelled(String tripId, String ownerUserId, String requesterUserId) {
        TripRequestCancelledEvent event = TripRequestCancelledEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(tripId, event);
    }

    @Override
    @CircuitBreaker(name = "tripDomainEventCircuitBreaker", fallbackMethod = "publishNotificationDeleteEventFallback")
    @Retry(name = "tripDomainEventRetry")
    public void publishNotificationDeleteEvent(String fromUserId, String notificationId) {
        NotificationDeleteEvent event = NotificationDeleteEvent.builder()
                .type("NOTIFICATION_DELETE")
                .from(fromUserId)
                .notificationId(notificationId)
                .timestamp(System.currentTimeMillis())
                .build();

        publish(notificationId, event);
    }
    
    public void tripCreatedFallback(Trip trip, Exception ex) {
        logger.error("Failed to publish TripCreatedEvent for tripId={}", trip.getTripId(), ex);
        TripCreatedEvent event = TripCreatedEvent.builder()
                .tripId(trip.getTripId())
                .ownerUserId(trip.getTripOwnerId())
                .tripName(trip.getTripName())
                .tripStartDate(trip.getTripStartDate())
                .tripEndDate(trip.getTripEndDate())
                .memberCount(trip.getTripMembers() == null ? 0 : trip.getTripMembers().size())
                .pendingRequestCount(trip.getPendingRequestCount())
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, trip.getTripId(), event, ex);
    }

    public void tripUpdatedFallback(Trip trip, Map<String, Object> updatedFields, Exception ex) {
        logger.error("Failed to publish TripUpdatedEvent for tripId={}", trip.getTripId(), ex);
        TripUpdatedEvent event = TripUpdatedEvent.builder()
                .tripId(trip.getTripId())
                .ownerUserId(trip.getTripOwnerId())
                .updatedFields(updatedFields)
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, trip.getTripId(), event, ex);
    }

    public void tripDeletedFallback(String tripId, String ownerUserId, Exception ex) {
        logger.error("Failed to publish TripDeletedEvent for tripId={}", tripId, ex);
        TripDeletedEvent event = TripDeletedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, tripId, event, ex);
    }

    public void tripRequestCreatedFallback(String tripId, String ownerUserId, String requesterUserId, Exception ex) {
        logger.error("Failed to publish TripRequestCreatedEvent for tripId={} requester={}", tripId, requesterUserId, ex);
        TripRequestCreatedEvent event = TripRequestCreatedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, tripId, event, ex);
    }

    public void tripRequestAcceptedFallback(String tripId, String ownerUserId, String requesterUserId, Exception ex) {
        logger.error("Failed to publish TripRequestAcceptedEvent for tripId={} requester={}", tripId, requesterUserId, ex);
        TripRequestAcceptedEvent event = TripRequestAcceptedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, tripId, event, ex);
    }

    public void tripRequestRejectedFallback(String tripId, String ownerUserId, String requesterUserId, Exception ex) {
        logger.error("Failed to publish TripRequestRejectedEvent for tripId={} requester={}", tripId, requesterUserId, ex);
        TripRequestRejectedEvent event = TripRequestRejectedEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, tripId, event, ex);
    }

    public void tripRequestCancelledFallback(String tripId, String ownerUserId, String requesterUserId, Exception ex) {
        logger.error("Failed to publish TripRequestCancelledEvent for tripId={} requester={}", tripId, requesterUserId, ex);
        TripRequestCancelledEvent event = TripRequestCancelledEvent.builder()
                .tripId(tripId)
                .ownerUserId(ownerUserId)
                .requesterUserId(requesterUserId)
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailedEvent(TOPIC, tripId, event, ex);
    }

    public void publishNotificationDeleteEventFallback(
            String fromUserId,
            String notificationId,
            Exception ex) {
        logger.error("Failed to publish notification-delete event fromUserId={} notificationId={}", fromUserId, notificationId, ex);
        saveFailedEvent(TOPIC, notificationId, new NotificationDeleteEvent(
                "NOTIFICATION_DELETE",
                fromUserId,
                notificationId,
                System.currentTimeMillis()), ex);
    }



    private void publish(String key, Object event) {
        try {
            kafkaTemplate.send(TOPIC, key, event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish trip domain event", e);
        }
    }

    private void saveFailedEvent(String topic, String key, Object event, Exception ex) {
        FailedNotification failed = FailedNotification.builder()
                .topic(topic)
                .key(key)
                .event(event)
                .retryCount(0)
                .createdAt(System.currentTimeMillis())
                .lastRetryAt(0L)
                .failureReason(ex.getMessage())
                .build();
        failedNotificationRepository.save(failed);
    }

    
}
