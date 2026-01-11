package com.priyansh.notification_system.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationDlqConsumer {

    @KafkaListener(
        topics = "notification.dlq",
        groupId = "notification-dlq-group"
    )
    public void consumeDlq(String message) {
        log.error("DLQ message received: {}", message);
    }
}
