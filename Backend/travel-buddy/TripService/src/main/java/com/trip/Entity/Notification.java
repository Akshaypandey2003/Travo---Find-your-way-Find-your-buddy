package com.trip.Entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Notification {

    private String notificationFrom;
    private String notificationTo;
    private String tripId;
    private NotificationType type;
    private boolean isRead = false;
    private String senderProfilePic;
    private String senderName;
    // This will hold contextual info based on type
    // private Map<String, Object> data;
    
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
    public enum NotificationType {
        TRIP_REQUEST,
        FRIEND_REQUEST,
        ACCEPTED,
        LIKE,
        COMMENT
    }
}
