package com.priyansh.notification_system.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDlqProducer {

    private static final String DLQ_TOPIC = "notification.dlq";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendtoDlq(Long notificationId, String reason) {
        
        String key = notificationId.toString();
        String value = notificationId + "|" + reason;

        kafkaTemplate.send(DLQ_TOPIC, key, value);

        log.info("Sent to DLQ. notificationId={}, reason={}", notificationId, reason);
        
    }

}
