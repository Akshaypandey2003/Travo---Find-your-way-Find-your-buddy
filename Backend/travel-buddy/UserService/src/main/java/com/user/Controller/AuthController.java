package com.user.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.DTO.AuthResponse;
import com.user.DTO.ForgotPasswordRequest;
import com.user.DTO.LoginRequest;
import com.user.DTO.MessageResponse;
import com.user.DTO.RegisterRequest;
import com.user.DTO.ResetPasswordRequest;
import com.user.Service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

        private final UserService userService;

        public AuthController(UserService userService) {
                this.userService = userService;
        }

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(
                        @Valid @RequestBody RegisterRequest request) {

                logger.info("Register request received for email: {}", request.getEmail());

                AuthResponse response = userService.addUser(request);

                logger.info("User registered successfully: {}", request.getEmail());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                logger.info("Login request received for email: {}",
                                request.getEmail());

                AuthResponse response = userService.generateToken(request);

                logger.info("Login successful for email: {}",
                                request.getEmail());

                return ResponseEntity.ok(response);
        }

        @PostMapping("/forget-password")
        public ResponseEntity<Void> forgotPassword(
                        @Valid @RequestBody ForgotPasswordRequest request) {

                userService.forgotPassword(request.getEmail());

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/reset-password")
        public ResponseEntity<MessageResponse> resetPassword(
                        @Valid @RequestBody ResetPasswordRequest request) {

                userService.resetPassword(request);
                return ResponseEntity.ok(new MessageResponse("Password reset successfully","success"));
        }

}
