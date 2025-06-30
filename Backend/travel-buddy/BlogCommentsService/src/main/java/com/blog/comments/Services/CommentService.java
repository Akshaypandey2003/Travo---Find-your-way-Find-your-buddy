package com.blog.comments.Services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blog.comments.Entity.Comment;

public interface CommentService {
    
    public Comment addComment(Comment comment);
    public Comment updateComment(String commentId,Comment comment);
     public List<Comment> getAllReplies(String parentId);
   
    public Page<Comment> getTopLevelCommentsByBlogId(String blogId,Pageable pageable);
    public List<Comment> getCommentsByBlogIdAndParentCommentId(String blogId, String parentCommentId);
    public List<Comment> getCommentsByBlogIdAndParentCommentIdAndRepliedToUserId(String blogId, String parentCommentId, String repliedToUserId);
    public Comment updateCommentLike(String commentId, String userId);
}
