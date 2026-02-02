package com.notification.Controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@SuppressWarnings("null")
public class NotificationSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String userId, Object payload) {
        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/notifications",
            payload
        );
    }
}
