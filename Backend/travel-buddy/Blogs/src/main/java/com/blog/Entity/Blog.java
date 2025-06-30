package com.blog.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString

@Document
public class Blog {
    
    @Id
    private String blogId;
    private String blogTitle;
    private String blogContent;
    private String blogAuthor;
    private String blogCaption;
    private LocalDateTime postedDate= LocalDateTime.now();
    private LocalDateTime updatedDate;
    private List<String> blogImages=new ArrayList<>();
    private List<String> cloudinaryImagePublicIds=new ArrayList<>();
    private String blogCategory;
    private List<String> blogComments=new ArrayList<>();
    private List<String> blogLikes=new ArrayList<>();
    private List<String> blogShares=new ArrayList<>();
    private Set<String> blogViews=new HashSet<>();

}
