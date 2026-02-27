package com.blog.Services;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;

public interface BlogService {
    
    public ApiResponse<Blog> createBlog(CreateBlogRequest blo, String authorId);
    public ApiResponse<Blog> getBlogById(String blogId);
    public ApiResponse<Blog> updateBlog(String blogId, UpdateBlogRequest blog, String authorId);
    public ApiResponse<Void> deleteBlog(String blogId, String authorId);
    public ApiResponse<Page<Blog>> getAllBlogs(int page, int size);
    public ApiResponse<Page<Blog>> getBlogsByCategory(String category,int page, int size);
    public ApiResponse<Page<Blog>> getBlogsByAuthor(String authorId,int page, int size);
    public ApiResponse<List<Blog>> getBlogsByDateRange(Instant startDate, Instant endDate);
    public ApiResponse<Page<Blog>> getBlogsByKeyword(String keyword,int page, int size);
    public ApiResponse<Page<Blog>> getBlogsByTitle(String title, int page, int size);
    public ApiResponse<Blog> likeBlog(String blogId, String userId);
    public ApiResponse<Blog> updateBlogViews(String blogId);
}
