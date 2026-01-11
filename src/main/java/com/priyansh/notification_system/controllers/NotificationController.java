package com.priyansh.notification_system.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.priyansh.notification_system.dto.CreateNotificationRequest;
import com.priyansh.notification_system.dto.NotificationResponse;
import com.priyansh.notification_system.metrics.NotificationMetrics;
import com.priyansh.notification_system.services.NotificationService;
import com.priyansh.notification_system.services.ratelimit.RateLimiterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final RateLimiterService rateLimiterService;
    private final NotificationMetrics metrics;

    @PostMapping()
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        
        // RATE LIMIT CHECK
        boolean allowed = rateLimiterService.allowRequest(request.getUserId());

        if (!allowed) {

            metrics.incrementRateLimited();

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(null);
        }

        NotificationResponse response =
                notificationService.createNotification(request);

        metrics.incrementCreated();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
}
