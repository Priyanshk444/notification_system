package com.priyansh.notification_system.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core threads always alive
        executor.setCorePoolSize(5);

        // Max threads under heavy load
        executor.setMaxPoolSize(10);

        // Queue size before spawning new threads
        executor.setQueueCapacity(100);

        // Helpful for debugging
        executor.setThreadNamePrefix("notification-async-");

        executor.initialize();
        return executor;

    }
}
