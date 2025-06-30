package com.chat.Controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.Entity.Chat;
import com.chat.Service.ChatService;

@RestController
@RequestMapping("/auth/chats")
public class ChatController {
    
    @Autowired
    private ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat){
      return ResponseEntity.ok(chatService.createChat(chat));
    }
    
    @GetMapping("/get-chat/{userId}")
    public ResponseEntity<?> getChat(@PathVariable String userId){
        return ResponseEntity.ok(chatService.getChat(userId));
    }
    @GetMapping("/get-chat-by-chatId/{chatId}")
    public ResponseEntity<?> getChatByChatId(@PathVariable String chatId){
        return ResponseEntity.ok(chatService.getChatByChatId(chatId));
    }

    @PutMapping("/update/{chatId}")
    public ResponseEntity<Chat> updateChat(@PathVariable String chatId, @RequestBody Chat chat){
        return ResponseEntity.ok(chatService.updateChat(chatId, chat));
    }
    @PutMapping("/update-favorite/{chatId}/{userId}")
    public ResponseEntity<Chat> updateFavorite(@PathVariable String chatId, @PathVariable String userId){
        return ResponseEntity.ok(chatService.updateFavorite(chatId, userId));
    }
    @PutMapping("/update-group-members/{chatId}")
    public ResponseEntity<Chat> updateGroupMembers(@PathVariable String chatId, @RequestBody Set<String> members ){
        return ResponseEntity.ok(chatService.updateGroupMembers(chatId, members));
    }
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable String chatId){
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }

}
