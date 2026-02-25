package com.events.Blog;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogCreatedEvent {
    
    private String id;
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
    private long likeCount;
    private long commentCount;
    private long shareCount;
    private long viewCount;
}
