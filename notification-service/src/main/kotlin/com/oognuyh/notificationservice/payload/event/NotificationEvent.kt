package com.oognuyh.notificationservice.payload.event

data class NotificationEvent (
    var type: NotificationType,
    var message: String?,
    var senderId: String,
    var recipientIds: List<String>,
    var channelId: String?,
    var messageId: String?
) {
    enum class NotificationType {
        NEW_MESSAGE, USER_CHANGED_IN_CHANNELS, USER_CHANGED_IN_FRIENDS
    }
}