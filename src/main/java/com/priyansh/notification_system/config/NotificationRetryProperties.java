package com.priyansh.notification_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@ConfigurationProperties(prefix = "notification.retry")
@Getter
public class NotificationRetryProperties {

    private int maxRetries = 3;
    private long retryIntervalMs = 10000; // 10 seconds

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setIntervalMs(long intervalMs) {
        this.retryIntervalMs = intervalMs;
    }
}
