package com.gateway.Controller;

import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestTemplate;

import com.gateway.Entity.Chat;
import com.gateway.Entity.Comment;
import com.gateway.Entity.MessageResponse;

@RestController
@RequestMapping("/auth/chats")
public class ChatController {
    
    @PostMapping("/create")
    public ResponseEntity<?> createChat(@RequestBody Chat chat){
         System.out.println("Data received to create a new chat is: "+chat);
        try {
           
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Chat> entity = new HttpEntity<>(chat);
            String chatServiceUrl = "http://localhost:8092/auth/chats/create";
            

            ResponseEntity<Chat> response = restTemplate.exchange(chatServiceUrl,HttpMethod.POST,entity,Chat.class);
             
             Chat savedChat = response.getBody();
             Set<String> participants = savedChat.getParticipants();
             String adminId = savedChat.getGroupAdmin().stream().findFirst().orElse(null);
             for (String participantId : participants) {
                if (!participantId.equals(adminId)) {
                    try {
                        String individualNotificationUrl = "http://localhost:8088/chat/new-group/send/" + participantId;
                        HttpEntity<Chat> notifyEntity = new HttpEntity<>(savedChat);
                        restTemplate.exchange(individualNotificationUrl, HttpMethod.POST, notifyEntity, Void.class);
                    } catch (Exception e) {
                        System.err.println("Failed to notify " + participantId + ": " + e.getMessage());
                    }
                }
            }
            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error creating chat: " + e.getMessage(),"error");
          
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/get-chat/{userId}")
    public ResponseEntity<?> getChat(@PathVariable String userId){
        try {
            RestTemplate restTemplate = new RestTemplate();

            String chatServiceUrl = "http://localhost:8092/auth/chats/get-chat/"+userId;

            ResponseEntity<List<Chat>> response = restTemplate.exchange(chatServiceUrl,HttpMethod.GET,null, new ParameterizedTypeReference<List<Chat>>() {
                    });

            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error fetching Chat: " + e.getMessage(),"error");
          
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{chatId}")
    public ResponseEntity<?> updateChat(@PathVariable String chatId, @RequestBody Chat chat){
        try {
            RestTemplate restTemplate = new RestTemplate();

             HttpEntity<Chat> entity = new HttpEntity<>(chat);

            String chatServiceUrl = "http://localhost:8092/auth/chats/update/"+chatId;

            ResponseEntity<Chat> response = restTemplate.exchange(chatServiceUrl,HttpMethod.PUT,entity,Chat.class);

            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating Chat: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @PutMapping("/update-favorite/{chatId}/{userId}")
    public ResponseEntity<?> updateFavorite(@PathVariable String chatId, @PathVariable String userId){
        try {
            RestTemplate restTemplate = new RestTemplate();

            String chatServiceUrl = "http://localhost:8092/auth/chats/update-favorite/"+chatId+"/"+userId;

            ResponseEntity<Chat> response = restTemplate.exchange(chatServiceUrl,HttpMethod.PUT,null,Chat.class);

            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating favorite status: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update-group-members/{chatId}")
    public ResponseEntity<?> updateGroupMembers(@PathVariable String chatId, @RequestBody Set<String> members){
        try {
            System.out.println("Updating group members for chatId: " + chatId + " with members: " + members);
            RestTemplate restTemplate = new RestTemplate();
            String chatServiceUrl = "http://localhost:8092/auth/chats/update-group-members/"+chatId;
            HttpEntity<Set<String>> entity = new HttpEntity<>(members);
            
            ResponseEntity<Chat> response = restTemplate.exchange(chatServiceUrl,HttpMethod.PUT,entity,Chat.class);

            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating group members: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable String chatId){
        try {
            RestTemplate restTemplate = new RestTemplate();

            String chatServiceUrl = "http://localhost:8092/auth/chats/update/"+chatId;

            ResponseEntity<Chat> response = restTemplate.exchange(chatServiceUrl,HttpMethod.DELETE,null,Chat.class);
            
            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error deleting Chat: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
