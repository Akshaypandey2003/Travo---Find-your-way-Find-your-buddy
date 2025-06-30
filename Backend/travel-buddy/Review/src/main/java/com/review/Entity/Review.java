package com.review.Entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document(collection = "Review")
public class Review {
    
    //Mandatory fields
    @Id
    private String reviewId;

    private String userId;
    private String hotelId;
    private String feedback;
    private int rating;
    private String date;

   //Additional fields
    private String title;
    private List<String> images;
    private boolean isEdited;
    private String hotelResponse;
    private int reviewLikes;
}
