package com.priyansh.notification_system.services.sender;

import com.priyansh.notification_system.entity.Notification;

public interface NotificationSender {
    
    void send(Notification notification, String recipientEmail);
    
}
