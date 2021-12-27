package com.oognuyh.notificationservice.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.oognuyh.notificationservice.logger
import com.oognuyh.notificationservice.payload.event.NotificationEvent
import com.oognuyh.notificationservice.payload.response.NotificationResponse
import com.oognuyh.notificationservice.service.NotificationService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val simpMessagingTemplate: SimpMessagingTemplate
) : NotificationService {
    private val logger = logger()

    @KafkaListener(
        topics = ["\${spring.kafka.template.notification-topic}"],
        groupId = "\${spring.kafka.consumer.notification-group-id}",
        containerFactory = "notificationListenerFactory"
    )
    override fun notify(@Payload payload: String) {
        val event = jacksonObjectMapper().readValue(payload, NotificationEvent::class.java)

        logger.info("Receive notification event with type ({})", event.type)

        event.recipientIds.forEach { recipientId ->
            simpMessagingTemplate.convertAndSend("/notifications/${recipientId}", NotificationResponse(event))
            logger.info("Successfully send event to {}", recipientId)
        }
    }
}