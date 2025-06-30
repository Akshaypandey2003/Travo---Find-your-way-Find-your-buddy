package com.gateway.Services;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gateway.Entity.Blog;

@FeignClient(name="BLOGS-SERVICE")
public interface BlogService {
    
    @GetMapping("/api/blogs/get-blogs-by-author/{authorId}")
    List<Blog> getBlogsByAuthor(@PathVariable String authorId);
}
