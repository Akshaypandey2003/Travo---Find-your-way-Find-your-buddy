package com.blog.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "blogId is required")
    private String blogId;
    private String parentCommentId;
    private String repliedToUserId;

    @NotBlank(message = "content is required")
    @Size(max = 2000, message = "content must not exceed 2000 characters")
    private String content;
}
