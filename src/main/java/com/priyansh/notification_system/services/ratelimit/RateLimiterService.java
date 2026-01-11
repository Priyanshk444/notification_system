package com.priyansh.notification_system.services.ratelimit;

public interface RateLimiterService {

    /**
     * @return true if request is allowed, false if rate limit exceeded
     */
    boolean allowRequest(Long userId);
    
}
