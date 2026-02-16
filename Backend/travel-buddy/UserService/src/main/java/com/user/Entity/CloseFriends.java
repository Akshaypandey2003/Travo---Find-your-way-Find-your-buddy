package com.user.Entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "close_friends")
@CompoundIndex(
    name = "unique_close_friend",
    def = "{'userId': 1, 'closeFriendId': 1}",
    unique = true
)
public class CloseFriends {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String closeFriendId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}