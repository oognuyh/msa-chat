package com.oognuyh.friendservice.payload.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
        
    private NotificationType type;

    private String message;

    private String senderId;

    private List<String> recipientIds;

    private String channelId;

    private String messageId;

    public enum NotificationType {
        NEW_MESSAGE, USER_CHANGED_IN_CHANNELS, USER_CHANGED_IN_FRIENDS
    }
}
