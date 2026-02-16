package com.blog.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.DTO.ApiResponse;
import com.blog.Entity.Blog;
import com.blog.Services.BlogService;

@RestController
@RequestMapping("/blog")
public class BlogController {
    
    @Autowired
    private BlogService blogService;

    @PostMapping("/create-blog")
    public ResponseEntity<?> postBlog(@RequestBody Blog blog)
    {
        System.out.println("Received blog data in Blog service is: "+blog);
       ApiResponse<Blog> savedBlog = blogService.createBlog(blog);
       return ResponseEntity.status(201).body(savedBlog);
    }

    @PutMapping("/update-blog/{blogId}")
    public ResponseEntity<?> updateBlog(@PathVariable String blogId , @RequestBody Blog blog)
    {
        System.out.println("Received blog data in blog service controller (to update)  is: "+blog+" and id is: "+blogId);
        ApiResponse<Blog> updatedBlog = blogService.updateBlog(blogId, blog);
        return ResponseEntity.status(200).body(updatedBlog);
    }
    @PostMapping("/like-blog/{blogId}/{userId}")
    ResponseEntity<?> likeBlog(@PathVariable String blogId, @PathVariable String userId)
    {
        ApiResponse<Blog> response = blogService.likeBlog(blogId, userId);

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete-blog/{blogId}")
    public ResponseEntity<?> deleteBlog(@PathVariable String blogId)
    {
        ApiResponse<Void> response = blogService.deleteBlog(blogId);
       
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/get-blog/{blogId}")
    public ResponseEntity<?> getBlogByBlogId(@PathVariable String blogId)
    {
        ApiResponse<Blog> response = blogService.getBlogById(blogId);
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/get-all-blogs")
    public ResponseEntity<?> getAllBlogs(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size)
    {
        ApiResponse<List<Blog>> response = blogService.getAllBlogs();
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/get-blogs-by-category/{category}")
    public ResponseEntity<?> getBlogsByCategory(@PathVariable String category)
    {
       ApiResponse<List<Blog>> response = blogService.getBlogsByCategory(category);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get-blogs-by-author/{authorId}")
    public ResponseEntity<?> getBlogsByAuthor(@PathVariable String authorId)
    {
        ApiResponse<List<Blog>> response = blogService.getBlogsByAuthor(authorId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get-blogs-by-keyword/{keyword}")
    public ResponseEntity<?> getBlogsByKeyword(@PathVariable String keyword)
    {

        ApiResponse<List<Blog>> response = blogService.getBlogsByKeyword(keyword);
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/get-blogs-by-title/{title}")
    public ResponseEntity<?> getBlogsByTitle(@PathVariable String title)
    {
        ApiResponse<List<Blog>> response = blogService.getBlogsByTitle(title);
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/get-blogs-by-date-range/{startDate}/{endDate}")
    public ResponseEntity<?> getBlogsByDateRange(@PathVariable String startDate, @PathVariable String endDate)
    {
        LocalDateTime startDateTime = LocalDateTime.parse(startDate);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate);
        ApiResponse<List<Blog>> response = blogService.getBlogsByDateRange(startDateTime, endDateTime);
        return ResponseEntity.status(200).body(response);
    }
    @PostMapping("/update-blog-views/{blogId}/{userId}")
    public ResponseEntity<?> updateBlogViews(@PathVariable String blogId, @PathVariable String userId)
    {
        ApiResponse<Blog> updatedBlog = blogService.updateBlogViews(blogId, userId);
        return ResponseEntity.status(200).body(updatedBlog);
    }

}
