package com.blog.Services;

import com.blog.Entity.Blog;

public interface EventPublisher {
    void publishBlogCreated(Blog blog);
    void publishBlogDeleted(String blogId, String authorId);
}
