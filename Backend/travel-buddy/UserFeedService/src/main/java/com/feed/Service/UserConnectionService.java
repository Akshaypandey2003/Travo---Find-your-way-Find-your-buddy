package com.feed.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(UserConnectionService.class);

    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${user.service.base-url:http://localhost:8080}")
    private String userServiceBaseUrl;

    @Value("${feed.connections.cache-ttl-minutes:10}")
    private long connectionsCacheTtlMinutes;

    public UserConnectionService(RestTemplate restTemplate, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<String> getFollowers(String userId) {
        String cacheKey = "feed:connections:" + userId;

        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<String>>() {});
            }
        } catch (Exception ex) {
            logger.warn("Failed to read connections cache for userId={}: {}", userId, ex.getMessage());
        }

        String url = userServiceBaseUrl + "/user/internal/followers/" + userId;
        try {
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {});

            List<String> followers = response.getBody();
            if (followers == null || followers.isEmpty()) {
                return Collections.emptyList();
            }

            try {
                String payload = objectMapper.writeValueAsString(followers);
                redisTemplate.opsForValue().set(cacheKey, payload, Duration.ofMinutes(connectionsCacheTtlMinutes));
            } catch (Exception ex) {
                logger.warn("Failed to cache followers for userId={}: {}", userId, ex.getMessage());
            }

            return followers;
        } catch (HttpClientErrorException.NotFound ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            logger.error("Failed to fetch followers for userId={}: {}", userId, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
