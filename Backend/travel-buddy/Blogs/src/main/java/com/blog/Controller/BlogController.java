package com.blog.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.blog.Entity.Blog;
import com.blog.Services.BlogService;

@RestController
@RequestMapping("/auth/blog")
public class BlogController {
    
    @Autowired
    private BlogService blogService;

    @PostMapping("/create-blog")
    public ResponseEntity<Blog> postBlog(@RequestBody Blog blog)
    {
        System.out.println("Received blog data in Blog service is: "+blog);
       Blog savedBlog = blogService.createBlog(blog);
       return ResponseEntity.status(201).body(savedBlog);
    }

    @PutMapping("/update-blog/{blogId}")
    public ResponseEntity<Blog> updateBlog(@PathVariable String blogId , @RequestBody Blog blog)
    {
        System.out.println("Received blog data in blog service controller (to update)  is: "+blog+" and id is: "+blogId);
        Blog updatedBlog = blogService.updateBlog(blogId, blog);
        return ResponseEntity.status(200).body(updatedBlog);
    }
    @PostMapping("/like-blog/{blogId}/{userId}")
    ResponseEntity<Object> likeBlog(@PathVariable String blogId, @PathVariable String userId)
    {
        return ResponseEntity.ok(blogService.likeBlog(blogId, userId));
    }
    @DeleteMapping("/delete-blog/{blogId}")
    public ResponseEntity<Object> deleteBlog(@PathVariable String blogId)
    {
        blogService.deleteBlog(blogId);
        Map<String,String> result = new HashMap<>();
        result.put("success","Blog Deleted Successfully");
        return ResponseEntity.status(200).body(result);
    }
    @GetMapping("/get-blog/{blogId}")
    public ResponseEntity<Blog> getBlogByBlogId(@PathVariable String blogId)
    {
        Blog blog = blogService.getBlogById(blogId);
        return ResponseEntity.status(200).body(blog);
    }
    @GetMapping("/get-all-blogs")
    public ResponseEntity<List<Blog>> getAllBlogs()
    {
        List<Blog> blogs = blogService.getAllBlogs();
        return ResponseEntity.status(200).body(blogs);
    }
    @GetMapping("/get-blogs-by-category/{category}")
    public ResponseEntity<List<Blog>> getBlogsByCategory(@PathVariable String category)
    {
        List<Blog> blogs = blogService.getBlogsByCategory(category);
        return ResponseEntity.status(200).body(blogs);
    }

    @GetMapping("/get-blogs-by-author/{authorId}")
    public ResponseEntity<List<Blog>> getBlogsByAuthor(@PathVariable String authorId)
    {
        System.out.println("Executing get-blogs-by-author in blog service.");
        List<Blog> blogs = blogService.getBlogsByAuthor(authorId);
        return ResponseEntity.status(200).body(blogs);
    }

    @GetMapping("/get-blogs-by-keyword/{keyword}")
    public ResponseEntity<List<Blog>> getBlogsByKeyword(@PathVariable String keyword)
    {

        List<Blog> blogs = blogService.getBlogsByKeyword(keyword);
        return ResponseEntity.status(200).body(blogs);
    }
    @GetMapping("/get-blogs-by-title/{title}")
    public ResponseEntity<List<Blog>> getBlogsByTitle(@PathVariable String title)
    {
        List<Blog> blogs = blogService.getBlogsByTitle(title);
        return ResponseEntity.status(200).body(blogs);
    }
    @GetMapping("/get-blogs-by-date-range/{startDate}/{endDate}")
    public ResponseEntity<List<Blog>> getBlogsByDateRange(@PathVariable String startDate, @PathVariable String endDate)
    {
        LocalDateTime startDateTime = LocalDateTime.parse(startDate);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate);
        List<Blog> blogs = blogService.getBlogsByDateRange(startDateTime, endDateTime);
        return ResponseEntity.status(200).body(blogs);
    }
    @PostMapping("/update-blog-views/{blogId}/{userId}")
    public ResponseEntity<Object> updateBlogViews(@PathVariable String blogId, @PathVariable String userId)
    {
        blogService.updateBlogViews(blogId, userId);
        Map<String,String> result = new HashMap<>();
        result.put("success","Blog Views Updated Successfully");
        return ResponseEntity.status(200).body(result);
    }

}
