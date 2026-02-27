package com.blog.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.blog.Controller.CommentController;
import com.blog.DTO.ApiResponse;
import com.blog.DTO.CommentRequestDTO;
import com.blog.DTO.CommentResponseDTO;
import com.blog.Entity.Comment;
import com.blog.Exceptions.CommentNotFoundException;
import com.blog.Exceptions.InvalidRequestException;
import com.blog.Services.CommentService;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    private CommentController commentController;

    @BeforeEach
    void setUp() {
        commentController = new CommentController(commentService);
    }

    @Test
    void addComment_success_returnsCreated() {
        CommentRequestDTO request = CommentRequestDTO.builder()
                .blogId("b1")
                .content("Nice post")
                .build();

        CommentResponseDTO dto = CommentResponseDTO.builder()
                .commentId("c1")
                .blogId("b1")
                .authorId("u1")
                .content("Nice post")
                .createdAt(Instant.now())
                .build();

        when(commentService.addComment(request, "u1")).thenReturn(dto);

        ResponseEntity<ApiResponse<CommentResponseDTO>> response = commentController.addComment(request, "u1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getCommentId()).isEqualTo("c1");
    }

    @Test
    void addComment_throwsInvalidRequest_whenUserMissing() {
        CommentRequestDTO request = CommentRequestDTO.builder()
                .blogId("b1")
                .content("x")
                .build();

        assertThatThrownBy(() -> commentController.addComment(request, " "))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void getTopLevelComments_success_returnsWrappedPage() {
        CommentResponseDTO dto = CommentResponseDTO.builder()
                .commentId("c1")
                .blogId("b1")
                .authorId("u1")
                .content("x")
                .createdAt(Instant.now())
                .build();

        Page<CommentResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);
        when(commentService.getTopLevelCommentsByBlogId("b1", PageRequest.of(0, 5))).thenReturn(page);

        ResponseEntity<ApiResponse<com.blog.DTO.CommentsPageResponse<CommentResponseDTO>>> response =
                commentController.getTopLevelComments("b1", 0, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getComments()).hasSize(1);
    }

    @Test
    void getReplies_success_returnsMappedPage() {
        Comment comment = Comment.builder().id("c1").blogId("b1").authorId("u1").content("r").build();
        CommentResponseDTO dto = CommentResponseDTO.builder()
                .commentId("c1")
                .blogId("b1")
                .authorId("u1")
                .content("r")
                .createdAt(Instant.now())
                .build();

        when(commentService.getAllReplies("p1", PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(comment), PageRequest.of(0, 5), 1));
        when(commentService.mapToDTO(comment)).thenReturn(dto);

        ResponseEntity<ApiResponse<Page<CommentResponseDTO>>> response =
                commentController.getReplies("p1", 0, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    void likeComment_success_andNotFound() {
        CommentResponseDTO dto = CommentResponseDTO.builder()
                .commentId("c1")
                .likesCount(1)
                .createdAt(Instant.now())
                .build();
        when(commentService.updateCommentLike("c1", "u1")).thenReturn(dto);
        when(commentService.updateCommentLike("missing", "u1"))
                .thenThrow(new CommentNotFoundException("not found"));

        ResponseEntity<ApiResponse<CommentResponseDTO>> success = commentController.likeComment("c1", "u1");
        assertThat(success.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(success.getBody().getData().getLikesCount()).isEqualTo(1);

        assertThatThrownBy(() -> commentController.likeComment("missing", "u1"))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void getRepliesByParent_success() {
        CommentResponseDTO dto = CommentResponseDTO.builder().commentId("c1").build();
        when(commentService.getCommentsByBlogIdAndParentCommentId(eq("b1"), eq("p1")))
                .thenReturn(List.of(dto));

        ResponseEntity<ApiResponse<List<CommentResponseDTO>>> response =
                commentController.getRepliesByParent("b1", "p1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).hasSize(1);
    }
}
