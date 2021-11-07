package com.oognuyh.notificationservice.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.oognuyh.notificationservice.payload.event.NotificationEvent
import com.oognuyh.notificationservice.payload.response.NotificationResponse
import com.oognuyh.notificationservice.service.NotificationService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) : NotificationService {

    @KafkaListener(
        topics = ["\${spring.kafka.template.notification-topic}"],
        groupId = "\${spring.kafka.consumer.notification-group-id}",
        containerFactory = "notificationListenerFactory"
    )
    override fun notify(@Payload payload: String) {
        val event = objectMapper.readValue(payload, NotificationEvent::class.java)
        println("event: ${event}")
        event.recipientIds.forEach { recipientId ->
            simpMessagingTemplate.convertAndSend("/notifications/${recipientId}", NotificationResponse(event))
        }
    }
}