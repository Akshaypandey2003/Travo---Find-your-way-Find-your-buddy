package com.user.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.user.DTO.NotificationMessage;
import com.user.Entity.Chat;
import com.user.Entity.Message;

@Service
public class WebSocketNotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(NotificationMessage message) {
        // Send to a user-specific topic
        messagingTemplate.convertAndSend("/topic/notifications/" + message.getNotificationTo(), message);
    }
    public void sendNotification(Message message, String messageTo) {
        // Send to a user-specific topic
        messagingTemplate.convertAndSend("/topic/notifications/" + messageTo, message);
        System.out.println("Message sent successfully to: "+ messageTo);
    }
    public void sendNotification(Chat chat, String messageTo) {
        // Send to a user-specific topic
        messagingTemplate.convertAndSend("/topic/notifications/" + messageTo, chat);
        System.out.println("Message sent successfully to: "+ messageTo);
    }
}
