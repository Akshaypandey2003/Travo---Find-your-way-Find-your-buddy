package com.gateway.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.gateway.Entity.MessageResponse;
import com.gateway.Entity.Notification;
import com.gateway.Entity.Trip;
import com.gateway.Entity.User;
import com.gateway.ErrorResponse.ErrorResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User updatedUser) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String userServiceUrl = "http://localhost:8088/user/update-user/" + userId;

            // HttpHeaders headers = new HttpHeaders();
            // headers.set("Authorization", request.getHeader("Authorization"));
            HttpEntity<User> entity = new HttpEntity<>(updatedUser);

            ResponseEntity<User> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.PUT, entity, User.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error updating user: " + e.getMessage(), "error");
            User user = new User();
            user.setMessageResponse(msg);
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            size = (int) (size + Math.pow(page, 2));
            String userServiceUrl = "http://localhost:8088/user/get-user?page=" + page + "&size=" + size;

            ResponseEntity<List<User>> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                    });

            List<User> users = response.getBody();

            if (users != null && !users.isEmpty()) {
                for (User user : users) {
                    String tripServiceUrl = "http://localhost:8090/auth/user/trip/get-trips-by-user/" + user.getUserId(); // Assume
                                                                                                                     // this
                                                                                                                     // endpoint
                                                                                                                     // exists
                    try {
                        ResponseEntity<List<Trip>> tripResponse = restTemplate.exchange(
                                tripServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Trip>>() {
                                });
                        user.setTrips(tripResponse.getBody());
                    } catch (Exception e) {
                        user.setTrips(new ArrayList<>()); // Empty trip list if failed
                    }
                }
            }
            System.out.println("Response from user+trip service: " + users);
            return new ResponseEntity<>(users, response.getStatusCode());

        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse();
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMsg("Something went wrong while fetching users");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/user/get-user/" + userId;

            ResponseEntity<User> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<User>() {
                    });

            User user = response.getBody();
            if (user != null) {
                String tripServiceUrl = "http://localhost:8090/auth/user/trip/get-trips-by-user/" + user.getUserId();
                try {
                    ResponseEntity<List<Trip>> tripResponse = restTemplate.exchange(
                            tripServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Trip>>() {
                            });
                    user.setTrips(tripResponse.getBody());
                } catch (Exception e) {
                    user.setTrips(new ArrayList<>()); // Empty trip list if failed
                }
            }
            return new ResponseEntity<>(user, response.getStatusCode());

        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching users: " + e.getMessage(), "error");
            User user = new User();
            user.setMessageResponse(msg);
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-notifications/{userId}")
    public ResponseEntity<Object> getNotifications(@PathVariable String userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/auth/user/notification/get-notifications/" + userId;

            ResponseEntity<Object> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<Object>() {
                    });

            Object notification = response.getBody();

            return new ResponseEntity<>(notification, response.getStatusCode());

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // ✅ Catching HTTP status error responses (e.g., 404, 500)
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());

        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching notifications: " + e.getMessage(), "error");

            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-user-by-preference")
    public ResponseEntity<List<User>> getUserByPreferences(@RequestBody List<String> preferences) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/user/get-user-by-preference/";

            HttpEntity<List<String>> entity = new HttpEntity<>(preferences);
            ResponseEntity<List<User>> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {
                    });

            List<User> users = response.getBody();
            return new ResponseEntity<>(users, response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching users: " + e.getMessage(), "error");
            List<User> users = new ArrayList<>();
            User user = new User();
            user.setMessageResponse(msg);
            users.add(user);
            return new ResponseEntity<>(users, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/accept-friend-request/{notificationId}/{senderId}/{receiverId}")
    public ResponseEntity<Notification> acceptFriendRequest(@PathVariable String notificationId,
            @PathVariable String senderId, @PathVariable String receiverId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/connection/accept/" + notificationId + "/" + senderId + "/"
                    + receiverId;

            ResponseEntity<Notification> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.POST, null, new ParameterizedTypeReference<Notification>() {
                    });

            Notification notification = response.getBody();
            MessageResponse msg = new MessageResponse("You started following " + notification.getSenderName(),
                    "success");
            notification.setMessageResponse(msg);
            return new ResponseEntity<>(notification, response.getStatusCode());

        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Something went wront while accepting request", "error");
            Notification notification = new Notification();
            notification.setMessageResponse(msg);
            return new ResponseEntity<>(notification, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-notification/{notificationId}")
    public ResponseEntity<Object> deleteFriendRequest(@PathVariable String notificationId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/auth/user/notification/delete/" + notificationId;

            ResponseEntity<String> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {
                    });

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse(
                    "Something went wrong while deleting notification: " + e.getMessage(), "error");

            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("updateLike/{userId}/{senderId}")
    public ResponseEntity<Object> updateLike(@PathVariable String userId, @PathVariable String senderId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/user/updateLike/" + userId + "/" + senderId;

            ResponseEntity<Object> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.PUT, null, new ParameterizedTypeReference<Object>() {
                    });
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error updating like: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addCloseFriend/{userId}/{friendId}")
    public ResponseEntity<Object> addCloseFriend(@PathVariable String userId, @PathVariable String friendId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8088/user/addCloseFriend/" + userId + "/" + friendId;

            ResponseEntity<Object> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.POST, null, new ParameterizedTypeReference<Object>() {
                    });
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error adding close friend: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
