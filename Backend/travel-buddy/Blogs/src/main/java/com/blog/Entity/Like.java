package com.blog.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.blog.Enum.ResourceType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "likes")
@CompoundIndex(
    def = "{'resourceId': 1, 'resourceType': 1, 'userId': 1}",
    unique = true
)
public class Like {

    @Id
    private String id;

    private String resourceId;      // blogId or commentId
    private ResourceType resourceType;

    private String userId;

    @Builder.Default
    private Instant createdAt = Instant.now();
}