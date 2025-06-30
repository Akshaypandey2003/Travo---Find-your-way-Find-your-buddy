package com.blog.Services;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.Entity.Blog;

public interface BlogService {
    
    public Blog createBlog(Blog blog);
    public Blog getBlogById(String blogId);
    public Blog updateBlog(String blogId, Blog blog);
    public void deleteBlog(String blogId);
    public List<Blog> getAllBlogs();
    public List<Blog> getBlogsByCategory(String category);
    public List<Blog> getBlogsByAuthor(String author);
    public List<Blog> getBlogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    public List<Blog> getBlogsByKeyword(String keyword);
    public List<Blog> getBlogsByTitle(String title);
    public Blog likeBlog(String blogId, String userId);
    public Blog updateBlogViews(String blogId, String userId);
}
