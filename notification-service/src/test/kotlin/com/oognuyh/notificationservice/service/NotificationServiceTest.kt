package com.oognuyh.notificationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.oognuyh.notificationservice.payload.event.NotificationEvent
import com.oognuyh.notificationservice.payload.response.NotificationResponse
import com.oognuyh.notificationservice.service.impl.NotificationServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.*

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @InjectMocks private lateinit var notificationService: NotificationServiceImpl

    @Mock private lateinit var simpMessagingTemplate: SimpMessagingTemplate

    private fun getRandomId() = UUID.randomUUID().toString()

    @Test
    fun givenPayloadWhenGettingNotificationThenNotifyRecipients() {
        // given
        val recipientIds = listOf(getRandomId(), getRandomId(), getRandomId())
        val payload = jacksonObjectMapper().writeValueAsString(
            NotificationEvent(
                type = NotificationEvent.NotificationType.NEW_MESSAGE,
                message = "",
                senderId = getRandomId(),
                recipientIds = recipientIds,
                channelId = getRandomId(),
                messageId = null
            )
        )

        // when
        notificationService.notify(payload)

        // then

        // verify
        BDDMockito.verify(simpMessagingTemplate, times(recipientIds.size)).convertAndSend(anyString(), any(NotificationResponse::class.java))
    }
}