package com.chat.Controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chat.Entity.Message;
import com.chat.Service.MessageService;

@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @PathVariable String chatId,
            @RequestBody Message message) {

        Message savedMessage = messageService.sendMessage(chatId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(@PathVariable String chatId) {
        return ResponseEntity.ok(messageService.getMessagesByChatId(chatId));
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(
            @PathVariable String messageId,
            @RequestBody Message message) {

        return ResponseEntity.ok(messageService.updateMessage(messageId, message));
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Message> markAsRead(@PathVariable String messageId) {
        return ResponseEntity.ok(messageService.updateReadStatus(messageId));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, String>> deleteMessage(
            @PathVariable String messageId) {

        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(
                Map.of("message", "Message deleted successfully")
        );
    }
}
