package com.blog.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "comments")
@CompoundIndex(def = "{'blogId': 1, 'createdAt': -1}")
@CompoundIndex(def = "{'parentCommentId': 1, 'createdAt': -1}")
public class Comment {

    @Id
    private String id;

    private String blogId;

    private String authorId;
    private String authorName;
    private String authorProfilePic;

    private String content;

    private String parentCommentId; // null if top-level comment

    @Builder.Default
    private Instant createdAt = Instant.now();


    @Builder.Default
    private long likeCount = 0;

    private int depth;
}