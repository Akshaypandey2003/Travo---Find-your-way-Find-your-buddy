package com.user.UnitTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Config.JwtProvider;
import com.user.Controller.AuthController;
import com.user.DTO.AuthResponse;
import com.user.Entity.User;
import com.user.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal","unused"})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtProvider jwtProvider; 

    // ---------- REGISTER SUCCESS ----------
    @Test
    void registerUser_success() throws Exception {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("123");

        AuthResponse response = new AuthResponse();
        response.setAccessToken("dummy-token");

        when(userService.getUserByEmail("test@gmail.com")).thenReturn(null);
        when(userService.addUser(user)).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    // ---------- REGISTER CONFLICT ----------
    @Test
    void registerUser_conflict() throws Exception {

        User user = new User();
        user.setEmail("test@gmail.com");

        when(userService.getUserByEmail("test@gmail.com")).thenReturn(new User());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict());
    }

    // ---------- LOGIN SUCCESS ----------
    @Test
    void login_success() throws Exception {

        User user = new User();
        user.setUserId("1");
        user.setEmail("test@gmail.com");
        user.setPassword("123");

        AuthResponse response = new AuthResponse();
        response.setAccessToken("dummy-token");

        when(userService.generateToken("1", "test@gmail.com", "123"))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    // ---------- LOGIN USER NOT FOUND ----------
    @Test
    void login_userNotFound() throws Exception {

        User user = new User();
        user.setUserId("1");
        user.setEmail("test@gmail.com");
        user.setPassword("123");

        when(userService.generateToken("1", "test@gmail.com", "123"))
                .thenReturn(null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    // ---------- LOGIN INVALID PASSWORD ----------
    @Test
    void login_invalidPassword() throws Exception {

        User user = new User();
        user.setUserId("1");
        user.setEmail("test@gmail.com");
        user.setPassword("wrong");

        when(userService.generateToken("1", "test@gmail.com", "wrong"))
                .thenThrow(new RuntimeException("INVALID_PASSWORD"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }
}

