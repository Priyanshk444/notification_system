package com.priyansh.notification_system.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.priyansh.notification_system.dto.NotificationResponse;
import com.priyansh.notification_system.services.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserNotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}/notifications")
    public List<NotificationResponse> getUserNotifications(
            @PathVariable Long userId) {

        return notificationService.getNotificationsForUser(userId);
    }
}

