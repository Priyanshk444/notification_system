package com.priyansh.notification_system.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.priyansh.notification_system.dto.CreateNotificationRequest;
import com.priyansh.notification_system.dto.NotificationResponse;

@Service
public interface NotificationService {

    NotificationResponse createNotification(CreateNotificationRequest request);
    List<NotificationResponse> getNotificationsForUser(Long userId);
    
} 
