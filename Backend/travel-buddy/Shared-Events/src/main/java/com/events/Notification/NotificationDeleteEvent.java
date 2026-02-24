package com.events.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDeleteEvent {
    
    String type;
    String from;
    String notificationId;
    private long timestamp;
}
