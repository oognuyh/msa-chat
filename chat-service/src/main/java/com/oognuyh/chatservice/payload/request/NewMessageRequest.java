package com.oognuyh.chatservice.payload.request;

import java.util.List;

import com.oognuyh.chatservice.model.Message;

import lombok.Data;

@Data
public class NewMessageRequest {
    
    private String senderId;

    private String senderName;

    private String channelId;

    private String content;

    private List<String> unreaderIds;

    public Message toEntity() {
        return Message.of(channelId, senderId, content, unreaderIds);
    }
}
