package com.gateway.Services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gateway.Entity.Comment;

@FeignClient(name="BLOGS-COMMENT-SERVICE")
public interface CommentService {
    
    @GetMapping("/auth/comment/post")
    Comment addComment(@RequestBody Comment comment);
}
