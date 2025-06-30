package com.gateway.Entity;

import java.time.LocalDateTime;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private String notificationId;
    private String notificationFrom;
    private String notificationTo;
    private NotificationType type;
    private boolean isRead = false;
    private String senderProfilePic;
    private String senderName;
    private String tripId;
    private MessageResponse messageResponse;

    // This will hold contextual info based on type
    // private Map<String, Object> data;

    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
    public enum NotificationType {
        TRIP_REQUEST,
        FRIEND_REQUEST,
        FRIEND_REQUEST_ACCEPTED,
        LIKE,
        COMMENT,
        NEW_TRIP
    }
}
