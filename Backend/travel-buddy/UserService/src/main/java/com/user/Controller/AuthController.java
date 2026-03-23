package com.user.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.Config.JwtProvider;
import com.user.DTO.AuthResponse;
import com.user.DTO.ForgotPasswordRequest;
import com.user.DTO.LoginRequest;
import com.user.DTO.MessageResponse;
import com.user.DTO.RefreshRequest;
import com.user.DTO.RegisterRequest;
import com.user.DTO.ResetPasswordRequest;
import com.user.DTO.UserResponse;
import com.user.Service.UserService;
import com.user.ServiceImpl.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

        private final UserService userService;
        private JwtProvider jwtProvider;
        private final RefreshTokenService refreshTokenService;

        public AuthController(UserService userService, JwtProvider jwtProvider,
                        RefreshTokenService refreshTokenService) {
                this.userService = userService;
                this.jwtProvider = jwtProvider;
                this.refreshTokenService = refreshTokenService;
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
                return ResponseEntity.ok(new MessageResponse("Password reset successfully", "success"));
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {

                String refreshToken = request.getRefreshToken();

                // ✅ 1. Validate and get userId
                String userId = refreshTokenService.validate(refreshToken);

                System.out.println("Refresh token valid for userId=" + userId);

                // ✅ 2. Fetch user (for roles)
                UserResponse user = userService.getUserById(userId);

                List<String> roles = Optional.ofNullable(user.getRole())
                                .map(List::of)
                                .orElse(List.of("ROLE_USER"));

                // ✅ 3. Generate new access token
                String newAccessToken = jwtProvider.generateToken(
                                userId,
                                user.getEmail(),
                                roles);

                // ✅ 4. (OPTIONAL but recommended) rotate refresh token
                refreshTokenService.delete(refreshToken);

                String newRefreshToken = UUID.randomUUID().toString();
                refreshTokenService.save(newRefreshToken, userId);

                return ResponseEntity.ok(Map.of(
                                "accessToken", newAccessToken,
                                "refreshToken", newRefreshToken));
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {

                refreshTokenService.delete(request.getRefreshToken());

                return ResponseEntity.ok(
                                new MessageResponse("Logged out successfully", "success"));
        }
}
