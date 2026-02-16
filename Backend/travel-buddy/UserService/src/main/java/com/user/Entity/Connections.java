package com.user.Entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.user.Enum.ConnectionStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
@CompoundIndex(
    name = "unique_follow_relationship",
    def = "{'followerId': 1, 'followingId': 1}",
    unique = true
)
public class Connections {
    
    @Id
    private String connectionId;

    @Indexed
    private String followerId;

    @Indexed
    private String followingId;

    @Indexed
    private ConnectionStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

   
}
