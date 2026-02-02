package com.user.UnitTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Config.JwtProvider;
import com.user.Controller.UserController;
import com.user.DTO.Author;
import com.user.Entity.User;
import com.user.Service.ImageService;
import com.user.Service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

     @MockBean
    private JwtProvider jwtProvider; 

    // ---------- updateUser ----------
    @Test
    void shouldUpdateUser() throws Exception {
        User existing = User.builder().userId("1").name("Old Name").build();
        User updated = User.builder().name("Akshay").build();

        when(userService.getUserById("1")).thenReturn(existing);
        when(userService.updateUser(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/user/update-user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Akshay"));
    }

    // ---------- updateLikes ----------
    @Test
    void shouldUpdateLikes() throws Exception {
        when(userService.updateLikes("1", "2")).thenReturn("Liked");

        mockMvc.perform(put("/user/updateLike/1/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Liked"));
    }

    // ---------- getAllUsers ----------
    @Test
    void shouldGetAllUsers() throws Exception {
        User user = User.builder().userId("1").name("Akshay").build();
        when(userService.getAllUser(any(Pageable.class))).thenReturn(List.of(user));

        mockMvc.perform(get("/user/get-all-users?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Akshay"));
    }

    // ---------- getAuthor ----------
    @Test
    void shouldGetAuthor() throws Exception {
        Author author = new Author("1", "Akshay", "pic");
        when(userService.getAuthorById("1")).thenReturn(author);

        mockMvc.perform(get("/user/get-author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Akshay"));
    }

    // ---------- getUserById ----------
    @Test
    void shouldGetUserById() throws Exception {
        User user = User.builder().userId("1").name("Akshay").build();
        when(userService.getUserById("1")).thenReturn(user);

        mockMvc.perform(get("/user/get-user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Akshay"));
    }

    // ---------- getUserByPreferences ----------
    @Test
    void shouldGetUserByPreferences() throws Exception {
        User user = User.builder().userId("1").name("Akshay").build();
        when(userService.getUserByPreferences(any())).thenReturn(List.of(user));

        mockMvc.perform(post("/user/get-user-by-preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("travel"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Akshay"));
    }

    // ---------- addCloseFriend ----------
    @Test
    void shouldAddCloseFriend() throws Exception {
        when(userService.addCloseFriend("1", "2")).thenReturn("Close friend added successfully");

        mockMvc.perform(post("/user/close-friend/add/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['message: ']").value("Close friend added successfully"));
    }

    // ---------- removeCloseFriend ----------
    @Test
    void shouldRemoveCloseFriend() throws Exception {
        when(userService.removeCloseFriend("1", "2")).thenReturn("Close friend removed successfully");

        mockMvc.perform(delete("/user/close-friend/remove/1/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['message: ']").value("Close friend removed successfully"));
    }
}
