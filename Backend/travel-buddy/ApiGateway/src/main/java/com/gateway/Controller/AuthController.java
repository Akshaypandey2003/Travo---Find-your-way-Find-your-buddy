package com.gateway.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.gateway.Config.JwtProvider;
import com.gateway.DTO.UserDto;
import com.gateway.Entity.MessageResponse;
import com.gateway.Entity.Trip;
import com.gateway.Entity.User;
import com.gateway.Response.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    // private Logger logger = LoggerFactory.getLogger(AuthController.class);

    // @GetMapping("/login")
    // public ResponseEntity<AuthResponse> login(
    // @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
    // @AuthenticationPrincipal OidcUser user, Model model)
    // {
    // logger.info("User logged in email {}: " + user.getEmail());
    // AuthResponse authResponse = new AuthResponse();
    // authResponse.setUserId(user.getEmail());
    // authResponse.setAccessToken(client.getAccessToken().getTokenValue());
    // authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());
    // authResponse.setExpire_at(client.getAccessToken().getExpiresAt().getEpochSecond());

    // List<String> authorities =
    // user.getAuthorities().stream().map(grantedAuthority -> {
    // return grantedAuthority.getAuthority();
    // }).collect(Collectors.toList());

    // authResponse.setAuthorities(authorities);

    // return new ResponseEntity<>(authResponse, HttpStatus.OK);
    // }
    @Autowired
    private JwtProvider jwtProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDto userDto) {
        System.out.println("Registering user: " + userDto);

        // Encode the password before sending to User Microservice
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        // Forward request to User Microservice to save user details
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<User> response = restTemplate.postForEntity(
                    "http://localhost:8088/user/add-user",
                    userDto,
                    User.class);

            if (response.getStatusCode() == HttpStatus.CONFLICT) {
                MessageResponse msg = new MessageResponse("User already exists ! Try loggin in.", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.CONFLICT);
            } else if (response.getStatusCode() != HttpStatus.CREATED) {
                MessageResponse msg = new MessageResponse("Something went wrong ! please try again later.", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Assign role based on email
            List<String> roles = userDto.getEmail().endsWith("@admin.com") ? Arrays.asList("ROLE_ADMIN")
                    : Arrays.asList("ROLE_USER");

            // Generate JWT Token
            String token = jwtProvider.generateToken(userDto.getEmail(), roles);

            AuthResponse authResponse = new AuthResponse(
                    response.getBody(),
                    token,
                    "refresh_token",
                    System.currentTimeMillis() + 3600000,
                    roles,
                    new MessageResponse("User registered successfully", "success"));

            return ResponseEntity.ok(authResponse);

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                MessageResponse msg = new MessageResponse("User already exists ! Try loggin in.", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.CONFLICT);
            }
            MessageResponse msg = new MessageResponse("Something went wrong! Please try again later.", "error");
            return new ResponseEntity<>(AuthResponse.builder().messageReponse(msg).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserDto userDto) {
        // Send login request to User Microservice
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<User> response = restTemplate.postForEntity(
                    "http://localhost:8088/user/validate-user",
                    userDto,
                    User.class);

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

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                MessageResponse msg = new MessageResponse("User not found !", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.NOT_FOUND);
            } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                MessageResponse msg = new MessageResponse("Invalid credentials !", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
            } else if (response.getStatusCode() != HttpStatus.OK) {
                MessageResponse msg = new MessageResponse("Something went wrong ! please try again later.", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            System.out.println("User received from data base in apigateway is: " + response);

            User userFromDb = response.getBody();

            // Assign role based on email
            List<String> roles = Arrays.asList("ROLE_USER");

            // Generate JWT Token
            String token = jwtProvider.generateToken(userFromDb.getEmail(), roles);

            return ResponseEntity.ok(new AuthResponse(
                    userFromDb,
                    token,
                    "refresh_token",
                    System.currentTimeMillis() + 3600000,
                    roles,
                    new MessageResponse("User logged in successfully", "success")));
        } catch (

        HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                MessageResponse msg = new MessageResponse("User not found", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.NOT_FOUND);
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                MessageResponse msg = new MessageResponse("Invalid credentials", "error");
                AuthResponse response2 = new AuthResponse();
                response2.setMessageReponse(msg);
                return new ResponseEntity<>(response2, HttpStatus.UNAUTHORIZED);
            }
            MessageResponse msg = new MessageResponse("Something went wrong ! please try again later.", "error");
            AuthResponse response2 = new AuthResponse();
            response2.setMessageReponse(msg);
            return new ResponseEntity<>(response2, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User updatedUser) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String userServiceUrl = "http://localhost:8088/user/update-user/" + userId;

            ResponseEntity<User> response = restTemplate.exchange(
                    userServiceUrl, HttpMethod.PUT, new HttpEntity<>(updatedUser), User.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error updating user: " + e.getMessage(), "error");
            User user = new User();
            user.setMessageResponse(msg);
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
