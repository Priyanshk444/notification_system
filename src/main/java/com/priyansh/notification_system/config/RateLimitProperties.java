package com.priyansh.notification_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@ConfigurationProperties(prefix = "notification.rate-limit")
@Getter
public class RateLimitProperties {

    private int maxRequests;
    private int windowSeconds;

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public void setWindowSeconds(int windowSeconds) {
        this.windowSeconds = windowSeconds;
    }
    
}
