package com.user.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.events.Trip.TripCreatedEvent;
import com.events.Trip.TripDeletedEvent;
import com.user.Repository.UserRepo;

@Component
@KafkaListener(
        topics = "${app.kafka.topics.trip-events:trip-events}",
        groupId = "${spring.kafka.consumer.group-id:user-service}")
public class TripEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TripEventConsumer.class);

    private final UserRepo userRepo;
    private final CacheManager cacheManager;

    public TripEventConsumer(UserRepo userRepo, CacheManager cacheManager) {
        this.userRepo = userRepo;
        this.cacheManager = cacheManager;
    }

    @KafkaHandler
    private void handleTripCreated(TripCreatedEvent event) {
        if (event.getOwnerUserId() == null || event.getOwnerUserId().isBlank()) {
            logger.warn("TripCreatedEvent missing ownerUserId, tripId={}", event.getTripId());
            return;
        }
        userRepo.incrementTripsCount(event.getOwnerUserId());
        evictUserCache(event.getOwnerUserId());
        logger.info("Incremented tripsCount for ownerUserId={} tripId={}", event.getOwnerUserId(), event.getTripId());
    }

    @KafkaHandler
    private void handleTripDeleted(TripDeletedEvent event) {
        if (event.getOwnerUserId() == null || event.getOwnerUserId().isBlank()) {
            logger.warn("TripDeletedEvent missing ownerUserId, tripId={}", event.getTripId());
            return;
        }
        userRepo.decrementTripsCount(event.getOwnerUserId());
        evictUserCache(event.getOwnerUserId());
        logger.info("Decremented tripsCount for ownerUserId={} tripId={}", event.getOwnerUserId(), event.getTripId());
    }

    @KafkaHandler(isDefault = true)
    private void handleUnknown(Object event) {
        logger.warn("Ignoring unsupported trip event type={}", event == null ? "null" : event.getClass().getName());
    }

    private void evictUserCache(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            Cache cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict(userId);
                // Clear all user cache entries to avoid stale reads from alternate keys (e.g., email).
                cache.clear();
            }
        } catch (Exception ex) {
            logger.warn("Failed to evict user cache for userId={}: {}", userId, ex.getMessage());
        }
    }
}
