package com.trip.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trip.DTO.PageResponseDto;
import com.trip.DTO.TripDeleteResponseDto;
import com.trip.DTO.TripListItemDto;
import com.trip.DTO.TripUpdateRequest;
import com.trip.Entity.Trip;
import com.trip.Response.MessageResponse;
import com.trip.Services.TripServices;

@RestController
@RequestMapping("/api/v1/trips")
@Validated
public class TripController {

    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

    private final TripServices tripService;

    public TripController(TripServices tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<Trip> createTrip(
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody Trip trip) 
            {
        enforceAuthenticated(authenticatedUserId);
        if (trip.getTripOwnerId() == null || trip.getTripOwnerId().isBlank()) {
            trip.setTripOwnerId(authenticatedUserId);
        } else if (!trip.getTripOwnerId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You cannot create a trip for another user.");
        }
        Trip createdTrip = tripService.createTrip(trip);
        logger.info("Created trip with ID={}", createdTrip.getTripId());
        return ResponseEntity.status(201).body(createdTrip);
    }

    @PutMapping
    public ResponseEntity<Trip> updateTrip(
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody TripUpdateRequest trip) {
        enforceAuthenticated(authenticatedUserId);

        Trip existingTrip = tripService.getTripById(trip.getTripId());
        
          if (!authenticatedUserId.equals(existingTrip.getTripOwnerId())) {
            throw new AccessDeniedException("You are not allowed to update this trip.");
        }
        logger.info("Updating trip with ID={}", trip.getTripId());
        Trip updatedTrip = tripService.updateTrip(authenticatedUserId, trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<TripDeleteResponseDto> deleteTrip(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String tripId) {
        enforceAuthenticated(authenticatedUserId);
        Trip trip = tripService.getTripById(tripId);
        if (!authenticatedUserId.equals(trip.getTripOwnerId())) {
            throw new AccessDeniedException("You are not allowed to delete this trip.");
        }
        TripDeleteResponseDto result = tripService.deleteTrip(tripId);
        return ResponseEntity.status(201).body(result);
    }

    @PostMapping("/send-trip-request/{tripId}/{requestFrom}")
    public ResponseEntity<?> sendTripRequest(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String tripId,
            @PathVariable String requestFrom) {
        try {
            enforceAuthenticated(authenticatedUserId);
            if (!authenticatedUserId.equals(requestFrom)) {
                throw new AccessDeniedException("Authenticated user and requestFrom do not match.");
            }
            Trip savedTrip = tripService.sendTripRequest(tripId, authenticatedUserId);
            logger.info("Trip request sent for tripId={} by userId={}", savedTrip.getTripId(), authenticatedUserId);

            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Trip request sent successfully")
                    .status("success")
                    .build());
        } catch (Exception e) {
            logger.error("Error sending trip request for tripId={} by userId={}", tripId, requestFrom, e);
            return ResponseEntity.status(500).body("Error sending trip request: " + e.getMessage());
        }
    }

    @PostMapping("/accept-trip-request/{notificationId}/{tripId}/{requestFrom}")
    public ResponseEntity<?> acceptTripRequest(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String notificationId,
            @PathVariable String tripId,
            @PathVariable String requestFrom) {
        try {
            enforceAuthenticated(authenticatedUserId);
            Trip trip = tripService.getTripById(tripId);
            if (!authenticatedUserId.equals(trip.getTripOwnerId())) {
                throw new AccessDeniedException("Only trip owner can accept trip requests.");
            }
            Trip savedTrip = tripService.acceptTripRequest(tripId, requestFrom, notificationId);
            logger.info("Trip request accepted for tripId={} by ownerId={}", savedTrip.getTripId(),
                    savedTrip.getTripOwnerId());

            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Trip request accepted successfully")
                    .status("success")
                    .build());
        } catch (Exception e) {
            logger.error("Error accepting trip request for tripId={} from userId={}", tripId, requestFrom, e);
            return ResponseEntity.status(500).body("Error accepting trip request: " + e.getMessage());
        }
    }

    @GetMapping("/get-trip/{tripId}")
    public ResponseEntity<Trip> getTripById(@PathVariable String tripId) {
        Trip trip = tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/get-all-trips")
    public ResponseEntity<PageResponseDto<TripListItemDto>> getAllTrips(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "tripCreatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        PageResponseDto<TripListItemDto> trips = tripService.getAllTrips(page, size, sortBy, direction);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/get-trips-by-user/{userId}")
    public ResponseEntity<PageResponseDto<TripListItemDto>> getTripsByUserId(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "tripCreatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        enforceAuthenticated(authenticatedUserId);
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access another user's trips.");
        }
        PageResponseDto<TripListItemDto> trips = tripService.getTripsByUserId(userId, page, size, sortBy, direction);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/get-trips-by-category/{category}")
    public ResponseEntity<PageResponseDto<TripListItemDto>> getTripsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "tripCreatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        PageResponseDto<TripListItemDto> trips = tripService.getTripsByCategory(category, page, size, sortBy,
                direction);
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/remove-trip-member/{tripId}/{memberId}")
    public ResponseEntity<Object> removeTripMember(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String tripId,
            @PathVariable String memberId) {
        enforceAuthenticated(authenticatedUserId);
        Trip existingTrip = tripService.getTripById(tripId);
        if (!authenticatedUserId.equals(existingTrip.getTripOwnerId()) && !authenticatedUserId.equals(memberId)) {
            throw new AccessDeniedException("Only trip owner or the same member can remove membership.");
        }
        Trip updatedTrip = tripService.removeTripMember(tripId, memberId);
        return ResponseEntity.ok(updatedTrip);
    }

    private void enforceAuthenticated(String authenticatedUserId) {
        if (authenticatedUserId == null || authenticatedUserId.isBlank()) {
            throw new AccessDeniedException("Authenticated user is required.");
        }
    }
}
