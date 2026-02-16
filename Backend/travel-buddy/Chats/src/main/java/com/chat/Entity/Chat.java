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

@Document(collection = "chats")
@Data
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private String chatId;

    private boolean groupChat;

    private Set<String> favoriteBy; // userIds who marked the chat as favorite
    private Set<String> participants; // userIds
    private Set<String> groupAdmin; // only if isGroupChat == true

    private String groupName; // null for 1-to-1 chats, set for group chats
    private String groupDescription;
    private String groupImageUrl; // null for 1-to-1 chats, set for group chats

    

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime recentConversationAt;

    public Chat()
    {
        this.favoriteBy = new HashSet<>();
        this.groupAdmin = new TreeSet<>();
        this.participants = new TreeSet<>();
    }
}
