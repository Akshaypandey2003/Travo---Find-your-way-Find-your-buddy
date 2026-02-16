package com.blog.UnitTests;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.blog.DTO.*;
import com.blog.Entity.Blog;
import com.blog.Entity.Comment;
import com.blog.Exceptions.CommentNotFoundException;
import com.blog.Repositories.CommentRepository;
import com.blog.Services.BlogService;
import com.blog.Services.NotificationProducer;
import com.blog.ServicesImpl.CommentServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

        @InjectMocks
        private CommentServiceImpl commentService;

        @Mock
        private CommentRepository commentRepo;

        @Mock
        private BlogService blogService;

        @Mock
        private NotificationProducer notificationProducer;

        /* ---------- ADD COMMENT ---------- */

        @Test
        void addComment_success_shouldNotifyBlogAuthor() {

                CommentRequestDTO request = CommentRequestDTO.builder()
                                .blogId("b1")
                                .authorId("user1")
                                .content("Nice blog")
                                .build();

                Comment savedComment = Comment.builder()
                                .commentId("c1")
                                .blogId("b1")
                                .authorId("user1")
                                .content("Nice blog")
                                .createdAt(LocalDateTime.now())
                                .build();

                Blog blog = Blog.builder()
                                .blogAuthorId("author")
                                .blogTitle("Test Blog")
                                .build();

                when(commentRepo.save(any(Comment.class))).thenReturn(savedComment);
                when(blogService.getBlogById("b1"))
                                .thenReturn(new ApiResponse<>(true, blog, "Fetched"));

                CommentResponseDTO response = commentService.addComment(request);

                assertThat(response.getCommentId()).isEqualTo("c1");
                assertThat(response.getContent()).isEqualTo("Nice blog");

                verify(notificationProducer).sendCommentNotification(
                                "user1", "author", "b1", "Test Blog");
        }

        /* ---------- UPDATE COMMENT ---------- */

        @Test
        void updateComment_success() {

                Comment existing = Comment.builder()
                                .commentId("c1")
                                .content("old")
                                .isEdited(false)
                                .build();

                CommentRequestDTO update = CommentRequestDTO.builder()
                                .content("new")
                                .build();

                when(commentRepo.findById("c1")).thenReturn(Optional.of(existing));
                when(commentRepo.save(any())).thenReturn(existing);

                CommentResponseDTO response = commentService.updateComment("c1", update);

                assertThat(response.getContent()).isEqualTo("new");
                assertThat(response.isEdited()).isTrue();
                assertThat(response.getUpdatedAt()).isNotNull();
        }

        @Test
        void updateComment_notFound() {
                when(commentRepo.findById("c1")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> commentService.updateComment("c1", new CommentRequestDTO()))
                                .isInstanceOf(CommentNotFoundException.class);
        }

        /* ---------- GET TOP LEVEL COMMENTS ---------- */

        @Test
        void getTopLevelCommentsByBlogId_success() {

                Comment comment = Comment.builder().commentId("c1").build();
                Page<Comment> page = new PageImpl<>(List.of(comment));

                when(commentRepo
                                .findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(
                                                eq("b1"), any(Pageable.class)))
                                .thenReturn(page);

                Page<CommentResponseDTO> result = commentService.getTopLevelCommentsByBlogId("b1",
                                PageRequest.of(0, 10));

                assertThat(result).hasSize(1);
        }

        @Test
        void getTopLevelCommentsByBlogId_empty() {

                when(commentRepo
                                .findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(
                                                eq("b1"), any(Pageable.class)))
                                .thenReturn(Page.empty());

                assertThatThrownBy(() -> commentService.getTopLevelCommentsByBlogId("b1", PageRequest.of(0, 10)))
                                .isInstanceOf(CommentNotFoundException.class);
        }

        /* ---------- GET COMMENTS BY BLOG + PARENT ---------- */

        @Test
        void getCommentsByBlogIdAndParentCommentId_success() {

                when(commentRepo
                                .findByBlogIdAndParentCommentIdOrderByCreatedAtDesc("b1", "p1"))
                                .thenReturn(List.of(new Comment()));

                List<CommentResponseDTO> result = commentService.getCommentsByBlogIdAndParentCommentId("b1", "p1");

                assertThat(result).hasSize(1);
        }

        @Test
        void getCommentsByBlogIdAndParentCommentId_empty() {

                when(commentRepo
                                .findByBlogIdAndParentCommentIdOrderByCreatedAtDesc("b1", "p1"))
                                .thenReturn(List.of());

                assertThatThrownBy(() -> commentService.getCommentsByBlogIdAndParentCommentId("b1", "p1"))
                                .isInstanceOf(CommentNotFoundException.class);
        }

        /* ---------- GET COMMENTS BY BLOG + PARENT + USER ---------- */

        @Test
        void getCommentsByBlogIdAndParentCommentIdAndRepliedToUserId_success() {

                when(commentRepo
                                .findByBlogIdAndParentCommentIdAndRepliedToUserIdOrderByCreatedAtDesc(
                                                "b1", "p1", "u1"))
                                .thenReturn(List.of(new Comment()));

                List<CommentResponseDTO> result = commentService
                                .getCommentsByBlogIdAndParentCommentIdAndReplieDTOUserId(
                                                "b1", "p1", "u1");

                assertThat(result).hasSize(1);
        }

        @Test
        void getCommentsByBlogIdAndParentCommentIdAndRepliedToUserId_empty() {

                when(commentRepo
                                .findByBlogIdAndParentCommentIdAndRepliedToUserIdOrderByCreatedAtDesc(
                                                "b1", "p1", "u1"))
                                .thenReturn(List.of());

                assertThatThrownBy(() -> commentService.getCommentsByBlogIdAndParentCommentIdAndReplieDTOUserId(
                                "b1", "p1", "u1"))
                                .isInstanceOf(CommentNotFoundException.class);
        }

        /* ---------- UPDATE COMMENT LIKE ---------- */

        @Test
        void updateCommentLike_addLike_shouldNotify() {

                Comment comment = Comment.builder()
                                .commentId("c1")
                                .blogId("b1")
                                .authorId("author")
                                .commentLikes(new ArrayList<>())
                                .build();

                Blog blog = Blog.builder()
                                .blogTitle("Test Blog")
                                .build();

                when(commentRepo.findById("c1")).thenReturn(Optional.of(comment));
                when(blogService.getBlogById("b1"))
                                .thenReturn(new ApiResponse<>(true, blog, "Fetched"));
                when(commentRepo.save(any())).thenReturn(comment);

                CommentResponseDTO response = commentService.updateCommentLike("c1", "user");

                assertThat(response.getLikesCount()).isEqualTo(1);

                verify(notificationProducer).sendCommentLikeNotification(
                                "user", "author", "b1", "Test Blog");
        }

        @Test
        void updateCommentLike_removeLike_shouldNotNotify() {

                Comment comment = Comment.builder()
                                .commentId("c1")
                                .blogId("b1")
                                .commentLikes(new ArrayList<>(List.of("user")))
                                .build();

                when(commentRepo.findById("c1"))
                                .thenReturn(Optional.of(comment));

                when(commentRepo.save(any()))
                                .thenReturn(comment);

                CommentResponseDTO response = commentService.updateCommentLike("c1", "user");

                assertThat(response.getLikesCount()).isZero();
                verifyNoInteractions(notificationProducer);
        }

        @Test
        void updateCommentLike_notFound() {

                when(commentRepo.findById("c1")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> commentService.updateCommentLike("c1", "user"))
                                .isInstanceOf(CommentNotFoundException.class);
        }

        /* ---------- GET ALL REPLIES (PAGINATED) ---------- */

        @Test
        void getAllReplies_success() {

                Page<Comment> page = new PageImpl<>(List.of(new Comment()));

                when(commentRepo.findByParentCommentIdOrderByCreatedAtAsc(
                                eq("p1"), any(Pageable.class)))
                                .thenReturn(page);

                Page<Comment> result = commentService.getAllReplies("p1", PageRequest.of(0, 10));

                assertThat(result).hasSize(1);
        }

        @Test
        void getAllReplies_empty() {

                when(commentRepo.findByParentCommentIdOrderByCreatedAtAsc(
                                eq("p1"), any(Pageable.class)))
                                .thenReturn(Page.empty());

                assertThatThrownBy(() -> commentService.getAllReplies("p1", PageRequest.of(0, 10)))
                                .isInstanceOf(CommentNotFoundException.class);
        }
}
