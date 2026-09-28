package com.blog.DTO;

import java.time.Instant;
import java.util.List;

import com.blog.Entity.Blog;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DiscoveryBlogResponse {
    String resourceId;
    String authorId;
    String authorName;
    String authorProfilePic;
    String caption;
    List<String> images;
    Instant createdAt;

    public static DiscoveryBlogResponse from(Blog blog) {
        return DiscoveryBlogResponse.builder()
                .resourceId(blog.getId())
                .authorId(blog.getAuthorId())
                .authorName(blog.getAuthorName())
                .authorProfilePic(blog.getAuthorProfilePic())
                .caption(blog.getCaption())
                .images(blog.getImageUrls())
                .createdAt(blog.getCreatedAt())
                .build();
    }
}