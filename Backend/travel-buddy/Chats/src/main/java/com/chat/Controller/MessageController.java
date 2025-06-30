package com.chat.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.Entity.Message;
import com.chat.Service.MessageService;

@RestController
@RequestMapping("/auth/chats/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        return new ResponseEntity<>(messageService.sendMessage(message), HttpStatus.CREATED);
    }

    @GetMapping("/get-messages/{chatId}")
    public ResponseEntity<?> getMessage(@PathVariable String chatId) {
        return new ResponseEntity<>(messageService.getMessage(chatId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId) {

        Map<String, String> response = Map.of("message", "Message deleted successfully");
        messageService.deleteMessage(messageId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @PutMapping("/update/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable String messageId, @RequestBody Message message) {
        return new ResponseEntity<>( messageService.updateMessage(messageId,message), HttpStatus.OK);

    }
    @PutMapping("/read/{messageId}")
    public ResponseEntity<?> updateMessageReadStatus(@PathVariable String messageId) {
        return new ResponseEntity<>( messageService.updateReadStatus(messageId), HttpStatus.OK);

    }
}
