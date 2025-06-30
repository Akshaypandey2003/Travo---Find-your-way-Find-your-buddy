package com.blog.comments.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document
public class Comment {

    @Id
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

    @Transient
    private List<Comment> replies = new ArrayList<>();
}
