package com.user.Config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
     http
     .csrf(csrf -> csrf.ignoringRequestMatchers(
        "/ws/**",
        "/user/**",
        "/connection/**",
        "/auth/user/notification/**",
        "/chat/**"
    ))
     .cors(cors -> cors.configurationSource(corsConfigurationSource()))
     .authorizeHttpRequests(auth->auth.requestMatchers("/user/**","/connection/**","/auth/user/notification/**","/ws/**","/chat/**").permitAll()
     .anyRequest()
     .authenticated());
    //  .oauth2ResourceServer(oauth2 -> oauth2.jwt()
    //             .jwtAuthenticationConverter(jwtAuthenticationConverter()));
     ;

     return http.build();
    }


     @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Collections.singletonList("*"));// Allowed origins

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP
                                                                                                     // methods
        config.setAllowedHeaders(Collections.singletonList("*")); // Allowed headers (* represents all headers)
        config.setAllowCredentials(true); // Allow credentials (cookies, etc.)
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setMaxAge(3600L); // Cache the preflight response for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply to all endpoints

        return source;
    }
    
    // public JwtAuthenticationConverter jwtAuthenticationConverter() {
    //     JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    //     grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    //     grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Ensure 'roles' claim exists in JWT

    //     JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    //     jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    //     return jwtAuthenticationConverter;
    // }
    @Bean
    public UserDetailsService userDetailsService() 
    {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }
}
