package com.gateway.Entity;
import java.time.LocalDateTime;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Message {

    private String messageId; // Unique ID for the message

    private String chatId; // Reference to Chat's ID

    private String senderId; // userId

    private String messageType; // "text", "image", "video", "file", etc.

    private String messageContent; // null if media

    private String mediaUrl; // only if media

    private String mediaType; // image/png, video/mp4, etc.

    private boolean isRead;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
