package com.gateway.Response;

import java.util.Collection;

import com.gateway.Entity.MessageResponse;
import com.gateway.Entity.User;

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
