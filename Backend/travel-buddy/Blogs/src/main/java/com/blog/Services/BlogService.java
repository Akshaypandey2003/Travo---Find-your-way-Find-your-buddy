package com.blog.Services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;

public interface BlogService {
    
    public ApiResponse<Blog> createBlog(CreateBlogRequest blo);
    public ApiResponse<Blog> getBlogById(String blogId);
    public ApiResponse<Blog> updateBlog(String blogId, UpdateBlogRequest blog, String authorId);
    public ApiResponse<Void> deleteBlog(String blogId, String authorId);
    public ApiResponse<List<Blog>> getAllBlogs();
    public ApiResponse<List<Blog>> getBlogsByCategory(String category);
    public ApiResponse<List<Blog>> getBlogsByAuthor(String author);
    public ApiResponse<List<Blog>> getBlogsByDateRange(Instant startDate, Instant endDate);
    public ApiResponse<List<Blog>> getBlogsByKeyword(String keyword);
    public ApiResponse<List<Blog>> getBlogsByTitle(String title);
    public ApiResponse<Blog> likeBlog(String blogId, String userId);
    public ApiResponse<Blog> updateBlogViews(String blogId);
}
