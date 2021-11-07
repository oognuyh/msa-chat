package com.oognuyh.notificationservice.payload.response

import com.oognuyh.notificationservice.payload.event.NotificationEvent.NotificationType
import com.oognuyh.notificationservice.payload.event.NotificationEvent

data class NotificationResponse(
    var type: NotificationType,
    var message: String?,
    var senderId: String,
    var channelId: String?,
    var messageId: String?
) {
    constructor(event: NotificationEvent): this(
        event.type,
        event.message,
        event.senderId,
        event.channelId,
        event.messageId
    )
}
