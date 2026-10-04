package com.feed.DTO;

import lombok.Data;

@Data
public class BlogEngagementResponse {
    private String blogId;
    private long likesCount;
    private long commentsCount;
    private long viewsCount;
    private boolean likedByMe;
}