package com.priyansh.notification_system.dto;

import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.entity.NotiicationType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private NotiicationType type;
    private NotificationStatus status;

}
