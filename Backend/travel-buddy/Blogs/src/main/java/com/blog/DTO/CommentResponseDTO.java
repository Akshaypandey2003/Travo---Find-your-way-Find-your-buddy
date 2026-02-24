package com.blog.DTO;

import java.time.Instant;

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

    private long likesCount;
    private boolean edited;

    private Instant createdAt;
}
