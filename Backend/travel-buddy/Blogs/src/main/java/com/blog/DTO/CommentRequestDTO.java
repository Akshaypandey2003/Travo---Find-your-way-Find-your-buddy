package com.blog.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    private String blogId;
    private String authorId;
    private String parentCommentId;
    private String repliedToUserId;
    private String content;
}
