package com.user.Entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

@Document
public class User {
    @Id
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

    @Builder.Default
    private ArrayList<String> likes = new ArrayList<>();

    @Builder.Default
    private ArrayList<String> preferences = new ArrayList<>();

    @Builder.Default
    private ArrayList<String> following = new ArrayList<>();

    @Builder.Default
    private ArrayList<String> followers = new ArrayList<>();

    @Builder.Default
    private ArrayList<String> trips = new ArrayList<>();

    @Builder.Default
    private List<String> closeFriends = new ArrayList<>();
}
