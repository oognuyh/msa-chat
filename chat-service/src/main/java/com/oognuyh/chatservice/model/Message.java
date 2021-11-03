package com.oognuyh.chatservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private String id;

    private String channelId;

    private String senderId;

    private String content;

    private List<String> unreaderIds;

    @CreatedDate
    private Date createdAt;

    private Message(String channelId, String senderId, String content, List<String> unreaderIds) {
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.unreaderIds = unreaderIds;
    }

    public static Message of(String channelId, String senderId, String content, List<String> unreaderIds) {
        return new Message(
            channelId,
            senderId,
            content,
            new ArrayList<>(unreaderIds)
        );
    }

    public Message read(String userId) {
        unreaderIds.remove(userId);

        return this;
    }
}