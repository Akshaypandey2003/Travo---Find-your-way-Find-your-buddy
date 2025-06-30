package com.blog.comments.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.comments.DTO.CommentsPageResponse;
import com.blog.comments.Entity.Comment;
import com.blog.comments.Services.CommentService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/auth/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/post")
    ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.addComment(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    @PutMapping("/update-comment/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String commentId, @RequestBody Comment comment) {

        Comment savedComment = commentService.updateComment(commentId, comment);
        return new ResponseEntity<>(savedComment, HttpStatus.OK);
    }

    @GetMapping("/get-comments/{blogId}")
    public ResponseEntity<CommentsPageResponse> getComments(@PathVariable String blogId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Comment> commentPage = commentService.getTopLevelCommentsByBlogId(blogId, pageable);

        for (Comment comment : commentPage.getContent()) {
            comment.setReplies(commentService.getAllReplies(comment.getCommentId()));
        }

        CommentsPageResponse response = new CommentsPageResponse(
                commentPage.getContent(),
                commentPage.getNumber(),
                commentPage.getTotalPages(),
                commentPage.isLast());
                
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-comments/{blogId}/{parentCommentId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String blogId,
            @PathVariable String parentCommentId) {
        List<Comment> comments = commentService.getCommentsByBlogIdAndParentCommentId(blogId, parentCommentId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/get-comments/{blogId}/{parentCommentId}/{repliedToUserId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String blogId, @PathVariable String parentCommentId,
            @PathVariable String repliedToUserId) {
        List<Comment> comments = commentService.getCommentsByBlogIdAndParentCommentIdAndRepliedToUserId(blogId,
                parentCommentId, repliedToUserId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/like-comment/{commentId}/{userId}")
    public ResponseEntity<Comment> likeComment(@PathVariable String commentId, @PathVariable String userId) {
        Comment comment = commentService.updateCommentLike(commentId, userId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}
