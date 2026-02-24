package com.user.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String userId;

    private String name;
    private String email;

    private String phone;

    private String profilePic;

    private String gender;

    private String bio;

    private String country;

    private String state;

    private String city;

    private int ratingsReceivedCount;

    private double averageRating;

}
