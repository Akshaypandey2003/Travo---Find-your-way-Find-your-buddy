package com.gateway.Controller;

import java.util.Set;
import java.util.TreeSet;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gateway.Entity.MessageResponse;
import com.gateway.Entity.Notification;
import com.gateway.Entity.Trip;
import com.gateway.Entity.User;

@RestController
@RequestMapping("/auth/user/trip")
public class TripController {

    @PostMapping("/create-trip")
    public ResponseEntity<?> createTrip(@RequestBody Trip trip) {
        try {
            // System.out.println("Creating trip for user with ID: " + userId);
            RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8090/auth/user/trip/create-trip";

            HttpEntity<Trip> entity = new HttpEntity<>(trip);

            ResponseEntity<Trip> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.POST, entity, Trip.class);

            Trip savedTrip = response.getBody();

            String userServiceUrl = "http://localhost:8088/user/get-user/" + savedTrip.getCreatedBy();

            ResponseEntity<User> userResponse = restTemplate.exchange(
                    userServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<User>() {
                    });
            User user = userResponse.getBody();
            Set<String> connections = new TreeSet<>();
            String isPrivateTrip = savedTrip.getIsPrivateTrip();
            if (isPrivateTrip.equalsIgnoreCase("true")) {
                for (String con : user.getCloseFriends())
                    connections.add(con);
            } else {
                for (String con : user.getFollowers())
                    connections.add(con);
                for (String con : user.getFollowing())
                    connections.add(con);
            }

            for (String con : connections) {
                if (!con.equals(user.getUserId())) {
                    try {
                        // Creating notification object
                        Notification notification = new Notification();
                        notification.setNotificationFrom(user.getUserId());
                        notification.setNotificationTo(con);
                        notification.setTripId(savedTrip.getTripId());
                        notification.setType(Notification.NotificationType.NEW_TRIP);
                        notification.setMessage("has planned a new trip " + savedTrip.getTripName());

                        // sending notification object to notification service(User service)
                        String notificationUrl = "http://localhost:8088/auth/user/notification/send-notification";

                        HttpEntity<Notification> notificatoinEntity = new HttpEntity<>(notification);

                        ResponseEntity<?> responseNotification = restTemplate.exchange(
                                notificationUrl, HttpMethod.POST, notificatoinEntity,
                                new ParameterizedTypeReference<Object>() {
                                });
                    } catch (Exception e) {
                        System.err.println("Failed to notify " + con + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("Response from Trip Service: " + response);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error creating trip: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-trip-by-tripId/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable String tripId)
    {
       try {
          RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8090/auth/user/trip/get-trip/" + tripId;

            ResponseEntity<Trip> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.GET, null,Trip.class);

        return ResponseEntity.ok(response.getBody());
       } catch (Exception e) {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Something went wrong while fetching trip");
        messageResponse.setStatus("error");

        return new ResponseEntity<>(messageResponse,HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
    @DeleteMapping("/delete-trip/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable String tripId) {
        try {
            // System.out.println("Creating trip for user with ID: " + userId);
            RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8090/auth/user/trip/delete-trip/" + tripId;

            ResponseEntity<?> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.DELETE, null, new ParameterizedTypeReference<Object>() {
                    });
            System.out.println("Response from Trip Service: " + response);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error deleting trip: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/send-trip-request/{tripId}/{requestFrom}/{requestTo}")
    public ResponseEntity<?> sendTripRequest(@PathVariable String tripId, @PathVariable String requestFrom,
            @PathVariable String requestTo) {
        try {

            // System.out.println("Requesting trip with ID: " + tripId + " from: " +
            // requestFrom + " to: " + requestTo);
            RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8090/auth/user/trip/send-trip-request/" + tripId + "/"
                    + requestFrom + "/"
                    + requestTo;

            // HttpHeaders headers = new HttpHeaders();
            // headers.set("Authorization", request.getHeader("Authorization"));
            // HttpEntity<Trip> entity = new HttpEntity<>(updatedUser);

            ResponseEntity<?> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.POST, null, new ParameterizedTypeReference<Object>() {
                    });
            System.out.println("Response from Trip Service: " + response);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error sending trip request: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/accept-trip-request/{notificationId}/{tripId}/{requestFrom}/{requestTo}")
    public ResponseEntity<?> acceptTripRequest(@PathVariable String notificationId, @PathVariable String tripId,
            @PathVariable String requestFrom, @PathVariable String requestTo) {
        try {

            System.out.println("Accepting trip request with ID: " + tripId + " from: " +
            requestFrom + " to: " + requestTo);
            RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8090/auth/user/trip/accept-trip-request/" + notificationId + "/"
                    + tripId + "/" + requestFrom + "/"
                    + requestTo;

            // HttpHeaders headers = new HttpHeaders();
            // headers.set("Authorization", request.getHeader("Authorization"));
            // HttpEntity<Trip> entity = new HttpEntity<>(updatedUser);

            ResponseEntity<?> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.POST, null, new ParameterizedTypeReference<Object>() {
                    });
            System.out.println("Response from Trip Service: " + response);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error sending trip request: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove-trip-member/{tripId}/{memberId}")
    ResponseEntity<Object> removeTripMember(@PathVariable String tripId, @PathVariable String memberId) {
        try {

            // System.out.println("Requesting trip with ID: " + tripId + " from: " +
            // requestFrom + " to: " + requestTo);
            RestTemplate restTemplate = new RestTemplate();

            String tripServiceUrl = "http://localhost:8090/auth/user/trip/remove-trip-member/" + tripId + "/"
                    + memberId;

            // HttpHeaders headers = new HttpHeaders();
            // headers.set("Authorization", request.getHeader("Authorization"));
            // HttpEntity<Trip> entity = new HttpEntity<>(updatedUser);

            ResponseEntity<?> response = restTemplate.exchange(
                    tripServiceUrl, HttpMethod.DELETE, null, new ParameterizedTypeReference<Object>() {
                    });
            System.out.println("Response from Trip Service: " + response);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error removing member from trip: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
