package com.blog.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.blog.Entity.Blog;
import java.util.List;


public interface BlogRepo extends MongoRepository<Blog,String> {
    
    public List<Blog> findByBlogCategory(String blogCategory);
    public List<Blog> findByBlogAuthor(String authorId);
    public List<Blog> findByBlogTitle(String title);
}