package com.priyansh.notification_system.services.async;

import org.springframework.stereotype.Service;

import com.priyansh.notification_system.entity.DeliveryStatus;
import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.entity.NotificationDeliveryLog;
import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.entity.User;
import com.priyansh.notification_system.repository.NotificationDeliveryLogRepository;
import com.priyansh.notification_system.repository.NotificationRepository;
import com.priyansh.notification_system.repository.UserRepository;
import com.priyansh.notification_system.services.sender.NotificationSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationAsyncService {

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;
    private final UserRepository userRepository;
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;

    public void processNotificationAsync(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalStateException("Notification not found with id: " + notificationId));

        int attempt = notification.getRetryCount() + 1;
        
        try {
            // Move to PROCESSING
            notification.setStatus(NotificationStatus.PROCESSING);
            notificationRepository.save(notification);

            // Fetch user details
            User user = userRepository.findById(notification.getUserId()).orElseThrow(
                    () -> new IllegalStateException("User not found with id: " + notification.getUserId()));

            // Send notification
            notificationSender.send(notification, user.getEmail());

            // SUCCESS log
            notificationDeliveryLogRepository.save(
                    new NotificationDeliveryLog(
                            null,
                            notificationId,
                            attempt,
                            DeliveryStatus.SUCCESS,
                            null,
                            null
                    )
            );

            // Mark as SENT
            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);

            log.info("Notification {} sent successfully", notificationId);

        } catch (Exception ex) {

            //  FAILED log
            notificationDeliveryLogRepository.save(
                    new NotificationDeliveryLog(
                            null,
                            notificationId,
                            attempt,
                            DeliveryStatus.FAILED,
                            ex.getMessage(),
                            null
                    )
            );

            // Handle failure
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);

            // Log only (no rethrow)
            log.error(
                "Notification {} failed on attempt {}",
                notificationId,
                attempt,
                ex
            );
        }
    }

}
