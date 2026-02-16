package com.user.DTO;

import java.util.Collection;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthResponse {
    
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private long expire_at;
    private Collection<String> authorities;
    private MessageResponse messageReponse;
}