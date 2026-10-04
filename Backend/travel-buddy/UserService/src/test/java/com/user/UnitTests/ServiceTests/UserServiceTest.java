package com.user.UnitTests.ServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.Config.JwtProvider;
import com.user.DTO.AuthResponse;
import com.user.DTO.LoginRequest;
import com.user.DTO.PageResponse;
import com.user.DTO.RegisterRequest;
import com.user.DTO.ResetPasswordRequest;
import com.user.DTO.UpdateUserRequest;
import com.user.DTO.UserResponse;
import com.user.Entity.CloseFriends;
import com.user.Entity.Connections;
import com.user.Entity.PasswordResetToken;
import com.user.Entity.User;
import com.user.Exceptions.InvalidPasswordException;
import com.user.Exceptions.InvalidResetTokenException;
import com.user.Exceptions.UserConflictException;
import com.user.Exceptions.UserNotFoundException;
import com.user.Helper.UserMapper;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.PasswordResetTokenRepo;
import com.user.Repository.UserRepo;
import com.user.ServiceImpl.UserEventProducer;
import com.user.ServiceImpl.UserNotificationProducer;
import com.user.ServiceImpl.RefreshTokenService;
import com.user.ServiceImpl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

        @Mock
        private UserRepo userRepo;

        @Mock
        private ConnectionRepo connectionRepo;

        @Mock
        private CloseFriendsRepo closeFriendsRepo;

        @Mock
        private JwtProvider jwtProvider;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private PasswordResetTokenRepo tokenRepo;

        @Mock
        private UserNotificationProducer notificationProducer;

        @Mock
        private UserEventProducer userEventProducer;

        @Mock
        private RefreshTokenService refreshTokenService;

        @Mock
        private UserMapper userMapper;

        @InjectMocks
        private UserServiceImpl userService;

        private User user;
        private UserResponse userResponse;

        @BeforeEach
        void setup() {

                user = User.builder()
                                .userId("user1")
                                .email("test@email.com")
                                .password("encodedPassword")
                                .name("Akshay")
                                .followersCount(5)
                                .followingsCount(3)
                                .closeFriendsCount(2)
                                .build();

                userResponse = new UserResponse();
                userResponse.setUserId("user1");
                userResponse.setName("Akshay");
        }

        // =====================================================
        // REGISTER USER SUCCESS
        // =====================================================

         @Test
    void addUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@email.com");
        request.setPassword("password");
        request.setGender("male");

        when(userRepo.findByEmail(any())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepo.save(any())).thenReturn(user);
        when(jwtProvider.generateToken(any(), any(), any())).thenReturn("jwt-token");
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse response = userService.addUser(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());

        verify(userEventProducer).publishUserCreated(any(), any(), any());
        verify(notificationProducer).sendWelcomeNotification(any(), any(), any());
    }

        // =====================================================
        // REGISTER USER FAILURE EMAIL EXISTS
        // =====================================================

        @Test
        void addUser_EmailAlreadyExists() {

                RegisterRequest request = new RegisterRequest();
                request.setEmail("test@email.com");

                when(userRepo.findByEmail(request.getEmail()))
                                .thenReturn(Optional.of(user));

                assertThrows(UserConflictException.class,
                                () -> userService.addUser(request));
        }

        // =====================================================
        // GET USER BY ID SUCCESS
        // =====================================================

        @Test
        void getUserById_Success() {

                when(userRepo.findById("user1"))
                                .thenReturn(Optional.of(user));

                when(userMapper.toResponse(user))
                                .thenReturn(userResponse);

                UserResponse result = userService.getUserById("user1");

                assertEquals("user1", result.getUserId());
        }

        // =====================================================
        // GET USER BY ID FAILURE
        // =====================================================

        @Test
        void getUserById_NotFound() {

                when(userRepo.findById("user1"))
                                .thenReturn(Optional.empty());

                assertThrows(UserNotFoundException.class,
                                () -> userService.getUserById("user1"));
        }

        // =====================================================
        // GET ALL USERS SUCCESS
        // =====================================================

        @Test
        void getAllUsers_Success() {

                Pageable pageable = PageRequest.of(0, 10);

                Page<User> page = new PageImpl<>(
                                List.of(user),
                                pageable,
                                1);

                when(userRepo.findAll(pageable))
                                .thenReturn(page);

                when(userMapper.toResponse(user))
                                .thenReturn(userResponse);

                PageResponse<UserResponse> result = userService.getAllUsers(pageable);

                // Assertions
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals("user1",
                                result.getContent().get(0).getUserId());

                assertEquals(0, result.getPage());
                assertEquals(10, result.getSize());
                assertEquals(1, result.getTotalElements());
                assertEquals(1, result.getTotalPages());
                assertTrue(result.isLast());
        }

        // =====================================================
        // GET USER BY EMAIL SUCCESS
        // =====================================================

        @Test
        void getUserByEmail_Success() {

                when(userRepo.findByEmail(user.getEmail()))
                                .thenReturn(Optional.of(user));

                User result = userService.getUserByEmail(user.getEmail());

                assertEquals("user1", result.getUserId());
        }

        // =====================================================
        // GET USER BY EMAIL FAILURE
        // =====================================================

        @Test
        void getUserByEmail_NotFound() {

                when(userRepo.findByEmail(any()))
                                .thenReturn(Optional.empty());

                assertThrows(UserNotFoundException.class,
                                () -> userService.getUserByEmail("email"));
        }

        // =====================================================
        // UPDATE USER SUCCESS
        // =====================================================

        @Test
        void updateUser_Success() {

                UpdateUserRequest request = new UpdateUserRequest();

                request.setName("Updated Name");

                when(userRepo.findById("user1"))
                                .thenReturn(Optional.of(user));

                when(userRepo.save(any()))
                                .thenReturn(user);

                when(userMapper.toResponse(user))
                                .thenReturn(userResponse);

                UserResponse result = userService.updateUser("user1", request);

                assertNotNull(result);

                verify(userRepo).save(user);
        }

        // =====================================================
        // UPDATE USER FAILURE
        // =====================================================

        @Test
        void updateUser_NotFound() {

                when(userRepo.findById("user1"))
                                .thenReturn(Optional.empty());

                assertThrows(UserNotFoundException.class,
                                () -> userService.updateUser(
                                                "user1",
                                                new UpdateUserRequest()));
        }

        // =====================================================
        // DELETE USER SUCCESS FULL RELATIONSHIPS
        // =====================================================

        @Test
        void deleteUser_Success_WithRelationships() {

                Connections follower = Connections.builder()
                                .followerId("follower1")
                                .followingId("user1")
                                .build();

                Connections following = Connections.builder()
                                .followerId("user1")
                                .followingId("following1")
                                .build();

                CloseFriends closeFriend = CloseFriends.builder()
                                .userId("user1")
                                .closeFriendId("cf1")
                                .build();

                CloseFriends addedByOther = CloseFriends.builder()
                                .userId("otherUser")
                                .closeFriendId("user1")
                                .build();

                when(userRepo.existsById("user1"))
                                .thenReturn(true);

                when(connectionRepo.findByFollowingId("user1"))
                                .thenReturn(List.of(follower));

                when(connectionRepo.findByFollowerId("user1"))
                                .thenReturn(List.of(following));

                when(closeFriendsRepo.findByUserId("user1"))
                                .thenReturn(List.of(closeFriend));

                when(closeFriendsRepo.findByCloseFriendId("user1"))
                                .thenReturn(List.of(addedByOther));

                userService.deleteUser("user1");

                verify(userRepo)
                                .decrementFollowingCount("follower1");

                verify(userRepo)
                                .decrementFollowersCount("following1");

                verify(userRepo)
                                .decrementCloseFriendsCount("user1");

                verify(userRepo)
                                .decrementCloseFriendsCount("otherUser");

                verify(userRepo)
                                .deleteById("user1");
        }

        // =====================================================
        // DELETE USER FAILURE
        // =====================================================

        @Test
        void deleteUser_NotFound() {

                when(userRepo.existsById("user1"))
                                .thenReturn(false);

                assertThrows(UserNotFoundException.class,
                                () -> userService.deleteUser("user1"));
        }

        // =====================================================
        // LOGIN SUCCESS
        // =====================================================

        @Test
        void generateToken_Success() {

                LoginRequest request = new LoginRequest();

                request.setEmail(user.getEmail());
                request.setPassword("password");

                when(userRepo.findByEmail(user.getEmail()))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(
                                "password",
                                user.getPassword()))
                                .thenReturn(true);

                when(jwtProvider.generateToken(any(), any(), any()))
                                .thenReturn("jwt-token");

                when(userMapper.toResponse(user))
                                .thenReturn(userResponse);

                AuthResponse response = userService.generateToken(request);

                assertEquals("jwt-token",
                                response.getAccessToken());
        }

        // =====================================================
        // LOGIN USER NOT FOUND
        // =====================================================

        @Test
        void generateToken_UserNotFound() {

                LoginRequest request = new LoginRequest();

                request.setEmail("wrong@email.com");

                when(userRepo.findByEmail(any()))
                                .thenReturn(Optional.empty());

                assertThrows(UserNotFoundException.class,
                                () -> userService.generateToken(request));
        }

        // =====================================================
        // LOGIN INVALID PASSWORD
        // =====================================================

        @Test
        void generateToken_InvalidPassword() {

                LoginRequest request = new LoginRequest();

                request.setEmail(user.getEmail());
                request.setPassword("wrong");

                when(userRepo.findByEmail(user.getEmail()))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(any(), any()))
                                .thenReturn(false);

                assertThrows(InvalidPasswordException.class,
                                () -> userService.generateToken(request));
        }

        @Test
        void resetPassword_TokenExpired() {
                PasswordResetToken token = PasswordResetToken.builder()
                                .token("token")
                                .userId("user1")
                                .used(false)
                                .expiryTime(System.currentTimeMillis() - 1000)
                                .build();

                when(tokenRepo.findByToken(any()))
                                .thenReturn(Optional.of(token));

                assertThrows(InvalidResetTokenException.class,
                                () -> userService.resetPassword(new ResetPasswordRequest()));
        }

        @Test
        void forgotPassword_UserExists() {
                when(userRepo.findByEmail(user.getEmail()))
                                .thenReturn(Optional.of(user));

                userService.forgotPassword(user.getEmail());

                verify(tokenRepo).save(any());
                verify(notificationProducer)
                                .sendPasswordResetNotification(any(), any(), any(), any());
        }

        @Test
        void forgotPassword_UserNotExists() {
                when(userRepo.findByEmail(any()))
                                .thenReturn(Optional.empty());

                userService.forgotPassword("wrong@email.com");

                verify(tokenRepo, never()).save(any());
                verify(notificationProducer, never())
                                .sendPasswordResetNotification(any(), any(), any(), any());
        }

        @Test
        void resetPassword_Success() {
                ResetPasswordRequest request = new ResetPasswordRequest();
                request.setToken("token123");
                request.setPassword("newPass");

                PasswordResetToken token = PasswordResetToken.builder()
                                .token("token123")
                                .userId("user1")
                                .expiryTime(System.currentTimeMillis() + 100000)
                                .used(false)
                                .build();

                when(tokenRepo.findByToken("token123"))
                                .thenReturn(Optional.of(token));
                when(userRepo.findById("user1"))
                                .thenReturn(Optional.of(user));
                when(passwordEncoder.encode("newPass"))
                                .thenReturn("encodedNewPass");

                userService.resetPassword(request);

                verify(userRepo).save(user);
                verify(tokenRepo).delete(token);
                verify(notificationProducer)
                                .sendPasswordResetSuccessNotification(any(), any(), any());
        }

      @Test
    void resetPassword_InvalidToken() {
        when(tokenRepo.findByToken(any()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidResetTokenException.class,
                () -> userService.resetPassword(new ResetPasswordRequest()));
    }

    @Test
    void resetPassword_TokenAlreadyUsed() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("token")
                .userId("user1")
                .used(true)
                .expiryTime(System.currentTimeMillis() + 100000)
                .build();

        when(tokenRepo.findByToken(any()))
                .thenReturn(Optional.of(token));

        assertThrows(InvalidResetTokenException.class,
                () -> userService.resetPassword(new ResetPasswordRequest()));
    }

}