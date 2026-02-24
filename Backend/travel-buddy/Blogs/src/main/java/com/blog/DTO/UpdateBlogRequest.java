package com.blog.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBlogRequest {
   private String title;
    private String content;
    private String caption;
    private String category;
    private List<String> imageUrls;
    private List<String> cloudinaryPublicIds;
}