package com.gateway.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.gateway.Entity.Blog;
import com.gateway.Entity.Message;
import com.gateway.Entity.MessageResponse;
import com.gateway.Entity.Notification;
import com.gateway.Services.BlogService;

@RestController
@RequestMapping("/auth/blog")
public class BlogController {

    @PostMapping("/create-blog")
    public ResponseEntity<Blog> postBlog(@RequestBody Blog blog)
    {
        try {
            System.out.println("Received blog data in apigateway  is: "+blog);
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Blog> entity = new HttpEntity<>(blog);
            String blogServiceUrl = "http://localhost:8089/auth/blog/create-blog";

            ResponseEntity<Blog> response = restTemplate.exchange(blogServiceUrl,HttpMethod.POST,entity,Blog.class);
            
            response.getBody().setMessageResponse(new MessageResponse("Blog Posted Successfully","success"));
            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error posting blog: " + e.getMessage(),"error");
           Blog blogResponse = new Blog();
           blog.setMessageResponse(msg);
           return new ResponseEntity<>(blogResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update-blog/{blogId}")
    public ResponseEntity<Blog> updateBlog(@RequestBody Blog blog , @PathVariable String blogId)
    {
        try {
            System.out.println("Received blog data in apigateway (to update)  is: "+blog);
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Blog> entity = new HttpEntity<>(blog);
            String blogServiceUrl = "http://localhost:8089/auth/blog/update-blog/"+blogId;

            ResponseEntity<Blog> response = restTemplate.exchange(blogServiceUrl,HttpMethod.PUT,entity,Blog.class);
            
            response.getBody().setMessageResponse(new MessageResponse("Blog Updated Successfully","success"));
            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
          
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating blog: " + e.getMessage(),"error");
           Blog blogResponse = new Blog();
           blog.setMessageResponse(msg);
           return new ResponseEntity<>(blogResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete-blog/{blogId}")
    public ResponseEntity<Object> deleteBlog(@PathVariable String blogId)
    {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String blogServiceUrl = "http://localhost:8089/auth/blog/delete-blog/"+blogId;

            ResponseEntity<Object> response = restTemplate.exchange(blogServiceUrl,HttpMethod.DELETE,null,Object.class);
            
              return new ResponseEntity<>(new MessageResponse("Blog Delete Successfully.","success"),HttpStatus.OK);
            // return new ResponseEntity<>(response.getBody(),response.getStatusCode());
          
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating blog: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-blog/{authorId}")
    public ResponseEntity<List<Blog>> getBlogByUserId(@PathVariable String authorId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String blogServiceUrl = "http://localhost:8089/api/blogs/get-blogs-by-author/" + authorId;

            ResponseEntity<List<Blog>> response = restTemplate.exchange(
                    blogServiceUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Blog>>() {
                    });

            // List<Blog> blogs = blogService.getBlogsByAuthor(authorId);
            // return ResponseEntity.ok(blogs);


            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get-all-blogs")
    public ResponseEntity<List<Blog>> getAllBlogs()
    {
        try 
        {
            RestTemplate restTemplate = new RestTemplate();
            String blogServiceUrl = "http://localhost:8089/auth/blog/get-all-blogs";

            ResponseEntity<List<Blog>> response = restTemplate.exchange(blogServiceUrl,HttpMethod.GET,null,new ParameterizedTypeReference<List<Blog>>() {
            });
            return new ResponseEntity<>(response.getBody(),response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error posting blog: " + e.getMessage(),"error");
            List<Blog> blogs = new ArrayList<>();
            Blog blog = new Blog();
            blog.setMessageResponse(msg);
            blogs.add(blog);
            return new ResponseEntity<>(blogs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/like-blog/{blogId}/{userId}")
    ResponseEntity<Object> likeBlog(@PathVariable String blogId, @PathVariable String userId)
    {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Update blog like
            String blogServiceUrl = "http://localhost:8089/auth/blog/like-blog/"+blogId+"/"+userId;
            ResponseEntity<Blog> response = restTemplate.exchange(blogServiceUrl,HttpMethod.POST,null,Blog.class);
            
            Blog updatedBlog = (Blog)response.getBody();
            
            if(!updatedBlog.getBlogLikes().contains(userId))
            {
                 MessageResponse msg = new MessageResponse("Blog Unliked Successfully.","success");
                 updatedBlog.setMessageResponse(msg);

                 return new ResponseEntity<>(updatedBlog,response.getStatusCode());
            }

             //Creating notification object
            Notification notification = new Notification();
            notification.setNotificationFrom(userId);
            notification.setNotificationTo(updatedBlog.getBlogAuthor());
            notification.setType(Notification.NotificationType.LIKE);  
            notification.setMessage("has liked your post.");
                   
           //sending notification object to notification service(User service)
            String notificationUrl = "http://localhost:8088/auth/user/notification/send-notification";

            
            HttpEntity<Notification> entity = new HttpEntity<>(notification);

            ResponseEntity<?> responseNotification = restTemplate.exchange(
                   notificationUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<Object>() {
                    });

            System.out.println("Notification sent successfully: " + response.getBody());

            MessageResponse msg = new MessageResponse("Blog Liked Successfully.","success");
            updatedBlog.setMessageResponse(msg);

            return new ResponseEntity<>(updatedBlog,response.getStatusCode());
          
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating blog like: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update-blog-views/{blogId}/{userId}")
    ResponseEntity<Object> updateBlogViews(@PathVariable String blogId, @PathVariable String userId)
    {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String blogServiceUrl = "http://localhost:8089/auth/blog/update-blog-views/"+blogId+"/"+userId;

            ResponseEntity<Object> response = restTemplate.exchange(blogServiceUrl,HttpMethod.POST,null,Object.class);
            
              return new ResponseEntity<>(new MessageResponse("Blog view updated Successfully.","success"),HttpStatus.OK);
            // return new ResponseEntity<>(response.getBody(),response.getStatusCode());
          
        } catch (Exception e) {
           MessageResponse msg = new MessageResponse("Error updating blog views: " + e.getMessage(),"error");
           return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}