package com.oognuyh.chatservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    
    @Id
    private String id;

    private String name;

    private List<String> participantIds;

    private String lastMessageContent;

    private Date lastMessageCreatedAt;

    private Type type;

    public enum Type {
        DIRECT, GROUP
    }

    @Builder
    public Channel(String name, List<String> participantIds, Type type) {
        this.name = name;
        this.participantIds = new ArrayList<>(participantIds);
        this.type = type;
        this.lastMessageContent = "";
        this.lastMessageCreatedAt = new Date();
    }

    public Channel updateLastMessage(Message message) {
        this.lastMessageContent = message.getContent();
        this.lastMessageCreatedAt = message.getCreatedAt();

        return this;
    }

    public Channel join(String userId) {
        if (!this.participantIds.contains(userId)) {
            this.participantIds.add(userId);
        }

        return this;
    }

    public Channel leave(String userId) {
        this.participantIds.remove(userId);

        return this;
    }
}
