package com.gateway.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString

public class Comment {
    private String commentId;
    private String blogId;
    private String authorId;
    private String authorName;
    private String authorProfilePic;
    private String authorGender;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private boolean isEdited = false;
    private String parentCommentId;
    private String repliedToUserId;
    private List<String> commentLikes = new ArrayList<>();
    private List<Comment> replies = new ArrayList<>();
}
