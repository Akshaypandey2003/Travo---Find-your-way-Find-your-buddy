package com.user.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.Config.JwtProvider;
import com.user.DTO.AuthResponse;
import com.user.Entity.User;
import com.user.Repository.UserRepo;
import com.user.ServiceImpl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;   // ✅ correct type

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId("1");
        user.setEmail("test@user.com");
        user.setName("Akshay");
        user.setGender("male");
        user.setPassword("encoded-pass"); // value does not matter now
        user.setCloseFriends(new ArrayList<>());
        user.setLikes(new ArrayList<>());
    }

    // ---------- addUser ----------
    @Test
    void shouldAddUserAndGenerateToken() {
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(jwtProvider.generateToken(any(), any(), any())).thenReturn("jwt-token");

        AuthResponse response = userService.addUser(user);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepo).save(user);
    }

    // ---------- getAllUser ----------
    @Test
    void shouldReturnAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepo.findAll(pageable)).thenReturn(page);

        List<User> users = userService.getAllUser(pageable);

        assertEquals(1, users.size());
    }

    @Test
    void shouldThrowExceptionWhenNoUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepo.findAll(pageable)).thenReturn(Page.empty());

        assertThrows(RuntimeException.class, () -> userService.getAllUser(pageable));
    }

    // ---------- getUserById ----------
    @Test
    void shouldGetUserById() {
        when(userRepo.findById("1")).thenReturn(Optional.of(user));

        User found = userService.getUserById("1");

        assertEquals("Akshay", found.getName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepo.findById("2")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUserById("2"));  // ✅ actual call
    }

    // ---------- deleteUser ----------
    @Test
    void shouldDeleteUser() {
        when(userRepo.findById("1")).thenReturn(Optional.of(user));

        String result = userService.deleteUser("1");

        verify(userRepo).delete(user);
        assertTrue(result.contains("deleted"));
    }

    // ---------- getUserByPreferences ----------
    @Test
    void shouldReturnUsersByPreferences() {
        when(userRepo.findByPreferencesInIgnoreCase(any()))
                .thenReturn(List.of(user));

        List<User> result = userService.getUserByPreferences(List.of("travel"));

        assertFalse(result.isEmpty());
    }

    // ---------- updateLikes ----------
    @Test
    void shouldLikeUser() {
        User sender = new User();
        sender.setUserId("2");

        when(userRepo.findById("1")).thenReturn(Optional.of(user));
        when(userRepo.findById("2")).thenReturn(Optional.of(sender));

        userService.updateLikes("1", "2");

        assertTrue(user.getLikes().contains("2"));
        verify(userRepo, atLeastOnce()).save(user);
    }

    // ---------- addCloseFriend ----------
    @Test
    void shouldAddCloseFriend() {
        when(userRepo.findById("1")).thenReturn(Optional.of(user));

        String msg = userService.addCloseFriend("1", "2");

        assertTrue(user.getCloseFriends().contains("2"));
    }

    // ---------- removeCloseFriend ----------
    @Test
    void shouldRemoveCloseFriend() {
        user.getCloseFriends().add("2");
        when(userRepo.findById("1")).thenReturn(Optional.of(user));

        String msg = userService.removeCloseFriend("1", "2");

        assertFalse(user.getCloseFriends().contains("2"));
    }

    // ---------- generateToken(User) ----------
    @Test
    void shouldGenerateTokenFromUser() {
        when(jwtProvider.generateToken(any(), any(), any()))
                .thenReturn("jwt-token");

        AuthResponse response = userService.generateToken(user);

        assertEquals("jwt-token", response.getAccessToken());
    }

    // ---------- generateToken(email,password) ----------
    @Test
    void shouldGenerateTokenWithEmailPassword() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        // ✅ VERY IMPORTANT
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        when(jwtProvider.generateToken(any(), any(), any()))
                .thenReturn("jwt-token");

        AuthResponse response =
                userService.generateToken("1", user.getEmail(), "password");

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
    }
}
