package com.gateway.DTO;

import java.util.List;

import com.gateway.Entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentsPageResponse {
    private List<Comment> comments;
    private int currentPage;
    private int totalPages;
    private boolean isLastPage;
}

