package com.trip.InternalController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trip.DTO.TripSummary;
import com.trip.Entity.Trip;
import com.trip.Services.TripServices;

@RestController
@RequestMapping("/trip/internal")
public class TripInternalController {
    
    private final TripServices tripServices;

    public TripInternalController(TripServices tripServices) {
        this.tripServices = tripServices;
    }

    @GetMapping("/summary/{tripId}")
    public ResponseEntity<?> getTripSummaryById(@PathVariable String tripId) {

        Trip trip = tripServices.getTripById(tripId);

        if (trip != null) {
            TripSummary summary = TripSummary.builder()
                    .tripId(trip.getTripId())
                    .tripName(trip.getTripName())
                    .members(trip.getTripMembers())
                    .build();

            return ResponseEntity.ok(summary);
        }
        return null;
    }

}
