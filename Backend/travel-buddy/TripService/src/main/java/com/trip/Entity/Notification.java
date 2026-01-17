package com.trip.Entity;

import java.time.LocalDateTime;
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
    
    @Builder.Default
    private boolean isRead = false;
    private String senderProfilePic;
    private String senderName;
    // This will hold contextual info based on type
    // private Map<String, Object> data;
    
    private String message;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    public enum NotificationType {
        TRIP_REQUEST,
        FRIEND_REQUEST,
        ACCEPTED,
        LIKE,
        COMMENT,
        SYSTEM_GENERATED
    }
}
