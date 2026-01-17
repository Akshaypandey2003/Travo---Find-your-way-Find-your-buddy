package com.user.DTO;

import java.util.Collection;

import com.user.Entity.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthResponse {
    
    private User user;
    private String accessToken;
    private String refreshToken;
    private long expire_at;
    private Collection<String> authorities;
    private MessageResponse messageReponse;
}