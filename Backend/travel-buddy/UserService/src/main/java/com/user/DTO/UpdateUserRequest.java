package com.user.DTO;


import com.user.Enum.AccountType;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private String name;

    private String phone;

    private String bio;

    private String country;

    private String state;

    private String city;

    private String gender;

    private String profilePic;
    private AccountType accountType;
    private String cloudinaryImagePublicId;
    @Size(max = 20)
    private String role;
}