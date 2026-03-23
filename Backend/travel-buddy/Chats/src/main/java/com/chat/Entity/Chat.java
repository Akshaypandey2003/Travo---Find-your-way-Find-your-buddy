package com.chat.Entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "chats")
@CompoundIndexes({
        @CompoundIndex(name = "idx_chat_participants_recent", def = "{'participants': 1, 'recentConversationAt': -1}"),
        @CompoundIndex(name = "idx_chat_group_recent", def = "{'groupChat': 1, 'recentConversationAt': -1}")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private String chatId;

    @Version
    private Long version;

    private boolean groupChat;

    @Builder.Default
    private Set<String> favoriteBy = new HashSet<>(); // userIds who marked the chat as favorite

    @Indexed
    @Builder.Default
    private Set<ChatParticipant> participants = new HashSet<>();

    @Builder.Default
    private Set<String> groupAdmin = new TreeSet<>(); // only if isGroupChat == true

    private String groupName; // null for 1-to-1 chats, set for group chats
    private String groupDescription;
    private String groupImageUrl; // null for 1-to-1 chats, set for group chats

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant recentConversationAt;
}
