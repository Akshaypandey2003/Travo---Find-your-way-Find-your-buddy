package com.feed.DTO;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class DiscoveryBlogResponse {
    private String resourceId;
    private String authorId;
    private String authorName;
    private String authorProfilePic;
    private String caption;
    private List<String> images;
    private Instant createdAt;
}