package com.urbanpulse.auth.service;

import com.urbanpulse.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for JWT token lifecycle management including
 * blacklisting for logout functionality.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_KEY_PREFIX = "token:blacklist:";

    /**
     * Blacklist a token for logout. Token remains in blacklist until its natural expiration.
     */
    public void blacklistToken(String token) {
        String tokenId = jwtTokenProvider.extractTokenId(token);
        long expirationMs = jwtTokenProvider.getExpirationTime();
        redisTemplate.opsForValue().set(
                BLACKLIST_KEY_PREFIX + tokenId,
                "blacklisted",
                expirationMs,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Check if a token is blacklisted.
     */
    public boolean isTokenBlacklisted(String token) {
        String tokenId = jwtTokenProvider.extractTokenId(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + tokenId));
    }

    /**
     * Get user ID from token.
     */
    public UUID getUserIdFromToken(String token) {
        return jwtTokenProvider.extractUserId(token);
    }
}
