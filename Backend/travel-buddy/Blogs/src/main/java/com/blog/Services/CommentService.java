package com.blog.Services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blog.DTO.CommentRequestDTO;
import com.blog.DTO.CommentResponseDTO;
import com.blog.Entity.Comment;


public interface CommentService {
    
    public CommentResponseDTO addComment(CommentRequestDTO request);
    public CommentResponseDTO updateComment(String commentId, CommentRequestDTO request);
    public Page<CommentResponseDTO> getTopLevelCommentsByBlogId(String blogId, Pageable pageable);
    public Page<Comment> getAllReplies(String parentCommentId, Pageable pageable);
    public List<CommentResponseDTO> getCommentsByBlogIdAndParentCommentId(String blogId, String parentCommentId);
    public List<CommentResponseDTO> getCommentsByBlogIdAndParentCommentIdAndReplieDTOUserId(
            String blogId, String parentCommentId, String replieDTOUserId);
    public CommentResponseDTO updateCommentLike(String commentId, String userId);
    public Comment mapToEntity(CommentRequestDTO dto);
    public CommentResponseDTO mapToDTO(Comment comment);
}
