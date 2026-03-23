package com.chat.Entity;

import lombok.*;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "messages")
@CompoundIndexes({
        @CompoundIndex(name = "idx_message_chat_created", def = "{'chatId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "idx_message_chat_read_created", def = "{'chatId': 1, 'isRead': 1, 'createdAt': -1}")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    private String messageId; // Unique ID for the message

    @Version
    private Long version;

    @Indexed
    private String chatId; // Reference to Chat's ID

    @Indexed
    private String senderId; // userId

    private String messageType; // "text", "image", "video", "file", etc.

    private String messageContent; // null if media

    private String mediaUrl; // only if media

    private String mediaType; // image/png, video/mp4, etc.

    @Builder.Default
    private boolean read = false;

    @CreatedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
