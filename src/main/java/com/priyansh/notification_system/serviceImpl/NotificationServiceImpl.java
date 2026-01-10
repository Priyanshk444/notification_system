package com.priyansh.notification_system.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.priyansh.notification_system.dto.CreateNotificationRequest;
import com.priyansh.notification_system.dto.NotificationResponse;
import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.repository.NotificationRepository;
import com.priyansh.notification_system.services.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setContent(request.getContent());
        notification.setTitle(request.getTitle());
        notification.setType(request.getType());
        notification.setStatus(NotificationStatus.PENDING);

        notification = notificationRepository.save(notification);

        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getType(),
                notification.getStatus()
        );
    }

    @Override
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findById(userId)
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(),n.getTitle(), n.getContent(), n.getType(), n.getStatus()))
                .toList();
    }
}
