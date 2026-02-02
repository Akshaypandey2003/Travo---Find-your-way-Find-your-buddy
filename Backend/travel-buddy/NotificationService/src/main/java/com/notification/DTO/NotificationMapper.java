package com.notification.DTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.events.Entity.NotificationEvent;
import com.notification.Entity.Notification;

public class NotificationMapper {

     public static Notification toEntity(
            NotificationEvent event,
            UserSummary sender,
            String receiverId) {

        return Notification.builder()
                .notificationFrom(event.getSenderId())
                .senderName(sender.getName())
                .senderProfilePic(sender.getProfilePic())
                .notificationTo(receiverId)
                .type(event.getType())
                .message(event.getMessage()) // already prepared by producer
                .resourceType(event.getResourceType())
                .resourceId(event.getResourceId())
                .metadata(event.getMetadata())
                .createdAt(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(event.getTimestamp()),
                        ZoneId.systemDefault()))
                .read(false)
                .build();
    }
}
