package com.blog.Entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "blogs")
public class Blog {

    @Id
    private String id;

    @Indexed
    private String authorId;

    private String authorName;
    private String authorProfilePic;

    private String title;
    private String content;
    private String caption;
    private String category;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Builder.Default
    private List<String> cloudinaryPublicIds = new ArrayList<>();

    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    // 🔥 Store only counts — NOT full lists
    @Builder.Default
    private long likeCount = 0;

    @Builder.Default
    private long commentCount = 0;

    @Builder.Default
    private long shareCount = 0;

    @Builder.Default
    private long viewCount = 0;
}