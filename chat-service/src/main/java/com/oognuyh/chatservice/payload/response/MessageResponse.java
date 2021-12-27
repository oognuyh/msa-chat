package com.oognuyh.chatservice.payload.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oognuyh.chatservice.model.Message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
    
    private String id;

    private String channelId;

    private String senderId;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;

    private MessageResponse(String id, String channelId, String senderId, String content, Date createdAt) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static MessageResponse of(Message message) {
        return new MessageResponse(
            message.getId(),
            message.getChannelId(),
            message.getSenderId(),
            message.getContent(),
            message.getCreatedAt()         
        );
    }
}
