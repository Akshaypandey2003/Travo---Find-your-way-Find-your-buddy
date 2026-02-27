package com.user.UnitTests.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.events.Repositories.FailedNotificationRepository;
import com.user.Config.JwtProvider;
import com.user.Controller.UserController;
import com.user.DTO.PageResponse;
import com.user.DTO.UpdateUserRequest;
import com.user.DTO.UserResponse;
import com.user.Exceptions.UserNotFoundException;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.PasswordResetTokenRepo;
import com.user.Repository.ProcessedFeedbackEventRepo;
import com.user.Repository.UserRepo;
import com.user.Service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("removal")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    
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

    // -----------------------------------------
    // updateUser SUCCESS
    // -----------------------------------------
    @Test
    @DisplayName("Should update user successfully")
    void updateUser_success() throws Exception {

        String userId = "user123";

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Akshay")
                .bio("Software Engineer")
                .build();

        UserResponse response = UserResponse.builder()
                .userId(userId)
                .name("Akshay")
                .bio("Software Engineer")
                .build();

       when(userService.updateUser(any(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/users")
                       .with(authentication(
            new UsernamePasswordAuthenticationToken(userId, null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.name").value("Akshay"))
                .andExpect(jsonPath("$.bio").value("Software Engineer"));
    }

    // -----------------------------------------
    // updateUser FAILURE
    // -----------------------------------------
    @Test
    @DisplayName("Should return 404 when updating non-existing user")
    void updateUser_userNotFound() throws Exception {

        String userId = "invalid";

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test")
                .build();

       when(userService.updateUser(any(), any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users")
                        .with(authentication(
            new UsernamePasswordAuthenticationToken(userId, null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------
    // getAllUsers SUCCESS
    // -----------------------------------------
    @Test
    @DisplayName("Should return paginated users")
    void getAllUsers_success() throws Exception {

        UserResponse user = UserResponse.builder()
                .userId("user1")
                .name("Akshay")
                .build();

        PageResponse<UserResponse> pageResponse =
                PageResponse.<UserResponse>builder()
                        .content(List.of(user))
                        .page(0)
                        .size(10)
                        .totalElements(1)
                        .totalPages(1)
                        .last(true)
                        .build();

        when(userService.getAllUsers(any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value("user1"))
                .andExpect(jsonPath("$.content[0].name").value("Akshay"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    // -----------------------------------------
    // getUserById SUCCESS
    // -----------------------------------------
    @Test
    @DisplayName("Should return user by ID")
    void getUserById_success() throws Exception {

        UserResponse response = UserResponse.builder()
                .userId("user123")
                .name("Akshay")
                .build();

        when(userService.getUserById("user123"))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/users/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.name").value("Akshay"));
    }

    // -----------------------------------------
    // getUserById FAILURE
    // -----------------------------------------
    @Test
    @DisplayName("Should return 404 if user not found")
    void getUserById_notFound() throws Exception {

        when(userService.getUserById("invalid"))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/invalid"))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------
    // getUserByPreferences SUCCESS
    // -----------------------------------------
    @Test
    @DisplayName("Should return users by preferences")
    void getUserByPreferences_success() throws Exception {

        UserResponse user = UserResponse.builder()
                .userId("user123")
                .name("Akshay")
                .build();

        PageResponse<UserResponse> response =
                PageResponse.<UserResponse>builder()
                        .content(List.of(user))
                        .page(0)
                        .size(10)
                        .totalElements(1)
                        .totalPages(1)
                        .last(true)
                        .build();

        when(userService.getUserByPreferences(any(Pageable.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users/get-user-by-preferences?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("travel"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value("user123"))
                .andExpect(jsonPath("$.content[0].name").value("Akshay"));
    }

    // -----------------------------------------
    // getUserByPreferences EMPTY RESULT
    // -----------------------------------------
    @Test
    @DisplayName("Should return empty list when no matching preferences")
    void getUserByPreferences_empty() throws Exception {

        PageResponse<UserResponse> response =
                PageResponse.<UserResponse>builder()
                        .content(List.of())
                        .page(0)
                        .size(10)
                        .totalElements(0)
                        .totalPages(0)
                        .last(true)
                        .build();

        when(userService.getUserByPreferences(any(Pageable.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users/get-user-by-preferences?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("unknown"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    // -----------------------------------------
    // deleteUser SUCCESS
    // -----------------------------------------
    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_success() throws Exception {

        doNothing().when(userService).deleteUser("user123");

        mockMvc.perform(delete("/api/v1/users/user123"))
                .andExpect(status().isNoContent());
    }

    // -----------------------------------------
    // deleteUser FAILURE
    // -----------------------------------------
    @Test
    @DisplayName("Should return 404 when deleting non-existing user")
    void deleteUser_notFound() throws Exception {

        doThrow(new UserNotFoundException("User not found"))
                .when(userService)
                .deleteUser("invalid");

        mockMvc.perform(delete("/api/v1/users/invalid"))
                .andExpect(status().isNotFound());
    }

}
