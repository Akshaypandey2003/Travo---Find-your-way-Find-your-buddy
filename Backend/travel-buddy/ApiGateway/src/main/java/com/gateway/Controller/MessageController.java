package com.gateway.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.reactive.socket.WebSocketSession;

import com.gateway.Entity.Chat;
// import com.gateway.Config.WebSocketConfiguration;
import com.gateway.Entity.Message;
import com.gateway.Entity.MessageResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/chats/message")
public class MessageController {

    // @Autowired
    // private WebSocketConfiguration webSocketConfig;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        try {

            // -------- store message in DB ---------------------
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Message> entity = new HttpEntity<>(message);
            String msgServiceUrl = "http://localhost:8092/auth/chats/message/send";

            ResponseEntity<Message> response = restTemplate.exchange(msgServiceUrl, HttpMethod.POST, entity,
                    Message.class);

            Message msg = response.getBody();
            String chatId = "";
            if (msg != null)
                chatId = msg.getChatId();

            // ---------- Fetch chats to get all the participants --------------------
            String chatServiceUrl = "http://localhost:8092/auth/chats/get-chat-by-chatId/" + chatId;

            ResponseEntity<Chat> response2 = restTemplate.exchange(chatServiceUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Chat>() {
                    });

            Chat chat = response2.getBody();
            Set<String> participants = new TreeSet<>();
            if (chat != null)
                participants = new TreeSet<>(chat.getParticipants());

            // ---------- Send msg notification to each participants ---------------

            for (String participantId : participants) {
                if (!participantId.equals(msg.getSenderId())) {
                    try {
                        String individualNotificationUrl = "http://localhost:8088/chat/message/send/" + participantId;
                        HttpEntity<Message> notifyEntity = new HttpEntity<>(msg);
                        restTemplate.exchange(individualNotificationUrl, HttpMethod.POST, notifyEntity, Void.class);
                    } catch (Exception e) {
                        System.err.println("Failed to notify " + participantId + ": " + e.getMessage());
                    }
                }
            }

            return new ResponseEntity<>(msg, response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error sending message: " + e.getMessage(), "error");

            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-messages/{chatId}")
    public ResponseEntity<?> getMessage(@PathVariable String chatId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String msgServiceUrl = "http://localhost:8092/auth/chats/message/get-messages/" + chatId;

            ResponseEntity<List<Message>> response = restTemplate.exchange(msgServiceUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Message>>() {
                    });

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching messages: " + e.getMessage(), "error");

            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            String msgServiceUrl = "http://localhost:8092/auth/chats/message/delete/" + messageId;

            ResponseEntity<Message> response = restTemplate.exchange(msgServiceUrl, HttpMethod.DELETE, null,
                    Message.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error deleting message: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable String messageId, @RequestBody Message message) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Message> entity = new HttpEntity<>(message);
            String msgServiceUrl = "http://localhost:8092/auth/chats/message/update/" + messageId;

            ResponseEntity<Message> response = restTemplate.exchange(msgServiceUrl, HttpMethod.PUT, entity,
                    Message.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error updating message: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/read/{messageId}")
    public ResponseEntity<?> updateMessageReadStatus(@PathVariable String messageId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String msgServiceUrl = "http://localhost:8092/auth/chats/message/read/" + messageId;

            ResponseEntity<Message> response = restTemplate.exchange(msgServiceUrl, HttpMethod.PUT, null,
                    Message.class);

            // ---------- Send msg notification to each participants ---------------
            Message msg = response.getBody();

            String individualNotificationUrl = "http://localhost:8088/chat/message/send/" + msg.getSenderId();
            HttpEntity<Message> notifyEntity = new HttpEntity<>(msg);
            restTemplate.exchange(individualNotificationUrl, HttpMethod.POST, notifyEntity, Void.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());

        } catch (

        Exception e) {
            MessageResponse msg = new MessageResponse("Error updating message read status: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
