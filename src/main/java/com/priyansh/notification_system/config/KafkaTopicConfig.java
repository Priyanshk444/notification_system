package com.priyansh.notification_system.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public NewTopic notificationTopic(){
        return TopicBuilder.name("notification.send")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
