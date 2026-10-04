package com.blog.DTO;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BlogEngagementResponse {
    String blogId;
    long likesCount;
    long commentsCount;
    long viewsCount;
    boolean likedByMe;
}