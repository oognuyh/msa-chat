package com.oognuyh.notificationservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig(
    @Value("\${spring.kafka.template.notification-topic}") private val NOTIFICATION_TOPIC: String
) {

    @Bean
    fun notificationTopic() = TopicBuilder
        .name(NOTIFICATION_TOPIC)
        .build()
}