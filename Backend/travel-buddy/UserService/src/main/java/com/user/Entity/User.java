package com.user.Entity;

import java.time.Instant;
import java.util.ArrayList;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.user.Enum.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Indexed(unique = true)
    private String email;
    private String phone;
    private String role;

    @JsonIgnore
    private String password;
    private String profilePic;
    private String cloudinaryImagePublicId;
    private String gender;
    private String country;
    private String state;
    private String city;
    private String bio;
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

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

   
}
