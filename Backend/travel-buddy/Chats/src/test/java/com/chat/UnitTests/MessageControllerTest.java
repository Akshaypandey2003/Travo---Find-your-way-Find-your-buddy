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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chat.Config.JwtProvider;
import com.chat.Controller.MessageController;
import com.chat.Entity.Message;
import com.chat.Exceptions.GlobalExceptionHandler;
import com.chat.Exceptions.MessageNotFoundException;
import com.chat.Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal"})
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private Message getSampleMessage() {
        Message message = new Message();
        message.setMessageId("msg123");
        message.setChatId("chat123");
        message.setSenderId("user1");
        message.setMessageContent("Hello!");
        message.setRead(false);
        return message;
    }

    // ---------------- SEND MESSAGE ----------------

    @Test
    void sendMessage_success() throws Exception {
        when(messageService.sendMessage(any(Message.class)))
                .thenReturn(getSampleMessage());

        mockMvc.perform(
                post("/api/v1/chats/chat123/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleMessage()))
        ).andExpect(status().isCreated());
    }

    @Test
    void sendMessage_failure() throws Exception {
        when(messageService.sendMessage(any(Message.class)))
                .thenThrow(new RuntimeException("Send failed"));

        mockMvc.perform(
                post("/api/v1/chats/chat123/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleMessage()))
        ).andExpect(status().isInternalServerError());
    }

    // ---------------- GET MESSAGES ----------------

    @Test
    void getMessages_success() throws Exception {
        when(messageService.getMessage("chat123"))
                .thenReturn(List.of(getSampleMessage()));

        mockMvc.perform(
                get("/api/v1/chats/chat123/messages")
        ).andExpect(status().isOk());
    }

    @Test
    void getMessages_notFound() throws Exception {
        when(messageService.getMessage("chat123"))
                .thenThrow(new MessageNotFoundException("Messages not found"));

        mockMvc.perform(
                get("/api/v1/chats/chat123/messages")
        ).andExpect(status().isNotFound());
    }

    // ---------------- UPDATE MESSAGE ----------------

    @Test
    void updateMessage_success() throws Exception {
        when(messageService.updateMessage(anyString(), any(Message.class)))
                .thenReturn(getSampleMessage());

        mockMvc.perform(
                put("/api/v1/chats/chat123/messages/msg123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleMessage()))
        ).andExpect(status().isOk());
    }

    @Test
    void updateMessage_notFound() throws Exception {
        when(messageService.updateMessage(anyString(), any(Message.class)))
                .thenThrow(new MessageNotFoundException("Message not found"));

        mockMvc.perform(
                put("/api/v1/chats/chat123/messages/msg123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleMessage()))
        ).andExpect(status().isNotFound());
    }

    // ---------------- UPDATE READ STATUS ----------------

    @Test
    void updateReadStatus_success() throws Exception {
        when(messageService.updateReadStatus("msg123"))
                .thenReturn(getSampleMessage());

        mockMvc.perform(
                put("/api/v1/chats/chat123/messages/msg123/read")
        ).andExpect(status().isOk());
    }

    @Test
    void updateReadStatus_notFound() throws Exception {
        when(messageService.updateReadStatus("msg123"))
                .thenThrow(new MessageNotFoundException("Message not found"));

        mockMvc.perform(
                put("/api/v1/chats/chat123/messages/msg123/read")
        ).andExpect(status().isNotFound());
    }

    // ---------------- DELETE MESSAGE ----------------

    @Test
    void deleteMessage_success() throws Exception {
        doNothing().when(messageService).deleteMessage("msg123");

        mockMvc.perform(
                delete("/api/v1/chats/chat123/messages/msg123")
        ).andExpect(status().isOk());
    }

    @Test
    void deleteMessage_failure() throws Exception {
        doThrow(new RuntimeException("Delete failed"))
                .when(messageService).deleteMessage("msg123");

        mockMvc.perform(
                delete("/api/v1/chats/chat123/messages/msg123")
        ).andExpect(status().isInternalServerError());
    }
}
