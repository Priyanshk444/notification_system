package com.priyansh.notification_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.priyansh.notification_system.entity.NotificationDeliveryLog;

import java.util.List;

public interface NotificationDeliveryLogRepository
        extends JpaRepository<NotificationDeliveryLog, Long> {

    List<NotificationDeliveryLog> findByNotificationId(Long notificationId);
}
