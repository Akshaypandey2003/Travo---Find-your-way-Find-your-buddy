package com.user.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.events.Trip.TripCreatedEvent;
import com.events.Trip.TripDeletedEvent;
import com.user.Repository.UserRepo;

@Component
public class TripEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TripEventConsumer.class);

    private final UserRepo userRepo;

    public TripEventConsumer(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.trip-events:trip-events}",
            groupId = "${spring.kafka.consumer.group-id:user-service}")
    public void consume(Object event) {
        try {
            if (event instanceof TripCreatedEvent createdEvent) {
                handleTripCreated(createdEvent);
                return;
            }

            if (event instanceof TripDeletedEvent deletedEvent) {
                handleTripDeleted(deletedEvent);
                return;
            }

            logger.debug("Ignoring unsupported trip event type: {}", event == null ? "null" : event.getClass().getName());
        } catch (Exception ex) {
            logger.error("Failed handling trip event: {}", event, ex);
            throw ex;
        }
    }

    private void handleTripCreated(TripCreatedEvent event) {
        if (event.getOwnerUserId() == null || event.getOwnerUserId().isBlank()) {
            logger.warn("TripCreatedEvent missing ownerUserId, tripId={}", event.getTripId());
            return;
        }
        userRepo.incrementTripsCount(event.getOwnerUserId());
        logger.info("Incremented tripsCount for ownerUserId={} tripId={}", event.getOwnerUserId(), event.getTripId());
    }

    private void handleTripDeleted(TripDeletedEvent event) {
        if (event.getOwnerUserId() == null || event.getOwnerUserId().isBlank()) {
            logger.warn("TripDeletedEvent missing ownerUserId, tripId={}", event.getTripId());
            return;
        }
        userRepo.decrementTripsCount(event.getOwnerUserId());
        logger.info("Decremented tripsCount for ownerUserId={} tripId={}", event.getOwnerUserId(), event.getTripId());
    }
}
