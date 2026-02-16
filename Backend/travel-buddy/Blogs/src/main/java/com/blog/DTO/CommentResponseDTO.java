package com.blog.DTO;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponseDTO {
    private String commentId;
    private String blogId;
    private String authorId;
    private String content;

    private String parentCommentId;
    private String repliedToUserId;

    private int likesCount;
    private boolean edited;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
