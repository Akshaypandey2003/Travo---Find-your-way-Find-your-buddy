package com.gateway.Config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allow all origins
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Collections.singletonList("*")); 
                    config.setAllowCredentials(true);
                    config.setExposedHeaders(Arrays.asList("Authorization"));
                    return config;
                })) // ✅ Fix: Correctly setting CORS configuration
                // .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .authorizeExchange( exchange -> exchange
                .pathMatchers("/auth/**").permitAll() // Allow public access to auth endpoints
                .pathMatchers("/user/**").permitAll()
                .pathMatchers("/blog/**").permitAll() // Require authentication for user endpoints
                .pathMatchers("/admin/**").hasRole("ADMIN") // Only admins can access
                .anyExchange().authenticated()); // All other endpoints require authentication


                // .and()
                // .oauth2ResourceServer()
                // .jwt();

        return http.build();
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {

    //     CorsConfiguration config = new CorsConfiguration();

    //    // Allow all origins (for debugging purposes)
    //     config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allowed origins

    //     config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP
    //                                                                                                  // methods
    //     config.setAllowedHeaders(Collections.singletonList("*")); // Allowed headers (* represents all headers)
    //     config.setAllowCredentials(true); // Allow credentials (cookies, etc.)
    //     config.setExposedHeaders(Arrays.asList("Authorization"));
    //     config.setMaxAge(3600L); // Cache the preflight response for 1 hour

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config); // Apply to all endpoints

    //     return source;
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
