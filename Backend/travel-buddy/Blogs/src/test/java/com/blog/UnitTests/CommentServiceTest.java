package com.blog.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.CommentRequestDTO;
import com.blog.DTO.CommentResponseDTO;
import com.blog.Entity.Blog;
import com.blog.Entity.Comment;
import com.blog.Entity.Like;
import com.blog.Enum.ResourceType;
import com.blog.Exceptions.CommentNotFoundException;
import com.blog.Exceptions.InvalidRequestException;
import com.blog.Repositories.CommentRepository;
import com.blog.Repositories.LikeRepository;
import com.blog.Services.BlogService;
import com.blog.Services.NotificationProducer;
import com.blog.ServicesImpl.CommentServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepo;
    @Mock
    private BlogService blogService;
    @Mock
    private LikeRepository likeRepo;
    @Mock
    private NotificationProducer notificationProducer;

    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepo, blogService, likeRepo, notificationProducer);
    }

    @Test
    void addComment_success_createsAndNotifies() {
        CommentRequestDTO request = CommentRequestDTO.builder()
                .blogId("b1")
                .content("Nice")
                .build();

        Comment saved = Comment.builder()
                .id("c1")
                .blogId("b1")
                .authorId("u1")
                .content("Nice")
                .createdAt(Instant.now())
                .build();

        Blog blog = Blog.builder().id("b1").authorId("owner").title("Blog").build();

        when(commentRepo.save(any(Comment.class))).thenReturn(saved);
        when(blogService.getBlogById("b1")).thenReturn(new ApiResponse<>(true, blog, "ok"));

        CommentResponseDTO response = commentService.addComment(request, "u1");

        assertThat(response.getCommentId()).isEqualTo("c1");
        verify(notificationProducer).sendCommentNotification("u1", "owner", "b1", "Blog");
    }

    @Test
    void addComment_throwsBadRequest_whenAuthorMissing() {
        CommentRequestDTO request = CommentRequestDTO.builder().blogId("b1").content("Nice").build();

        assertThatThrownBy(() -> commentService.addComment(request, " "))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void addComment_throwsNotFound_whenParentMissing() {
        CommentRequestDTO request = CommentRequestDTO.builder()
                .blogId("b1")
                .content("reply")
                .parentCommentId("missing-parent")
                .build();
        when(commentRepo.findById("missing-parent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(request, "u1"))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void addComment_throwsIllegalArgument_whenDepthExceeded() {
        Comment parent = Comment.builder().id("p1").depth(2).build();
        when(commentRepo.findById("p1")).thenReturn(Optional.of(parent));

        CommentRequestDTO request = CommentRequestDTO.builder()
                .blogId("b1")
                .content("too deep")
                .parentCommentId("p1")
                .build();

        assertThatThrownBy(() -> commentService.addComment(request, "u1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getTopLevelComments_returnsPage() {
        Comment c = Comment.builder().id("c1").blogId("b1").authorId("u1").content("x").build();
        Page<Comment> page = new PageImpl<>(List.of(c));

        when(commentRepo.findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(eq("b1"), any()))
                .thenReturn(page);

        Page<CommentResponseDTO> result = commentService.getTopLevelCommentsByBlogId("b1", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCommentId()).isEqualTo("c1");
    }

    @Test
    void updateCommentLike_togglesAndPersistsLikeCount() {
        Comment comment = Comment.builder().id("c1").build();
        Like existing = Like.builder().id("l1").build();

        when(commentRepo.findById("c1")).thenReturn(Optional.of(comment));
        when(likeRepo.findByResourceIdAndResourceTypeAndUserId("c1", ResourceType.COMMENT, "u1"))
                .thenReturn(Optional.of(existing))
                .thenReturn(Optional.empty());
        when(likeRepo.countByResourceIdAndResourceType("c1", ResourceType.COMMENT))
                .thenReturn(0)
                .thenReturn(1);
        when(commentRepo.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        CommentResponseDTO first = commentService.updateCommentLike("c1", "u1");
        CommentResponseDTO second = commentService.updateCommentLike("c1", "u1");

        assertThat(first.getLikesCount()).isZero();
        assertThat(second.getLikesCount()).isEqualTo(1);
        verify(likeRepo).delete(existing);
        verify(likeRepo).save(any(Like.class));
    }

    @Test
    void updateCommentLike_throwsNotFound_whenCommentMissing() {
        when(commentRepo.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateCommentLike("missing", "u1"))
                .isInstanceOf(CommentNotFoundException.class);
    }
}
