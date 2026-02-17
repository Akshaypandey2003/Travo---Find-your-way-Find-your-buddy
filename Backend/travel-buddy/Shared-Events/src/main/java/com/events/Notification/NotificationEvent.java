package com.events.Notification;

import java.util.Map;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
    
    private String type;           // COMMENT_ADDED, POST_LIKED
    private String senderId;

    private String receiverId;

    private String message;

    // Primary navigation target
    private String resourceType;   // POST, BLOG, TRIP, CHAT
    private String resourceId;     // postId / blogId / tripId

    // Secondary / deep-link info
    private Map<String, String> metadata;

    private long timestamp;
}