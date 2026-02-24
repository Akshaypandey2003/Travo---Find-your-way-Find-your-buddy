package com.trip.Services;

import java.util.Map;

import com.trip.Entity.Trip;

public interface TripDomainEventPublisher {
    void publishTripCreated(Trip trip);

    void publishTripUpdated(Trip trip, Map<String, Object> updatedFields);

    void publishTripDeleted(String tripId, String ownerUserId);

    void publishTripRequestCreated(String tripId, String ownerUserId, String requesterUserId);

    void publishTripRequestAccepted(String tripId, String ownerUserId, String requesterUserId);

    void publishTripRequestRejected(String tripId, String ownerUserId, String requesterUserId);

    void publishTripRequestCancelled(String tripId, String ownerUserId, String requesterUserId);

    void publishNotificationDeleteEvent(String fromUserId, String notificationId);
}
