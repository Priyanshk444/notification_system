package com.priyansh.notification_system.kafka.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.priyansh.notification_system.entity.DeliveryStatus;
import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.entity.NotificationDeliveryLog;
import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.entity.User;
import com.priyansh.notification_system.kafka.producer.NotificationDlqProducer;
import com.priyansh.notification_system.metrics.NotificationMetrics;
import com.priyansh.notification_system.repository.NotificationDeliveryLogRepository;
import com.priyansh.notification_system.repository.NotificationRepository;
import com.priyansh.notification_system.repository.UserRepository;
import com.priyansh.notification_system.services.sender.NotificationSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    private final NotificationSender emailNotificationSender;
    private final NotificationDlqProducer notificationDlqProducer;
    private final NotificationMetrics metrics;

    @KafkaListener(topics = "notification.send", groupId = "notification-consumers")
    @Transactional
    public void consume(String Message) {

        Long notificationId = Long.parseLong(Message);

        log.info("Consumed notification event. notificationId={}", notificationId);

        // Retry Locking (atomic)
        int updated = notificationRepository.updateStatusIfAllowed(
                notificationId, NotificationStatus.PROCESSING,
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED));

        if (updated == 0) {
            log.info(
                    "Notification {} already being processed by another worker",
                    notificationId);
            return;
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalStateException("Notification not found with id: " + notificationId));

        int attempt = notification.getRetryCount() + 1;

        try {
            // Check retry limit
            if (notification.getRetryCount() >= notification.getMaxRetries()) {

                notification.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(notification);

                notificationDlqProducer.sendtoDlq(
                        notificationId,
                        "Max retries exceeded");

                log.error(
                        "Notification {} permanently failed and sent to DLQ",
                        notificationId);

                return;

            }

            // Fetch user
            User user = userRepository.findById(notification.getUserId())
                    .orElseThrow();

            // Send notification
            emailNotificationSender.send(notification, user.getEmail());

            // Log success
            notificationDeliveryLogRepository.save(
                    new NotificationDeliveryLog(
                            null,
                            notificationId,
                            attempt,
                            DeliveryStatus.SUCCESS,
                            null,
                            null));

            // Mark SENT
            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);

            metrics.incrementSentSuccess();


            log.info("Notification {} sent successfully", notificationId);

        } catch (Exception ex) {
            // 7️⃣ Log failure
            notificationDeliveryLogRepository.save(
                    new NotificationDeliveryLog(
                            null,
                            notificationId,
                            attempt,
                            DeliveryStatus.FAILED,
                            ex.getMessage(),
                            null));

            notification.setRetryCount(attempt);
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);

            metrics.incrementSentFailed();

            log.error(
                    "Notification {} failed on attempt {}",
                    notificationId,
                    attempt,
                    ex);

            // Let Kafka re-deliver if needed
            throw ex;
        }
    }

}
