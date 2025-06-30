package com.gateway.Entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder


public class User {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String password;
    private String profilePic;
    private String cloudinaryImagePublicId;
    private String gender;
    private String country;
    private String state;
    private String city;
    private String bio;
    private MessageResponse messageResponse;
    
    // ArrayList<String> partnerPreference;
    private ArrayList<String> likes;
    private ArrayList<String> preferences;
    private ArrayList<String> following;
    private ArrayList<String> followers;
    private List<Trip> trips;
    private List<String> closeFriends = new ArrayList<>();
}
