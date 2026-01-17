package com.gateway.Config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // private final JwtAuthenticationFilter jwtFilter;

    // public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
    //     this.jwtFilter = jwtFilter;
    // }

    // @Bean
    // SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    //     return http
    //             .csrf(csrf -> csrf.disable())
    //             .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
    //             .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
    //             .securityContextRepository(
    //                     NoOpServerSecurityContextRepository.getInstance())
    //             .cors(cors -> cors.configurationSource(request -> {
    //                 CorsConfiguration config = new CorsConfiguration();
    //                 config.setAllowedOriginPatterns(Collections.singletonList("*"));
    //                 config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    //                 config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
    //                 config.setAllowedHeaders(Collections.singletonList("*"));
    //                 config.setAllowCredentials(true);
    //                 config.setExposedHeaders(Arrays.asList("Authorization"));
    //                 return config;
    //             }))
    //             .authorizeExchange(exchange -> exchange
    //                     .pathMatchers("/auth/**", "/actuator/**").permitAll()
    //                     .anyExchange().authenticated())
    //             .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
    //             .build();

    // }

     @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationManager authManager,
            JwtAuthenticationConverter authConverter
    ) {

        AuthenticationWebFilter jwtFilter =
                new AuthenticationWebFilter(authManager);

        jwtFilter.setServerAuthenticationConverter(authConverter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(
                        NoOpServerSecurityContextRepository.getInstance()
                )
                  .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setExposedHeaders(Arrays.asList("Authorization"));
                    return config;
                }))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**", "/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
