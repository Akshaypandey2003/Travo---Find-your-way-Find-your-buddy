package com.blog.Services;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.DTO.ApiResponse;
import com.blog.Entity.Blog;

public interface BlogService {
    
    public ApiResponse<Blog> createBlog(Blog blog);
    public ApiResponse<Blog> getBlogById(String blogId);
    public ApiResponse<Blog> updateBlog(String blogId, Blog blog);
    public ApiResponse<Void> deleteBlog(String blogId);
    public ApiResponse<List<Blog>> getAllBlogs();
    public ApiResponse<List<Blog>> getBlogsByCategory(String category);
    public ApiResponse<List<Blog>> getBlogsByAuthor(String author);
    public ApiResponse<List<Blog>> getBlogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    public ApiResponse<List<Blog>> getBlogsByKeyword(String keyword);
    public ApiResponse<List<Blog>> getBlogsByTitle(String title);
    public ApiResponse<Blog> likeBlog(String blogId, String userId);
    public ApiResponse<Blog> updateBlogViews(String blogId, String userId);
}
