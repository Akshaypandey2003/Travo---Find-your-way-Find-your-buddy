package com.chat.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.chat.Config.JwtProvider;
import com.chat.Controller.ChatController;
import com.chat.Entity.Chat;
import com.chat.Service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal"})
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private Chat getSampleChat() {
        Chat chat = new Chat();
        chat.setChatId("chat123");
        chat.setGroupChat(true);
        chat.setGroupName("Test Group");
        chat.setParticipants(Set.of("user1", "user2"));
        chat.setGroupAdmin(Set.of("admin1"));
        return chat;
    }

    // ---------- CREATE CHAT ----------

    @Test
    void createChat_success() throws Exception {
        when(chatService.createChat(any(Chat.class)))
                .thenReturn(getSampleChat());

        mockMvc.perform(
                post("/api/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleChat()))
        ).andExpect(status().isCreated());
    }

    @Test
    void createChat_failure() throws Exception {
        when(chatService.createChat(any(Chat.class)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                post("/api/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleChat()))
        ).andExpect(status().isInternalServerError());
    }

    // ---------- GET CHAT BY USER ----------

    @Test
    void getChatByUser_success() throws Exception {
        when(chatService.getChat("user1"))
                .thenReturn(List.of(getSampleChat()));

        mockMvc.perform(
                get("/api/v1/chats/user/user1")
        ).andExpect(status().isOk());
    }

    @Test
    void getChatByUser_failure() throws Exception {
        when(chatService.getChat("user1"))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                get("/api/v1/chats/user/user1")
        ).andExpect(status().isInternalServerError());
    }

    // ---------- GET CHAT BY CHAT ID ----------

    @Test
    void getChatById_success() throws Exception {
        when(chatService.getChatByChatId("chat123"))
                .thenReturn(getSampleChat());

        mockMvc.perform(
                get("/api/v1/chats/chat123")
        ).andExpect(status().isOk());
    }

    @Test
    void getChatById_failure() throws Exception {
        when(chatService.getChatByChatId("chat123"))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                get("/api/v1/chats/chat123")
        ).andExpect(status().isInternalServerError());
    }

    // ---------- UPDATE CHAT ----------

    @Test
    void updateChat_success() throws Exception {
        when(chatService.updateChat(anyString(), anyString(), any(Chat.class)))
                .thenReturn(getSampleChat());

        mockMvc.perform(
                put("/api/v1/chats/chat123")
                        .header("X-ADMIN-ID", "admin1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleChat()))
        ).andExpect(status().isOk());
    }

    @Test
    void updateChat_failure() throws Exception {
        when(chatService.updateChat(anyString(), anyString(), any(Chat.class)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                put("/api/v1/chats/chat123")
                        .header("X-ADMIN-ID", "admin1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleChat()))
        ).andExpect(status().isInternalServerError());
    }

    // ---------- UPDATE FAVORITE ----------

    @Test
    void updateFavorite_success() throws Exception {
        when(chatService.updateFavorite("chat123", "user1"))
                .thenReturn(getSampleChat());

        mockMvc.perform(
                put("/api/v1/chats/chat123/favorite/user1")
        ).andExpect(status().isOk());
    }

    @Test
    void updateFavorite_failure() throws Exception {
        when(chatService.updateFavorite(anyString(), anyString()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                put("/api/v1/chats/chat123/favorite/user1")
        ).andExpect(status().isInternalServerError());
    }

    // ---------- UPDATE GROUP MEMBERS ----------

    @Test
    void updateGroupMembers_success() throws Exception {
        when(chatService.updateGroupMembers(anyString(), anyString(), any()))
                .thenReturn(getSampleChat());

        mockMvc.perform(
                put("/api/v1/chats/chat123/members")
                        .header("X-ADMIN-ID", "admin1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("user3")))
        ).andExpect(status().isOk());
    }

    @Test
    void updateGroupMembers_failure() throws Exception {
        when(chatService.updateGroupMembers(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(
                put("/api/v1/chats/chat123/members")
                        .header("X-ADMIN-ID", "admin1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of("user3")))
        ).andExpect(status().isInternalServerError());
    }

    // ---------- DELETE CHAT ----------

    @Test
    void deleteChat_success() throws Exception {
        doNothing().when(chatService).deleteChat(anyString(), anyString());

        mockMvc.perform(
                delete("/api/v1/chats/chat123")
                        .header("X-ADMIN-ID", "admin1")
        ).andExpect(status().isNoContent());
    }

    @Test
    void deleteChat_failure() throws Exception {
        doThrow(new RuntimeException())
                .when(chatService).deleteChat(anyString(), anyString());

        mockMvc.perform(
                delete("/api/v1/chats/chat123")
                        .header("X-ADMIN-ID", "admin1")
        ).andExpect(status().isInternalServerError());
    }
}
