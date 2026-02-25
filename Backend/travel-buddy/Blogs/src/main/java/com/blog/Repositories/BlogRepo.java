package com.blog.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.Entity.Blog;


public interface BlogRepo extends MongoRepository<Blog,String> {
    
    public Page<Blog> findByBlogCategory(String blogCategory,Pageable pageable);
    public Page<Blog> findByBlogAuthorId(String authorId,Pageable pageable);
    public Page<Blog> findByBlogTitle(String title,Pageable pageable);
    public Page<Blog> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String keyword, String contentKeyword, Pageable pageable);
}