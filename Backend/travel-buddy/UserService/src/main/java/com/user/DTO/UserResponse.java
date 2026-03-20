package com.user.DTO;


import java.time.Instant;
import java.util.ArrayList;

import com.user.Enum.AccountType;

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

    private String role;

    private String cloudinaryImagePublicId;
    private AccountType accountType;
    
    @Builder.Default
    private ArrayList<String> preferences = new ArrayList<>();

    @Builder.Default
    private int followersCount = 0;

    @Builder.Default
    private int followingsCount = 0;

    @Builder.Default
    private int closeFriendsCount = 0;

    @Builder.Default
    private int tripsCount = 0;

    @Builder.Default
    private int blogsCount = 0;

    @Builder.Default
    private int ratingsReceivedSum = 0;

    private Instant createdAt;
    private Instant updatedAt;

}
