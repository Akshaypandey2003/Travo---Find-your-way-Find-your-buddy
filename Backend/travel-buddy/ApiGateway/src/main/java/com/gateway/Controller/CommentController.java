package com.gateway.Controller;

import java.util.List;

import javax.print.attribute.standard.PageRanges;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gateway.DTO.CommentsPageResponse;
import com.gateway.Entity.Blog;
import com.gateway.Entity.Comment;
import com.gateway.Entity.MessageResponse;
import com.gateway.Entity.Notification;
import com.gateway.Services.CommentService;

@RestController
@RequestMapping("/auth/api/comment")
public class CommentController {

    @PostMapping("/post/{postAuthor}")
    public ResponseEntity<Object> createComment(@RequestBody Comment comment, @PathVariable String postAuthor) {
        System.out.println("Post author is: "+postAuthor);
        try {
            System.out.println("Received comment data in apigateway is: " + comment);
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Comment> entity = new HttpEntity<>(comment);
            String commentServiceUrl = "http://localhost:8091/auth/comment/post";

            
            ResponseEntity<Comment> response = restTemplate.exchange(commentServiceUrl, HttpMethod.POST, entity,
                    Comment.class);
            
            Comment savedComment = response.getBody();
            
             //Creating notification object
            Notification notification = new Notification();
            notification.setNotificationFrom(savedComment.getAuthorId());
            notification.setNotificationTo(postAuthor);
            notification.setType(Notification.NotificationType.COMMENT);  
            notification.setMessage("has commented on your post.");
                   
           //sending notification object to notification service(User service)
            String notificationUrl = "http://localhost:8088/auth/user/notification/send-notification";

            
            HttpEntity<Notification> notificatoinEntity = new HttpEntity<>(notification);

            ResponseEntity<?> responseNotification = restTemplate.exchange(
                   notificationUrl, HttpMethod.POST, notificatoinEntity, new ParameterizedTypeReference<Object>() {
                    });

            System.out.println("Notification sent successfully: " + response.getBody());

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error posting blog: " + e.getMessage(), "error");

            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-comment/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable String commentId, @RequestBody Comment comment) {

        try {
            System.out.println("Received comment data to update in apigateway is: " + comment);
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Comment> entity = new HttpEntity<>(comment);
            String commentServiceUrl = "http://localhost:8091/auth/comment/update-comment/" + commentId;

            ResponseEntity<Comment> response = restTemplate.exchange(commentServiceUrl, HttpMethod.PUT, entity,
                    Comment.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error updating blog: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-comments/{blogId}")
    public ResponseEntity<Object> getComments(@PathVariable String blogId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String commentServiceUrl = "http://localhost:8091/auth/comment/get-comments/" + blogId+ "?page=" + page + "&size=" + size;

            ResponseEntity<CommentsPageResponse> response = restTemplate.exchange(
                    commentServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<CommentsPageResponse>() {
                    });

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching blogs: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-comments/{blogId}/{parentCommentId}")
    public ResponseEntity<Object> getComments(@PathVariable String blogId,
            @PathVariable String parentCommentId) {
         try {
            RestTemplate restTemplate = new RestTemplate();

            String commentServiceUrl = "http://localhost:8091/auth/comment/get-comments/" + blogId+"/" + parentCommentId;

            ResponseEntity<List<Comment>> response = restTemplate.exchange(
                    commentServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Comment>>() {
                    });

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching blogs: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-comments/{blogId}/{parentCommentId}/{repliedToUserId}")
    public ResponseEntity<Object> getComments(@PathVariable String blogId, @PathVariable String parentCommentId,
            @PathVariable String repliedToUserId) {
         try {
            RestTemplate restTemplate = new RestTemplate();

            String commentServiceUrl = "http://localhost:8091/auth/comment/get-comments/" + blogId+"/" + parentCommentId + "/" + repliedToUserId;

            ResponseEntity<List<Comment>> response = restTemplate.exchange(
                    commentServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Comment>>() {
                    });

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error fetching blogs: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/like-comment/{commentId}/{userId}")
    public ResponseEntity<Object> likeComment(@PathVariable String commentId, @PathVariable String userId) {
         try {
            RestTemplate restTemplate = new RestTemplate();

            String commentServiceUrl = "http://localhost:8091/auth/comment/like-comment/" + commentId+"/"+userId;

            ResponseEntity<Comment> response = restTemplate.exchange(
                    commentServiceUrl,
                    HttpMethod.PUT,
                    null,
                    new ParameterizedTypeReference<Comment>() {
                    });

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            MessageResponse msg = new MessageResponse("Error liking blogs: " + e.getMessage(), "error");
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
