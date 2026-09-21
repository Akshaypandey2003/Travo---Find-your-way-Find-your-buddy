package com.blog.Controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.CreateBlogRequest;
import com.blog.DTO.UpdateBlogRequest;
import com.blog.Entity.Blog;
import com.blog.Exceptions.InvalidRequestException;
import com.blog.Services.BlogService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BlogController {

    private final BlogService blogService;

    // ✅ Create Blog
    @PostMapping
    public ResponseEntity<ApiResponse<Blog>> createBlog(
            @Valid @RequestBody CreateBlogRequest request,
            @AuthenticationPrincipal String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("Authenticated user is required");
        }
        log.info("Creating blog for user: {}", userId);

        ApiResponse<Blog> response = blogService.createBlog(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Update Blog
    @PutMapping("/{blogId}")
    public ResponseEntity<ApiResponse<Blog>> updateBlog(
            @PathVariable String blogId,
            @Valid @RequestBody UpdateBlogRequest request,
            @AuthenticationPrincipal String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("Authenticated user is required");
        }
        return ResponseEntity.ok(blogService.updateBlog(blogId, request, userId));
    }

    // ✅ Like Blog
    @PostMapping("/{blogId}/like")
    public ResponseEntity<ApiResponse<Blog>> likeBlog(
            @PathVariable String blogId,
            @AuthenticationPrincipal String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("Authenticated user is required");
        }
        return ResponseEntity.ok(blogService.likeBlog(blogId, userId));
    }

    // ✅ Delete Blog
    @DeleteMapping("/{blogId}")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(
            @PathVariable String blogId,
            @AuthenticationPrincipal String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("Authenticated user is required");
        }
        return ResponseEntity.ok(blogService.deleteBlog(blogId, userId));
    }

    // ✅ Get Blog by ID
    @GetMapping("/{blogId}")
    public ResponseEntity<ApiResponse<Blog>> getBlog(@PathVariable String blogId) {
        return ResponseEntity.ok(blogService.getBlogById(blogId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<Page<Blog>>> getAllBlogs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        ApiResponse<Page<Blog>> response = blogService.getAllBlogs(page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-blogs-by-category/{category}")
    public ResponseEntity<?> getBlogsByCategory(@PathVariable String category,
         @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        ApiResponse<Page<Blog>> response = blogService.getBlogsByCategory(category, page, size);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getBlogsByAuthor(@PathVariable String authorId,
        @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        ApiResponse<Page<Blog>> response = blogService.getBlogsByAuthor(authorId,page,size);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get-blogs-by-keyword/{keyword}")
    public ResponseEntity<?> getBlogsByKeyword(@PathVariable String keyword,
        @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {

        ApiResponse<Page<Blog>> response = blogService.getBlogsByKeyword(keyword,page,size);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get-blogs-by-title/{title}")
    public ResponseEntity<?> getBlogsByTitle(@PathVariable String title,
        @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        ApiResponse<Page<Blog>> response = blogService.getBlogsByTitle(title, page, size);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get-blogs-by-date-range/{startDate}/{endDate}")
    public ResponseEntity<?> getBlogsByDateRange(@PathVariable String startDate, @PathVariable String endDate) {
        Instant startDateTime = Instant.parse(startDate);
        Instant endDateTime = Instant.parse(endDate);
        ApiResponse<List<Blog>> response = blogService.getBlogsByDateRange(startDateTime, endDateTime);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/{blogId}/view")
    public ResponseEntity<?> updateBlogViews(@PathVariable String blogId,
            @AuthenticationPrincipal String userId) {
        ApiResponse<Blog> updatedBlog = blogService.updateBlogViews(blogId);
        return ResponseEntity.status(200).body(updatedBlog);
    }

}
