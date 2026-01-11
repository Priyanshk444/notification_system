package com.priyansh.notification_system.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventProducer {

    private static final String TOPIC = "notification.send";

    private final KafkaTemplate<String,String> kafkaTemplate;

    public void publishNotification(Long notificationId){

        String key = notificationId.toString();
        String value = notificationId.toString();

        kafkaTemplate.send(TOPIC, key, value);

        log.info(
            "Published notification event to Kafka. topic={}, notificationId={}",
            TOPIC,
            notificationId
        );
    }

}
