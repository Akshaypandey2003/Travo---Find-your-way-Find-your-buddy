package com.trip.Services;

import java.util.List;
import java.util.Map;

import com.trip.Entity.Trip;

public interface TripServices {
    // Create a new trip
    Trip createTrip(Trip trip);
    Map<String,String> deleteTrip(String tripId);

    // Update a trip
    Trip updateTrip(String tripId, Trip tripDetails);

    // Get a trip by ID
    Trip getTripById(String tripId);

    // Get all trips
    List<Trip> getAllTrips();

    // Get trips by user ID
    List<Trip> getTripsByUserId(String userId);

    List<Trip> getTripsByCategory(String category);

    public Trip sendTripRequest(String tripId, String requestFrom, String requestTo);
    public Trip acceptTripRequest(String tripId, String requestFrom);
    public Trip removeTripMember(String tripId, String memberId);

}
