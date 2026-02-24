package com.trip.Controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trip.DTO.PageResponseDto;
import com.trip.DTO.TripRequestActionResponseDto;
import com.trip.DTO.TripRequestDto;
import com.trip.Entity.Trip;
import com.trip.Services.TripServices;

@RestController
@RequestMapping("/api/v1/trips")
@Validated
public class TripRequestController {

    private final TripServices tripServices;

    public TripRequestController(TripServices tripServices) {
        this.tripServices = tripServices;
    }

    @GetMapping("/{tripId}/requests/pending")
    public ResponseEntity<PageResponseDto<TripRequestDto>> getPendingTripRequests(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String tripId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "requestedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        enforceAuthenticated(authenticatedUserId);
        Trip trip = tripServices.getTripById(tripId);
        if (!authenticatedUserId.equals(trip.getTripOwnerId())) {
            throw new AccessDeniedException("Only trip owner can view pending requests.");
        }
        return ResponseEntity.ok(tripServices.getPendingTripRequests(tripId, page, size, sortBy, direction));
    }

    @PatchMapping("/{tripId}/requests/{requesterUserId}/reject")
    public ResponseEntity<TripRequestActionResponseDto> rejectTripRequest(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String tripId,
            @PathVariable String requesterUserId) {
        enforceAuthenticated(authenticatedUserId);
        Trip trip = tripServices.getTripById(tripId);
        if (!authenticatedUserId.equals(trip.getTripOwnerId())) {
            throw new AccessDeniedException("Only trip owner can reject trip requests.");
        }
        return ResponseEntity.ok(tripServices.rejectTripRequest(tripId, requesterUserId, authenticatedUserId));
    }

    @PatchMapping("/{tripId}/requests/{requesterUserId}/cancel")
    public ResponseEntity<TripRequestActionResponseDto> cancelTripRequest(
            @AuthenticationPrincipal String authenticatedUserId,
            @PathVariable String tripId,
            @PathVariable String requesterUserId) {
        enforceAuthenticated(authenticatedUserId);
        Trip trip = tripServices.getTripById(tripId);
        boolean isOwner = authenticatedUserId.equals(trip.getTripOwnerId());
        boolean isRequester = authenticatedUserId.equals(requesterUserId);
        if (!isOwner && !isRequester) {
            throw new AccessDeniedException("Only trip owner or requester can cancel this request.");
        }
        return ResponseEntity.ok(tripServices.cancelTripRequest(tripId, requesterUserId, authenticatedUserId));
    }

    private void enforceAuthenticated(String authenticatedUserId) {
        if (authenticatedUserId == null || authenticatedUserId.isBlank()) {
            throw new AccessDeniedException("Authenticated user is required.");
        }
    }
}
