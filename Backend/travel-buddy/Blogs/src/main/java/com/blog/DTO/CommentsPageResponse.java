package com.blog.DTO;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentsPageResponse<T> {
    private List<T> comments;
    private int currentPage;
    private int totalPages;
    private boolean isLastPage;
}
