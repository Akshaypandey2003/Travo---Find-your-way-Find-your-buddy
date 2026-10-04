package com.feed.DTO;

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
public class FeedItemResponse {

    private String eventId;
    private String resourceType;

    private String authorId;
    private String authorName;
    private String authorProfilePic;

    private String resourceId;
    private String caption;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    private String visibility;

    private Instant createdAt;

    private long viewsCount;
    private long likesCount;
    private long commentsCount;
    private boolean likedByMe;

    private String thumbnailUrl;
}
