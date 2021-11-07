package com.oognuyh.notificationservice.config

import com.oognuyh.notificationservice.payload.event.NotificationEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}") private val BOOTSTRAP_SERVERS: String,
    @Value("\${spring.kafka.consumer.notification-group-id}") private val NOTIFICATION_GROUP_ID: String
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val configs: MutableMap<String, Any> = HashMap()

        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = NOTIFICATION_GROUP_ID

        return DefaultKafkaConsumerFactory(
            configs,
            StringDeserializer(),
            StringDeserializer()
        )
    }

    @Bean
    fun notificationListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()

        factory.consumerFactory = consumerFactory()

        return factory
    }
}