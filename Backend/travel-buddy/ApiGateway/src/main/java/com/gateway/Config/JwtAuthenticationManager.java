package com.gateway.Config;

import java.util.List;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        Claims claims = jwtProvider.validateToken(token);

        String email = claims.getSubject();

        @SuppressWarnings("unchecked")
        List<String> roles = ((List<?>) claims.get("roles"))
                .stream()
                .map(Object::toString)
                .toList();

        var authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return Mono.just(
                new UsernamePasswordAuthenticationToken(email, token, authorities)
        );
    }
}
