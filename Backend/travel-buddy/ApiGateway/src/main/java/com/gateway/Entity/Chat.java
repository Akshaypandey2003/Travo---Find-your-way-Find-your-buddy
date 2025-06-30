package com.gateway.Entity;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Chat {

    private String chatId;

    private boolean groupChat;
    private Set<String> favoriteBy = new HashSet<>(); // true if the chat is marked as favorite by the user
    private String groupName; // null for 1-to-1 chats, set for group chats
    private String groupDescription;
    private String groupImageUrl; // null for 1-to-1 chats, set for group chats
    private Set<String> participants = new TreeSet<>(); // userIds

    private Set<String> groupAdmin = new TreeSet<>(); // only if isGroupChat == true

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

