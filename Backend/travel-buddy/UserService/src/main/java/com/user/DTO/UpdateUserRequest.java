package com.user.DTO;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String name;

    private String phone;

    private String bio;

    private String country;

    private String state;

    private String city;

    private String gender;

    private String profilePic;

    private String cloudinaryImagePublicId;

    @Size(max = 20)
    private String role;
}