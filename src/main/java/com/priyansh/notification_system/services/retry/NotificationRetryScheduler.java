package com.priyansh.notification_system.services.retry;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.repository.NotificationRepository;
import com.priyansh.notification_system.services.async.NotificationAsyncService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationAsyncService notificationAsyncService;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedDelayString = "${notification.retry.retry-interval-ms}") 
    @Transactional
    public void retryFailedNotifications() {

        List<Notification> failedNotifications = notificationRepository.findByStatusAndRetryCountLessThan(
                NotificationStatus.FAILED,
                3 // Max retry count
        );

        if(failedNotifications.isEmpty()) {
            log.info("No failed notifications to retry at this time.");
            return;
        }

        log.info("Retrying {} failed notifications...", failedNotifications.size());

        

        

        for (Notification notification : failedNotifications) {
            log.info(
                "Retrying notificationId={}, attempt={}",
                notification.getId(),
                notification.getRetryCount() + 1
            );

            // Mark back to PENDING so async worker picks it up cleanly
            notification.setStatus(NotificationStatus.PENDING);
            notificationRepository.save(notification);

            // Re-trigger async pipeline
            notificationAsyncService.processNotificationAsync(notification.getId());
        }
    }
}
