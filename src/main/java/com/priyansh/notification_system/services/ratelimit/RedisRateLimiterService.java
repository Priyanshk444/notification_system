package com.priyansh.notification_system.services.ratelimit;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.priyansh.notification_system.config.RateLimitProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimiterService implements RateLimiterService {

    private final RateLimitProperties properties;

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean allowRequest(Long userId) {
        
        String key = "rate_limit:user:" + userId;

        //Atomic increment
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) {
            // Redis failure → fail open (important decision)
            log.warn("Redis unavailable, allowing request for user {}", userId);
            return true;
        }

        // First request in this window → set expiry
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(properties.getWindowSeconds()));
        }

        boolean allowed = count <= properties.getMaxRequests();

        if (!allowed) {
            log.warn(
                "Rate limit exceeded for user {}. count={}",
                userId,
                count
            );
        }

        return allowed;
    }
}
