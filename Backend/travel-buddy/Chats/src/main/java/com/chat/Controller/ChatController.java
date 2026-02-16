package com.chat.Controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chat.Entity.Chat;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getChatsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getChat(userId));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatById(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.getChatByChatId(chatId));
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<Chat> updateChat(
            @RequestHeader("X-ADMIN-ID") String adminId,
            @PathVariable String chatId,
            @RequestBody Chat chat) {

        return ResponseEntity.ok(chatService.updateChat(adminId, chatId, chat));
    }

    @PutMapping("/{chatId}/favorite/{userId}")
    public ResponseEntity<Chat> updateFavorite(
            @PathVariable String chatId,
            @PathVariable String userId) {

        return ResponseEntity.ok(chatService.updateFavorite(chatId, userId));
    }

    @PutMapping("/{chatId}/members")
    public ResponseEntity<Chat> updateGroupMembers(
            @RequestHeader("X-ADMIN-ID") String adminId,
            @PathVariable String chatId,
            @RequestBody Set<String> members) {

        return ResponseEntity.ok(chatService.updateGroupMembers(adminId, chatId, members));
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @RequestHeader("X-ADMIN-ID") String adminId,
            @PathVariable String chatId) {

        chatService.deleteChat(adminId, chatId);
        return ResponseEntity.noContent().build();
    }
}
