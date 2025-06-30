package com.chat.Entity;

import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    private String messageId; // Unique ID for the message

    @Indexed
    private String chatId; // Reference to Chat's ID

    private String senderId; // userId

    private String messageType; // "text", "image", "video", "file", etc.

    private String messageContent; // null if media

    private String mediaUrl; // only if media

    private String mediaType; // image/png, video/mp4, etc.

    private boolean isRead;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
