package com.oognuyh.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.template.user-changed-topic}")
    private String USER_CHANGED_TOPIC;

    @Bean
    public NewTopic userChangedTopic() {
        return TopicBuilder
            .name(USER_CHANGED_TOPIC)
            .build();
    }
}
