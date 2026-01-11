package com.priyansh.notification_system.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {

    private final Counter createdCounter;
    private final Counter sentSuccessCounter;
    private final Counter sentFailedCounter;
    private final Counter rateLimitedCounter;

    public NotificationMetrics(MeterRegistry registry) {

        this.createdCounter = Counter.builder("notifications.created")
                .description("Total notifications created")
                .register(registry);

        this.sentSuccessCounter = Counter.builder("notifications.sent.success")
                .description("Notifications sent successfully")
                .register(registry);

        this.sentFailedCounter = Counter.builder("notifications.sent.failed")
                .description("Notifications that failed to send")
                .register(registry);

        this.rateLimitedCounter = Counter.builder("notifications.rate_limited")
                .description("Requests blocked by rate limiting")
                .register(registry);
    }

    public void incrementCreated() {
        createdCounter.increment();
    }

    public void incrementSentSuccess() {
        sentSuccessCounter.increment();
    }

    public void incrementSentFailed() {
        sentFailedCounter.increment();
    }

    public void incrementRateLimited() {
        rateLimitedCounter.increment();
    }
}