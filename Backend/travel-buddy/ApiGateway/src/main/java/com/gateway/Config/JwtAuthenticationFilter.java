package com.gateway.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String secret;

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    @SuppressWarnings("null")
    public Mono<Void> filter( ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtProvider.validateToken(token);

            String email = claims.getSubject();
            List<String> roles = claims.get("roles", List.class)
                    .stream()
                    .map(Object::toString)
                    .toList();

            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            Authentication authentication = new UsernamePasswordAuthenticationToken(email, token, authorities);

            SecurityContext context = new SecurityContextImpl(authentication);

            return chain.filter(exchange)
                    .contextWrite(
                            ReactiveSecurityContextHolder.withSecurityContext(
                                    Mono.just(context)));

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
