package com.gateway.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${gateway.security.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private List<String> allowedOrigins;

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
                    config.setAllowedOrigins(allowedOrigins);
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowCredentials(false);
                    config.setExposedHeaders(Arrays.asList("Authorization"));
                    return config;
                }))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/v1/auth/**", "/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        .anyExchange().authenticated()
                )
              .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
