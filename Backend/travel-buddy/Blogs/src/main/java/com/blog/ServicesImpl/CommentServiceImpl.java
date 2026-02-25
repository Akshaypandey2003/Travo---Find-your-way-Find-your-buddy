package com.blog.ServicesImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blog.DTO.ApiResponse;
import com.blog.DTO.CommentRequestDTO;
import com.blog.DTO.CommentResponseDTO;
import com.blog.Entity.Blog;
import com.blog.Entity.Comment;
import com.blog.Entity.Like;
import com.blog.Enum.ResourceType;
import com.blog.Exceptions.BlogNotFoundException;
import com.blog.Exceptions.CommentNotFoundException;
import com.blog.Repositories.CommentRepository;
import com.blog.Repositories.LikeRepository;
import com.blog.Services.BlogService;
import com.blog.Services.CommentService;
import com.blog.Services.NotificationProducer;

@Service
@SuppressWarnings("unused")
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final BlogService blogService;
    private final NotificationProducer notificationProducer;
    private final LikeRepository likeRepo;

    private static final int MAX_DEPTH = 2;

    public CommentServiceImpl(
            CommentRepository commentRepo,
            BlogService blogService,
            LikeRepository likeRepo,
            NotificationProducer notificationProducer) {
        this.commentRepo = commentRepo;
        this.blogService = blogService;
        this.notificationProducer = notificationProducer;
        this.likeRepo = likeRepo;
    }

    @Override
    public CommentResponseDTO addComment(CommentRequestDTO request) {
        
        int depth = 0;
        if (request.getParentCommentId() != null) {
            Comment parentComment = commentRepo.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment not found"));
            depth = parentComment.getDepth() + 1;
            if (depth > MAX_DEPTH) {
                throw new IllegalArgumentException("Maximum reply depth exceeded");
            }
        }
        Comment comment = Comment.builder()
            .blogId(request.getBlogId())
            .authorId(request.getAuthorId())
            .content(request.getContent())
            .parentCommentId(request.getParentCommentId())
            .depth(depth)
            .build();
            
        Comment savedComment = commentRepo.save(comment);

        Blog blog = fetchBlog(savedComment.getBlogId());

        notificationProducer.sendCommentNotification(
                savedComment.getAuthorId(),
                blog.getAuthorId(),
                savedComment.getBlogId(),
                blog.getTitle());

        return mapToDTO(savedComment);
    }


    @Override
    public Page<CommentResponseDTO> getTopLevelCommentsByBlogId(String blogId, Pageable pageable) {

        Page<Comment> comments = commentRepo.findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(blogId, pageable);

        if (comments.isEmpty()) {
            throw new CommentNotFoundException(
                    "No top-level comments found for blog id: " + blogId);
        }

        return comments.map(this::mapToDTO);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByBlogIdAndParentCommentId(
            String blogId, String parentCommentId) {

        List<Comment> comments = commentRepo.findByBlogIdAndParentCommentIdOrderByCreatedAtDesc(blogId,
                parentCommentId);

        if (comments.isEmpty()) {
            throw new CommentNotFoundException(
                    "No comments found for blog id: " + blogId + " and parent comment id: " + parentCommentId);
        }

        return comments.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<CommentResponseDTO> getCommentsByBlogIdAndParentCommentIdAndReplieDTOUserId(
            String blogId, String parentCommentId, String replieDTOUserId) {

        List<Comment> comments = commentRepo.findByBlogIdAndParentCommentIdAndRepliedToUserIdOrderByCreatedAtDesc(
                blogId, parentCommentId, replieDTOUserId);

        if (comments.isEmpty()) {
            throw new CommentNotFoundException(
                    "No comments found for blog id: " + blogId +
                            ", parent comment id: " + parentCommentId +
                            ", replieDTO user id: " + replieDTOUserId);
        }

        return comments.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public CommentResponseDTO updateCommentLike(String commentId, String userId) {

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
        
        Optional<Like> like = likeRepo.findByResourceIdAndResourceTypeAndAuthorId(commentId,ResourceType.COMMENT,userId);

        if(like.isPresent())
        {
            likeRepo.delete(like.get());
        }
        else
        {
            Like newLike = Like.builder()
                    .resourceId(commentId)
                    .resourceType(ResourceType.COMMENT)
                    .userId(userId)
                    .build();
            likeRepo.save(newLike);
        }

         int likeCount = likeRepo.countByResourceIdAndResourceType(commentId, ResourceType.COMMENT);
         comment.setLikeCount(likeCount);
        Comment updatedComment = commentRepo.save(comment);

        return mapToDTO(updatedComment);
    }

    /* ---------------- PRIVATE HELPERS ---------------- */

    private Comment getCommentById(String commentId) {
        return commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
    }

    private Blog fetchBlog(String blogId) {
        ApiResponse<Blog> response = blogService.getBlogById(blogId);
        return response.getData();
    }

    @Override
    public Page<Comment> getAllReplies(String parentCommentId, Pageable pageable) {

        Page<Comment> repliesPage = commentRepo.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId, pageable);

        if (repliesPage.isEmpty()) {
            throw new CommentNotFoundException(
                    "No replies found for parent comment id: " + parentCommentId);
        }

        return repliesPage;
    }

    public Comment mapToEntity(CommentRequestDTO dto) {
        return Comment.builder()
                .blogId(dto.getBlogId())
                .authorId(dto.getAuthorId())
                .parentCommentId(dto.getParentCommentId())
                .content(dto.getContent())
                .createdAt(Instant.now())
                .build();
    }

    public CommentResponseDTO mapToDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .blogId(comment.getBlogId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .likesCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
