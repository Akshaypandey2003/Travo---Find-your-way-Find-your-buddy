package com.notification.Entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document(collection = "notifications")
public class Notification {
    
     @Id
    private String notificationId;

    // Who triggered the action
    private String notificationFrom;

    private String senderName;
    private String senderProfilePic;

    // Receiver (1 notification per user)
    private String notificationTo;

    // FRIEND_REQUEST, COMMENT, CHAT_MESSAGE
    private String type;

    private String message;

    // Navigation info
    private String resourceType;   // CHAT, POST, TRIP
    private String resourceId;

    // Extra metadata (deep links, flags)
    private Map<String, String> metadata;

    @Builder.Default
    private boolean read = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
