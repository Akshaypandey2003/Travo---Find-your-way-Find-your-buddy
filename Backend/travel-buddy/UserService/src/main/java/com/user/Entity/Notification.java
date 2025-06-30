package com.user.Entity;

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

@Document
public class Notification {

    @Id
    private String notificationId;
    private String notificationFrom;
    private String notificationTo;
    private NotificationType type;
    private boolean isRead = false;
    private String senderProfilePic;
    private String senderName;
    private String tripId;
    // This will hold contextual info based on type
    // private Map<String, Object> data;
    
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
    public enum NotificationType {
        TRIP_REQUEST,
        FRIEND_REQUEST,
        ACCEPTED,
        LIKE,
        COMMENT,
        NEW_TRIP
    }
}
