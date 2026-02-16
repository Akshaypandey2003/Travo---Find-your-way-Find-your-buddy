package com.blog.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.Config.JwtProvider;
import com.blog.Controller.CommentController;
import com.blog.DTO.*;
import com.blog.Entity.Comment;
import com.blog.Exceptions.CommentNotFoundException;
import com.blog.Services.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal"})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtProvider jwtProvider;

    /* ---------------- HELPERS ---------------- */

    private CommentRequestDTO sampleRequest() {
        return CommentRequestDTO.builder()
                .blogId("b1")
                .authorId("u1")
                .content("Nice blog!")
                .build();
    }

    private CommentResponseDTO sampleResponse() {
        return CommentResponseDTO.builder()
                .commentId("c1")
                .blogId("b1")
                .authorId("u1")
                .content("Nice blog!")
                .likesCount(0)
                .edited(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /* ---------------- CREATE COMMENT ---------------- */

    @Test
    void shouldAddComment() throws Exception {

        Mockito.when(commentService.addComment(any(CommentRequestDTO.class)))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Nice blog!"))
                .andExpect(jsonPath("$.message").value("Comment added successfully"));
    }

    /* ---------------- UPDATE COMMENT ---------------- */

    @Test
    void shouldUpdateComment() throws Exception {

        CommentResponseDTO updated = sampleResponse();
        updated.setContent("Updated content");
        updated.setEdited(true);

        Mockito.when(commentService.updateComment(eq("c1"), any(CommentRequestDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/comments/c1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("Updated content"))
                .andExpect(jsonPath("$.data.edited").value(true));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingComment() throws Exception {

        Mockito.when(commentService.updateComment(eq("invalid"), any()))
                .thenThrow(new CommentNotFoundException("Comment not found"));

        mockMvc.perform(put("/comments/invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound());
    }

    /* ---------------- GET TOP LEVEL COMMENTS ---------------- */

    @Test
    void shouldGetTopLevelCommentsWithPagination() throws Exception {

        Page<CommentResponseDTO> page = new PageImpl<>(
                List.of(sampleResponse()),
                PageRequest.of(0, 5),
                1);

        Mockito.when(commentService.getTopLevelCommentsByBlogId(eq("b1"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/comments/blog/b1")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comments.length()").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.lastPage").value(true));
    }

    @Test
    void shouldReturn404WhenNoTopLevelComments() throws Exception {

        Mockito.when(commentService.getTopLevelCommentsByBlogId(eq("b1"), any()))
                .thenThrow(new CommentNotFoundException("No comments"));

        mockMvc.perform(get("/comments/blog/b1"))
                .andExpect(status().isNotFound());
    }

    /* ---------------- GET REPLIES (PAGINATED) ---------------- */

    @Test
    void shouldGetReplies() throws Exception {

        Comment comment = new Comment();
        Page<Comment> page = new PageImpl<>(List.of(comment));

        Mockito.when(commentService.getAllReplies(eq("p1"), any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(commentService.mapToDTO(any(Comment.class)))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/comments/p1/replies")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    /* ---------------- FILTERED REPLIES ---------------- */

    @Test
    void shouldGetRepliesByParent() throws Exception {

        Mockito.when(commentService.getCommentsByBlogIdAndParentCommentId("b1", "p1"))
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/comments/blog/b1/parent/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldReturn404WhenNoRepliesByParent() throws Exception {

        Mockito.when(commentService.getCommentsByBlogIdAndParentCommentId("b1", "p1"))
                .thenThrow(new CommentNotFoundException("No replies"));

        mockMvc.perform(get("/comments/blog/b1/parent/p1"))
                .andExpect(status().isNotFound());
    }

    /* ---------------- LIKE / UNLIKE ---------------- */

    @Test
    void shouldLikeComment() throws Exception {

        CommentResponseDTO liked = sampleResponse();
        liked.setLikesCount(1);

        Mockito.when(commentService.updateCommentLike("c1", "u9"))
                .thenReturn(liked);

        mockMvc.perform(put("/comments/c1/like/u9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likesCount").value(1))
                .andExpect(jsonPath("$.message").value("Like status updated"));
    }

    @Test
    void shouldReturn404WhenLikingNonExistingComment() throws Exception {

        Mockito.when(commentService.updateCommentLike("invalid", "u9"))
                .thenThrow(new CommentNotFoundException("Comment not found"));

        mockMvc.perform(put("/comments/invalid/like/u9"))
                .andExpect(status().isNotFound());
    }
}
