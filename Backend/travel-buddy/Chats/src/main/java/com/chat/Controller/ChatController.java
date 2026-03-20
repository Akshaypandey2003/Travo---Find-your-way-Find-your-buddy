package com.chat.Controller;

import java.util.Set;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.chat.Entity.Chat;
import com.chat.Entity.ChatParticipant;
import com.chat.Service.ChatService;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        Chat createdChat = chatService.createChat(chat);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Chat>> getChatsByUser(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(chatService.getChatsByUserId(userId));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChatById(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.getChatById(chatId));
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<Chat> updateChat(
            @AuthenticationPrincipal String adminId,
            @PathVariable String chatId,
            @RequestBody Chat chat) {

        return ResponseEntity.ok(chatService.updateChat(adminId, chatId, chat));
    }

    @PutMapping("/{chatId}/favorite")
    public ResponseEntity<Chat> updateFavorite(
            @PathVariable String chatId,
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(chatService.updateFavorite(chatId, userId));
    }


    @PutMapping("/{chatId}/members")
    public ResponseEntity<Chat> updateGroupMembers(
            @AuthenticationPrincipal String adminId,
            @PathVariable String chatId,
            @RequestBody Set<ChatParticipant> members) {

        return ResponseEntity.ok(chatService.updateGroupMembers(adminId, chatId, members));
    }


    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @AuthenticationPrincipal String userId,
            @PathVariable String chatId) {

        chatService.deleteChat(userId, chatId);
        return ResponseEntity.noContent().build();
    }
}