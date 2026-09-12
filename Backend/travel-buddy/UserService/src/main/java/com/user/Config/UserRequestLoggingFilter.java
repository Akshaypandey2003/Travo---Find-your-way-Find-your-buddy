package com.user.Config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserRequestLoggingFilter.class);

    @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.startsWith("/api/v1/user")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startedAt = System.currentTimeMillis();
        boolean hasAuthorization = request.getHeader("Authorization") != null;

        logger.info("UserService request started: method={}, path={}, authPresent={}, remote={}",
                request.getMethod(), path, hasAuthorization, request.getRemoteAddr());

        try {
            filterChain.doFilter(request, response);
            logger.info("UserService request completed: method={}, path={}, status={}, durationMs={}",
                    request.getMethod(), path, response.getStatus(),
                    System.currentTimeMillis() - startedAt);
        } catch (Exception error) {
            logger.error(
                    "UserService request failed: method={}, path={}, status={}, durationMs={}, errorType={}, message={}",
                    request.getMethod(), path, response.getStatus(),
                    System.currentTimeMillis() - startedAt, error.getClass().getSimpleName(),
                    error.getMessage(), error);
            throw error;
        }
    }
}
