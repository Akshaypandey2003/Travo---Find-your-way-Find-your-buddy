package com.blog.comments.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blog.comments.Entity.Comment;
import com.blog.comments.Exceptions.CommentNotFoundException;
import com.blog.comments.Repository.CommentRepository;
import com.blog.comments.Services.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepo;

    @Override
    public Comment addComment(Comment comment) {
        return commentRepo.save(comment);
    }

    @Override
    public Comment updateComment(String commentId, Comment comment) {
        Comment existingComment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        if (comment.getContent() != null)
            existingComment.setContent(comment.getContent());

        existingComment.setUpdatedAt(LocalDateTime.now());
        existingComment.setEdited(true);
        return commentRepo.save(existingComment);
    }

    @Override
    public Page<Comment> getTopLevelCommentsByBlogId(String blogId, Pageable pageable) {

        Page<Comment> topLevelComments = commentRepo.findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtDesc(blogId,
                pageable);
        if (topLevelComments.isEmpty()) {
            throw new CommentNotFoundException("No top-level comments found for blog id: " + blogId);
        }
        return topLevelComments;

    }
    public List<Comment> getAllReplies(String parentId) {
        List<Comment> replies = commentRepo.findByParentCommentIdOrderByCreatedAtAsc(parentId);
        for (Comment reply : replies) {
            reply.setReplies(getAllReplies(reply.getCommentId())); // you must support this in DTO
        }
        return replies;
    }


    @Override
    public List<Comment> getCommentsByBlogIdAndParentCommentId(String blogId, String parentCommentId) 
    {
        List<Comment> comments = commentRepo.findByBlogIdAndParentCommentIdOrderByCreatedAtDesc(blogId,
                parentCommentId);
        if (comments.isEmpty())
            throw new CommentNotFoundException(
                    "No comments found for blog id: " + blogId + " and parent comment id: " + parentCommentId);
        return comments;
    }

    @Override
    public List<Comment> getCommentsByBlogIdAndParentCommentIdAndRepliedToUserId(String blogId, String parentCommentId,
            String repliedToUserId) {
        List<Comment> comments = commentRepo.findByBlogIdAndParentCommentIdAndRepliedToUserIdOrderByCreatedAtDesc(
                blogId, parentCommentId,
                repliedToUserId);
        if (comments.isEmpty())
            throw new CommentNotFoundException("No comments found for blog id: " + blogId + " and parent comment id: "
                    + parentCommentId + " and replied to user id: " + repliedToUserId);

        return comments;
    }

    @Override
    public Comment updateCommentLike(String commentId, String userId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
        List<String> commentLikes = comment.getCommentLikes();
        if (commentLikes == null) {
            commentLikes = new ArrayList<>();
        }
        if (commentLikes.contains(userId)) {
            commentLikes.remove(userId);
        } else {
            commentLikes.add(userId);
        }
        comment.setCommentLikes(commentLikes);
        return commentRepo.save(comment);
    }

}
