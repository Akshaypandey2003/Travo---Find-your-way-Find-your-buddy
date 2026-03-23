package com.user.ServiceImpl;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String PREFIX = "refresh:";

    // ✅ Save token
    public void save(String refreshToken, String userId) {
        redisTemplate.opsForValue().set(
                PREFIX + refreshToken,
                userId,
                Duration.ofDays(7) // expiry
        );
    }

    // ✅ Validate token
    public String validate(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(PREFIX + refreshToken);

        if (userId == null) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        return userId;
    }

    // ✅ Delete (logout / rotation)
    public void delete(String refreshToken) {
        redisTemplate.delete(PREFIX + refreshToken);
    }
}