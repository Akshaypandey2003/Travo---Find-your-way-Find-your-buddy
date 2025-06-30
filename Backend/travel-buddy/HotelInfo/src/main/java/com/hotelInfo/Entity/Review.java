package com.hotelInfo.Entity;

import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

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
