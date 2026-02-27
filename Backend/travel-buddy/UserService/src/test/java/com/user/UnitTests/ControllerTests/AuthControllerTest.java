package com.user.UnitTests.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.events.Repositories.FailedNotificationRepository;
import com.user.Config.JwtProvider;
import com.user.Controller.AuthController;
import com.user.DTO.*;
import com.user.Exceptions.InvalidPasswordException;
import com.user.Exceptions.InvalidResetTokenException;
import com.user.Exceptions.UserConflictException;
import com.user.Exceptions.UserNotFoundException;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.PasswordResetTokenRepo;
import com.user.Repository.ProcessedFeedbackEventRepo;
import com.user.Repository.UserRepo;
import com.user.Service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({ "removal", "unused" })
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private JwtProvider jwtProvider;

        @MockBean
        private UserRepo userRepo;

        @MockBean
        private ConnectionRepo connectionRepo;

        @MockBean
        private CloseFriendsRepo closeFriendsRepo;

        @MockBean
        private PasswordResetTokenRepo passwordResetTokenRepo;

        @MockBean
        private ProcessedFeedbackEventRepo processedFeedbackEventRepo;

        @MockBean
        private FailedNotificationRepository failedNotificationRepository;

        @Autowired
        private ObjectMapper objectMapper;

        // ========================================
        // REGISTER SUCCESS
        // ========================================

        @Test
        @DisplayName("Register user success")
        void register_success() throws Exception {

                RegisterRequest request = new RegisterRequest();
                request.setName("Akshay Pandey");
                request.setEmail("test@email.com");
                request.setPassword("password");
                request.setGender("male");

                MessageResponse message = new MessageResponse("User registered successfully", "success");

                AuthResponse response = AuthResponse.builder()
                                .accessToken("access-token")
                                .refreshToken("refresh-token")
                                .expire_at(System.currentTimeMillis() + 3600000)
                                .authorities(List.of("ROLE_USER"))
                                .messageReponse(message)
                                .build();

                when(userService.addUser(any(RegisterRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.accessToken").value("access-token"))
                                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"))
                                .andExpect(jsonPath("$.messageReponse.message")
                                                .value("User registered successfully"))
                                .andExpect(jsonPath("$.messageReponse.status")
                                                .value("success"));
        }

        // ========================================
        // REGISTER CONFLICT
        // ========================================

        @Test
        @DisplayName("Register user conflict")
        void register_conflict() throws Exception {

                RegisterRequest request = new RegisterRequest();
                request.setName("Akshay Pandey");
                request.setEmail("existing@email.com");
                request.setPassword("password");
                request.setGender("male");

                when(userService.addUser(request))
                                .thenThrow(new UserConflictException("User already exists"));

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        // ========================================
        // LOGIN SUCCESS
        // ========================================

        @Test
        @DisplayName("Login success")
        void login_success() throws Exception {

                LoginRequest request = new LoginRequest();
                request.setEmail("test@email.com");
                request.setPassword("password");

                MessageResponse message = new MessageResponse("User logged in successfully", "success");

                AuthResponse response = AuthResponse.builder()
                                .accessToken("access-token")
                                .refreshToken("refresh-token")
                                .expire_at(System.currentTimeMillis() + 3600000)
                                .authorities(List.of("ROLE_USER"))
                                .messageReponse(message)
                                .build();

                when(userService.generateToken(any(LoginRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("access-token"))
                                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"))
                                .andExpect(jsonPath("$.messageReponse.message")
                                                .value("User logged in successfully"))
                                .andExpect(jsonPath("$.messageReponse.status")
                                                .value("success"));
        }

        // ========================================
        // LOGIN USER NOT FOUND
        // ========================================

        @Test
        @DisplayName("Login user not found")
        void login_userNotFound() throws Exception {

                LoginRequest request = new LoginRequest();
                request.setEmail("notfound@email.com");
                request.setPassword("password");

                when(userService.generateToken(request))
                                .thenThrow(new UserNotFoundException("User not found"));

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        // ========================================
        // LOGIN INVALID PASSWORD
        // ========================================

        @Test
        @DisplayName("Login invalid password")
        void login_invalidPassword() throws Exception {

                LoginRequest request = new LoginRequest();
                request.setEmail("test@email.com");
                request.setPassword("wrongpassword");

                when(userService.generateToken(request))
                                .thenThrow(new InvalidPasswordException("Invalid password"));

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        // ========================================
        // FORGOT PASSWORD SUCCESS
        // ========================================

        @Test
        @DisplayName("Forgot password success")
        void forgotPassword_success() throws Exception {

                ForgotPasswordRequest request = new ForgotPasswordRequest();

                request.setEmail("test@email.com");

                doNothing()
                                .when(userService)
                                .forgotPassword("test@email.com");

                mockMvc.perform(post("/api/v1/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNoContent());
        }

        // ========================================
        // RESET PASSWORD SUCCESS
        // ========================================

        @Test
        @DisplayName("Reset password success")
        void resetPassword_success() throws Exception {

                ResetPasswordRequest request = new ResetPasswordRequest();

                request.setToken("valid-token");
                request.setPassword("newpassword");

                doNothing()
                                .when(userService)
                                .resetPassword(request);

                mockMvc.perform(post("/api/v1/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message")
                                                .value("Password reset successfully"))
                                .andExpect(jsonPath("$.status")
                                                .value("success"));
        }

        // ========================================
        // RESET PASSWORD INVALID TOKEN
        // ========================================

        @Test
        @DisplayName("Reset password invalid token")
        void resetPassword_invalidToken() throws Exception {

                ResetPasswordRequest request = new ResetPasswordRequest();

                request.setToken("invalid-token");
                request.setPassword("newpassword");

                doThrow(new InvalidResetTokenException("Invalid token"))
                                .when(userService)
                                .resetPassword(request);

                mockMvc.perform(post("/api/v1/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        // ========================================
        // VALIDATION FAILURE TEST
        // ========================================

        @Test
        void register_validationFailure() throws Exception {

                RegisterRequest request = new RegisterRequest();

                // Missing name, email, password

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

}
