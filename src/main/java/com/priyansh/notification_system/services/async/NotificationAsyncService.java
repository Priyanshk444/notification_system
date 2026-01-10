package com.priyansh.notification_system.services.async;

import org.springframework.stereotype.Service;

import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.repository.NotificationRepository;
import com.priyansh.notification_system.services.sender.NotificationSender;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationAsyncService {

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    public void processNotificationAsync(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> 
            new IllegalStateException("Notification not found with id: " + notificationId));

            try{
                //  Move to PROCESSING
                notification.setStatus(NotificationStatus.PROCESSING);
                notificationRepository.save(notification);

                //  Send notification
                notificationSender.send(notification);

                //  Mark as SENT
                notification.setStatus(NotificationStatus.SENT);
                notificationRepository.save(notification);

            } catch (Exception ex) {
                //  Handle failure
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setStatus(NotificationStatus.FAILED);

                notificationRepository.save(notification);

                // Log only (no rethrow)
                System.err.println("Failed to send notification " +
                    notificationId + ": " + ex.getMessage());
            }
    }

}
