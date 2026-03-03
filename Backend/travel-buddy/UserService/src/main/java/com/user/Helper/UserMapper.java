package com.user.Helper;


import org.springframework.stereotype.Component;

import com.user.DTO.RegisterRequest;
import com.user.DTO.UserResponse;
import com.user.Entity.User;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .gender(request.getGender())
                .build();
    }
    public User toEntity(UserResponse user) {

        return User.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePic(user.getProfilePic())
                .bio(user.getBio())
                .country(user.getCountry())
                .state(user.getState())
                .city(user.getCity())
                .ratingsReceivedCount(user.getRatingsReceivedCount())
                .averageRating(user.getAverageRating())
                .build();
    }

    public UserResponse toResponse(User user) {

         return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .gender(user.getGender())
                .phone(user.getPhone())
                .profilePic(user.getProfilePic())
                .bio(user.getBio())
                .country(user.getCountry())
                .state(user.getState())
                .city(user.getCity())
                .ratingsReceivedCount(user.getRatingsReceivedCount())
                .averageRating(user.getAverageRating())
                .build();
    }
}
