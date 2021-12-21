package com.oognuyh.chatservice.payload.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.oognuyh.chatservice.model.Message;

import lombok.Data;

@Data
public class NewMessageRequest {
    
    @NotNull
    private String senderId;

    @NotNull
    private String senderName;

    @NotNull
    private String channelId;

    @NotBlank
    private String content;

    private List<String> unreaderIds;

    public Message toEntity() {
        return Message.of(channelId, senderId, content, unreaderIds);
    }
}
