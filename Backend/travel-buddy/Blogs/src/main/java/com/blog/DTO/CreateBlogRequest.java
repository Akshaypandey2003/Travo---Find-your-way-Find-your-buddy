package com.blog.DTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBlogRequest {
    @NotBlank(message = "title is required")
    @Size(max = 150, message = "title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "content is required")
    @Size(max = 10000, message = "content must not exceed 10000 characters")
    private String content;

    @Size(max = 500, message = "caption must not exceed 500 characters")
    private String caption;

    @NotBlank(message = "category is required")
    @Size(max = 60, message = "category must not exceed 60 characters")
    private String category;

    private List<String> imageUrls;
    private List<String> cloudinaryPublicIds;

    @Size(max = 100, message = "authorName must not exceed 100 characters")
    private String authorName;

    @Size(max = 500, message = "authorProfilePic must not exceed 500 characters")
    private String authorProfilePic;
    
}
