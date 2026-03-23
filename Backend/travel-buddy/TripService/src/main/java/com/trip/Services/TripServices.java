package com.trip.Services;

import java.util.List;

import org.checkerframework.checker.units.qual.t;

import com.trip.DTO.PageResponseDto;
import com.trip.DTO.TripDeleteResponseDto;
import com.trip.DTO.TripListItemDto;
import com.trip.DTO.TripRequestActionResponseDto;
import com.trip.DTO.TripRequestDto;
import com.trip.DTO.TripUpdateRequest;
import com.trip.Entity.Trip;
import com.trip.Entity.TripMember;

public interface TripServices {
    // Create a new trip
    Trip createTrip(Trip trip);
    TripDeleteResponseDto deleteTrip(String tripId);

    // Update a trip
    Trip updateTrip(String authenticatedUserId, TripUpdateRequest trip);

    // Get a trip by ID
    Trip getTripById(String tripId);

    // Get all trips
    PageResponseDto<TripListItemDto> getAllTrips(int page, int size, String sortBy, String direction);

    // Get trips by user ID
    PageResponseDto<TripListItemDto> getTripsByUserId(String userId, int page, int size, String sortBy, String direction);

    PageResponseDto<TripListItemDto> getTripsByCategory(String category, int page, int size, String sortBy, String direction);

    public Trip sendTripRequest(String tripId, String requestFrom);
    public Trip acceptTripRequest(String tripId, String requestFrom, String notificationId);
    public PageResponseDto<TripRequestDto> getPendingTripRequests(String tripId, int page, int size, String sortBy, String direction);
    public TripRequestActionResponseDto rejectTripRequest(String tripId, String requesterUserId, String actionByUserId);
    public TripRequestActionResponseDto cancelTripRequest(String tripId, String requesterUserId, String actionByUserId);
    public Trip addTripMember(String tripId, List<TripMember> members);
    public Trip removeTripMember(String tripId, String memberId);
    public void removeTrip(String userId);

}
