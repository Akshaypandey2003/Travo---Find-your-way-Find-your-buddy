package com.user.UnitTests.ControllerTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.events.Repositories.FailedNotificationRepository;
import com.user.Config.JwtProvider;
import com.user.Controller.ConnectionController;
import com.user.DTO.CloseFriendResponse;
import com.user.DTO.ConnectionResponse;
import com.user.DTO.PageResponse;
import com.user.DTO.UserSummary;
import com.user.Enum.ConnectionStatus;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.PasswordResetTokenRepo;
import com.user.Repository.ProcessedFeedbackEventRepo;
import com.user.Repository.UserRepo;
import com.user.Service.ConnectionService;

@WebMvcTest(ConnectionController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({ "removal", "unused" })
class ConnectionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ConnectionService connectionService;

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

        private final String USER_ID = "user1";
        private final String OTHER_USER_ID = "user2";

        private UsernamePasswordAuthenticationToken auth() {
                return new UsernamePasswordAuthenticationToken(USER_ID, null);
        }

        // ===============================
        // FOLLOW USER
        // ===============================

        @Test
        @DisplayName("followUser success")
        void followUser_success() throws Exception {

                ConnectionResponse response = ConnectionResponse.builder()
                                .requestFrom(USER_ID)
                                .requestTo(OTHER_USER_ID)
                                .status(ConnectionStatus.REQUESTED)
                                .build();

                when(connectionService.sendFollowRequest(any(), any()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/connections/follow/{userId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.requestFrom").value(USER_ID))
                                .andExpect(jsonPath("$.requestTo").value(OTHER_USER_ID))
                                .andExpect(jsonPath("$.status").value("REQUESTED"));
        }

        @Test
        @DisplayName("followUser failure")
        void followUser_failure() throws Exception {

                when(connectionService.sendFollowRequest(any(), any()))
                                .thenThrow(new RuntimeException("User not found"));

                mockMvc.perform(post("/api/v1/connections/follow/{userId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isInternalServerError());
        }

        // ===============================
        // ACCEPT FOLLOW REQUEST
        // ===============================
        @Test
        @DisplayName("accept follow request success")
        void acceptFollowRequest_success() throws Exception {

                ConnectionResponse response = ConnectionResponse.builder()
                                .requestFrom(OTHER_USER_ID)
                                .requestTo(USER_ID)
                                .status(ConnectionStatus.FOLLOWING)
                                .build();

                when(connectionService.acceptFollowRequest(any(), any()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/connections/accept/{followerId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("FOLLOWING"));
        }

        
        @Test
        void acceptFollowRequest_failure() throws Exception {

                when(connectionService.acceptFollowRequest(any(), any()))
                                .thenThrow(new RuntimeException("Request not found"));

                mockMvc.perform(post("/api/v1/connections/accept/{followerId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isInternalServerError());
        }

        // ===============================
        // REJECT FOLLOW REQUEST
        // ===============================

        @Test
        void rejectFollowRequest_success() throws Exception {

                doNothing().when(connectionService)
                                .rejectFollowRequest(USER_ID, OTHER_USER_ID);

                mockMvc.perform(delete("/api/v1/connections/reject/{followerId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Follow request rejected"));
        }

        @Test
        void rejectFollowRequest_failure() throws Exception {

                doThrow(new RuntimeException("Request not found"))
                                .when(connectionService)
                                .rejectFollowRequest(any(), any());

                mockMvc.perform(delete("/api/v1/connections/reject/{followerId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isInternalServerError());
        }

        // ===============================
        // UNFOLLOW USER
        // ===============================

        @Test
        void unfollowUser_success() throws Exception {

                doNothing().when(connectionService)
                                .unfollowUser(USER_ID, OTHER_USER_ID);

                mockMvc.perform(delete("/api/v1/connections/unfollow/{userId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Unfollowed successfully"));
        }

        @Test
        void unfollowUser_failure() throws Exception {

                doThrow(new RuntimeException("Connection not found"))
                                .when(connectionService)
                                .unfollowUser(any(), any());

                mockMvc.perform(delete("/api/v1/connections/unfollow/{userId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isInternalServerError());
        }

        // ===============================
        // GET FOLLOWERS
        // ===============================

        @Test
        void getFollowers_success() throws Exception {

                UserSummary summary = UserSummary.builder()
                                .userId("follower1")
                                .name("Follower Name")
                                .profilePic("pic.jpg")
                                .build();

                PageResponse<UserSummary> response = PageResponse.<UserSummary>builder()
                                .content(List.of(summary))
                                .page(0)
                                .size(1)
                                .totalElements(1)
                                .totalPages(1)
                                .last(true)
                                .build();

                when(connectionService.getFollowers(any(), any()))
                                .thenReturn(response);

                mockMvc.perform(get("/api/v1/connections/followers")
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].userId").value("follower1"));
        }

        // ===============================
        // GET FOLLOWING
        // ===============================

        @Test
        void getFollowing_success() throws Exception {

                UserSummary summary = UserSummary.builder()
                                .userId("following1")
                                .name("Following Name")
                                .profilePic("pic.jpg")
                                .build();

                PageResponse<UserSummary> response = PageResponse.<UserSummary>builder()
                                .content(List.of(summary))
                                .page(0)
                                .size(1)
                                .totalElements(1)
                                .totalPages(1)
                                .last(true)
                                .build();

                when(connectionService.getFollowing(any(), any()))
                                .thenReturn(response);

                mockMvc.perform(get("/api/v1/connections/following")
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].userId").value("following1"));
        }

        // ===============================
        // ADD CLOSE FRIEND
        // ===============================

        @Test
        void addCloseFriend_success() throws Exception {

                UserSummary friendSummary = UserSummary.builder()
                                .userId(OTHER_USER_ID)
                                .name("Friend Name")
                                .profilePic("pic.jpg")
                                .build();

                CloseFriendResponse response = CloseFriendResponse.builder()
                                .id("cf123")
                                .addedAt(Instant.now())
                                .user(friendSummary)
                                .build();

                when(connectionService.addCloseFriend(any(), any()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/connections/close-friends/{closeFriendId}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("cf123"))
                                .andExpect(jsonPath("$.user.userId").value(OTHER_USER_ID));
        }

        // ===============================
        // REMOVE CLOSE FRIEND
        // ===============================

        @Test
        void removeCloseFriend_success() throws Exception {

                doNothing().when(connectionService)
                                .removeCloseFriend(USER_ID, OTHER_USER_ID);

                mockMvc.perform(delete("/api/v1/connections/close-friends/{id}", OTHER_USER_ID)
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Removed from close friends"));
        }

        // ===============================
        // GET CLOSE FRIENDS
        // ===============================

        @Test
        void getCloseFriends_success() throws Exception {

                UserSummary friendSummary = UserSummary.builder()
                                .userId("cf1")
                                .name("Close Friend")
                                .profilePic("pic.jpg")
                                .build();

                CloseFriendResponse response = CloseFriendResponse.builder()
                                .id("rel123")
                                .addedAt(Instant.now())
                                .user(friendSummary)
                                .build();

                when(connectionService.getCloseFriends(any()))
                                .thenReturn(List.of(response));

                mockMvc.perform(get("/api/v1/connections/close-friends")
                                .with(authentication(auth())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value("rel123"))
                                .andExpect(jsonPath("$[0].user.userId").value("cf1"));
        }

}
