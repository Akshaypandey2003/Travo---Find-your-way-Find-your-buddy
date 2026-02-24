package com.blog.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.CommentRequestDTO;
import com.blog.DTO.CommentResponseDTO;
import com.blog.DTO.CommentsPageResponse;
import com.blog.Services.CommentService;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* ---------------- CREATE ---------------- */

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDTO>> addComment(
            @RequestBody CommentRequestDTO request) {

        CommentResponseDTO response = commentService.addComment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response, "Comment added successfully"));
    }

    /* ---------------- UPDATE ---------------- */

//     @PutMapping("/{commentId}")
//     public ResponseEntity<ApiResponse<CommentResponseDTO>> updateComment(
//             @PathVariable String commentId,
//             @RequestBody CommentRequestDTO request) {

//         CommentResponseDTO response =
//                 commentService.updateComment(commentId, request);

//         return ResponseEntity.ok(
//                 new ApiResponse<>(true, response, "Comment updated successfully"));
//     }

    /* ---------------- GET TOP-LEVEL COMMENTS ---------------- */

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<ApiResponse<CommentsPageResponse<CommentResponseDTO>>> getTopLevelComments(
            @PathVariable String blogId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponseDTO> commentsPage =
                commentService.getTopLevelCommentsByBlogId(blogId, pageable);

        CommentsPageResponse<CommentResponseDTO> response =
                new CommentsPageResponse<>(
                        commentsPage.getContent(),
                        commentsPage.getNumber(),
                        commentsPage.getTotalPages(),
                        commentsPage.isLast());

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, "Comments fetched successfully"));
    }

    /* ---------------- GET REPLIES (PAGINATED) ---------------- */

    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<ApiResponse<Page<CommentResponseDTO>>> getReplies(
            @PathVariable String parentCommentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<CommentResponseDTO> replies =
                commentService.getAllReplies(parentCommentId, pageable)
                        .map(comment -> commentService.mapToDTO(comment));

        return ResponseEntity.ok(
                new ApiResponse<>(true, replies, "Replies fetched successfully"));
    }

    /* ---------------- FILTERED REPLIES ---------------- */

    @GetMapping("/blog/{blogId}/parent/{parentCommentId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getRepliesByParent(
            @PathVariable String blogId,
            @PathVariable String parentCommentId) {

        List<CommentResponseDTO> comments =
                commentService.getCommentsByBlogIdAndParentCommentId(blogId, parentCommentId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, comments, "Replies fetched successfully"));
    }

    /* ---------------- LIKE / UNLIKE ---------------- */

//     @PutMapping("/{commentId}/like/{userId}")
//     public ResponseEntity<ApiResponse<CommentResponseDTO>> likeComment(
//             @PathVariable String commentId,
//             @PathVariable String userId) {

//         CommentResponseDTO response =
//                 commentService.updateCommentLike(commentId, userId);

//         return ResponseEntity.ok(
//                 new ApiResponse<>(true, response, "Like status updated"));
//     }
}
