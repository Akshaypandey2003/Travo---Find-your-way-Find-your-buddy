package com.blog.Services;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.BlogEngagementResponse;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;

public interface BlogService {

    Page<Blog> getBlogsByAuthors(List<String> authorIds, int size);
    
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
    public List<BlogEngagementResponse> getEngagement(List<String> blogIds, String userId);
    public void incrementCommentCount(String blogId);
    public ApiResponse<BlogEngagementResponse> likeBlog(String blogId, String userId);
    public ApiResponse<BlogEngagementResponse> updateBlogViews(String blogId, String userId);
}
