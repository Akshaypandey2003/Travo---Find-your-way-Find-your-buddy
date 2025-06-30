package com.gateway.Entity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class Blog {
    
    private String blogId;
    private String blogTitle;
    private String blogContent;
    private String blogAuthor;
    private String blogCaption;
    private LocalDateTime postedDate;
    private LocalDateTime updatedDate;
    private List<String> blogImages;
    private List<String> cloudinaryImagePublicIds;
    private String blogCategory;
    private List<String> blogComments;
    private List<String> blogLikes;
    private List<String> blogShares;
    private Set<String> blogViews;
    private MessageResponse messageResponse = new MessageResponse();
}
