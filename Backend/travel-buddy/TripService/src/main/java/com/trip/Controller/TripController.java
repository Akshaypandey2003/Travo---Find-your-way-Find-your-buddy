package com.trip.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.trip.Entity.Notification;
import com.trip.Entity.Trip;
import com.trip.Response.MessageResponse;
import com.trip.Services.TripServices;

@RestController
@RequestMapping("/auth/user/trip")
public class TripController {
    
    @Autowired
    private TripServices tripService;

    @PostMapping("/create-trip")
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip)
    {
        Trip createdTrip = tripService.createTrip(trip);
        return ResponseEntity.status(201).body(createdTrip);
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
            // System.out.println("Requesting trip with ID: " + tripId + " from: " + requestFrom + " to: " + requestTo);
            // Logic to send trip request
              
            //saving trip request in trip service
            Trip savedTrip = tripService.sendTripRequest(tripId, requestFrom,requestTo);
            System.out.println("Trip request sent successfully: " + savedTrip);
            

            //Creating notification object
            Notification notification = new Notification();
            notification.setNotificationFrom(requestFrom);
            notification.setNotificationTo(requestTo);
            notification.setTripId(tripId);
            notification.setType(Notification.NotificationType.TRIP_REQUEST);  
            notification.setMessage("Wants to join your "+savedTrip.getTripName()+" trip");
                   
           //sending notification object to notification service(User service)
            RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8088/auth/user/notification/send-notification";

            
            HttpEntity<Notification> entity = new HttpEntity<>(notification);

            ResponseEntity<?> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<Object>() {
                    });

            System.out.println("Notification sent successfully: " + response.getBody());
           
            return ResponseEntity.ok(MessageResponse.builder()
            .message("Trip request sent successfully")
            .status("success")
            .build() );

            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending trip request: " + e.getMessage());
        }
    }
    @PostMapping("/accept-trip-request/{notificationId}/{tripId}/{requestFrom}/{requestTo}")
    public ResponseEntity<?> acceptTripRequest(@PathVariable String notificationId,@PathVariable String tripId, @PathVariable String requestFrom, @PathVariable String requestTo) {
        try {
            // System.out.println("Requesting trip with ID: " + tripId + " from: " + requestFrom + " to: " + requestTo);
            // Logic to send trip request
              
            //saving trip request in trip service
            Trip savedTrip = tripService.acceptTripRequest(tripId, requestFrom);
            System.out.println("Trip request accepted successfully: " + savedTrip);
            

            //Creating notification object
            Notification notification = new Notification();
            //Change the order because we are sending notification for trip request accepted hence the sender and receiver will be altered
            notification.setNotificationFrom(requestTo);
            notification.setNotificationTo(requestFrom);
            notification.setTripId(tripId);
            notification.setType(Notification.NotificationType.ACCEPTED);  
            notification.setMessage("has accepted your request to join "+savedTrip.getTripName()+" trip");
                   
           //sending notification object to notification service(User service)
            RestTemplate restTemplate = new RestTemplate();

            String notificationServiceUrl = "http://localhost:8088/auth/user/notification/send-notification";
            String notificationServiceUrl2 = "http://localhost:8088/auth/user/notification/delete/"+notificationId;
            
            HttpEntity<Notification> entity = new HttpEntity<>(notification);

            ResponseEntity<?> response = restTemplate.exchange(
                    notificationServiceUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<Object>() {
                    });

              System.out.println("Notification sent successfully: " + response.getBody());
             System.out.println("Notification id is: "+notificationId);
             System.out.println("Is null: " + (notificationId == null));
            if(!notificationId.equals("null"))
            {
                ResponseEntity<String> response2 = restTemplate.exchange(
                    notificationServiceUrl2, HttpMethod.DELETE, null, String.class);
            }

            return ResponseEntity.ok(MessageResponse.builder()
            .message("Trip request accepted successfully")
            .status("success")
            .build() );

            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error accepting trip request: " + e.getMessage());
        }
    }

    @PutMapping("/update-trip/{tripId}")
    public ResponseEntity<Trip> updateTrip(@PathVariable String tripId, @RequestBody Trip tripDetails) {
        Trip updatedTrip = tripService.updateTrip(tripId, tripDetails);
        return ResponseEntity.ok(updatedTrip);
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
