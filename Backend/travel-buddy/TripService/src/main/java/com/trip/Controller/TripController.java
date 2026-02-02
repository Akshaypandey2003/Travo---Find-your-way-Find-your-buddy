package com.trip.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trip.Entity.Trip;
import com.trip.Response.MessageResponse;
import com.trip.Services.TripServices;

@RestController
@RequestMapping("/trip")
public class TripController {
    
    @Autowired
    private TripServices tripService;

    @PostMapping("/create-trip")
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip)
    {
        Trip createdTrip = tripService.createTrip(trip);
        System.out.println("Created Trip: " + createdTrip);
        return ResponseEntity.status(201).body(createdTrip);
    }
     @PutMapping("/update-trip")
    public ResponseEntity<Trip> updateTrip(@RequestBody Trip trip) {
         System.out.println("Updating trip with ID: " + trip.getTripId());
        Trip updatedTrip = tripService.updateTrip(trip);
        return ResponseEntity.ok(updatedTrip);
    }
    @DeleteMapping("/delete-trip/{tripId}")
    public ResponseEntity<Map<String,String>> createTrip(@PathVariable String tripId)
    {
        Map<String,String>result = tripService.deleteTrip(tripId);
        return ResponseEntity.status(201).body(result);
    }
    
    @PostMapping("/send-trip-request/{tripId}/{requestFrom}/{requestTo}")
    public ResponseEntity<?> sendTripRequest(@PathVariable String tripId, @PathVariable String requestFrom, @PathVariable String requestTo) {
        try {
            Trip savedTrip = tripService.sendTripRequest(tripId, requestFrom,requestTo);

            System.out.println("Trip request sent successfully: " + savedTrip);
           
            return ResponseEntity.ok(MessageResponse.builder()
            .message("Trip request sent successfully")
            .status("success")
            .build() );

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending trip request: " + e.getMessage());
        }
    }
    
    
    @SuppressWarnings("null")
    @PostMapping("/accept-trip-request/{notificationId}/{tripId}/{requestFrom}/{requestTo}")
    public ResponseEntity<?> acceptTripRequest(@PathVariable String notificationId,@PathVariable String tripId, @PathVariable String requestFrom, @PathVariable String requestTo) {
        try {
           
            Trip savedTrip = tripService.acceptTripRequest(tripId, requestFrom, requestTo);
            System.out.println("Trip request accepted successfully: " + savedTrip);
            
            return ResponseEntity.ok(MessageResponse.builder()
            .message("Trip request accepted successfully")
            .status("success")
            .build() );

            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error accepting trip request: " + e.getMessage());
        }
    }

    @GetMapping("/get-trip/{tripId}")
    public ResponseEntity<Trip> getTripById(@PathVariable String tripId) {
        Trip trip = tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/get-all-trips")
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/get-trips-by-user/{userId}")
    public ResponseEntity<List<Trip>> getTripsByUserId(@PathVariable String userId) {
        List<Trip> trips = tripService.getTripsByUserId(userId);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/get-trips-by-category/{category}")
    public ResponseEntity<List<Trip>> getTripsByCategory(@PathVariable String category) {
        List<Trip> trips = tripService.getTripsByCategory(category);
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/remove-trip-member/{tripId}/{memberId}")
    ResponseEntity<Object> removeTripMember(@PathVariable String tripId, @PathVariable String memberId)
    {
        Trip trip = tripService.removeTripMember(tripId,memberId);
        return ResponseEntity.ok(trip);
    }
}
