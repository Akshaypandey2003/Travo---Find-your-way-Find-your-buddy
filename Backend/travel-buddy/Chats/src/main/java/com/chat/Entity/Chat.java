package com.chat.Entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private String chatId;

    private boolean groupChat;
    private Set<String> favoriteBy = new HashSet<>(); // userIds who marked the chat as favorite
    private String groupName; // null for 1-to-1 chats, set for group chats
    private String groupDescription;
    private String groupImageUrl; // null for 1-to-1 chats, set for group chats
    private Set<String> participants = new TreeSet<>(); // userIds
    private Set<String> groupAdmin = new TreeSet<>(); // only if isGroupChat == true

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
