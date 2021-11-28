package com.oognuyh.chatservice.payload.response;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.oognuyh.chatservice.model.Channel;
import com.oognuyh.chatservice.model.Channel.Type;

import org.springframework.util.StringUtils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelResponse {
    
    private String id;

    private Type type;

    private String name;

    private String imageUrl;

    private List<UserResponse> participants;

    private List<UserResponse> recipients;

    private String lastMessageContent;

    private Date lastMessageCreatedAt;

    private Integer numOfUnreadMessages;

    private List<MessageResponse> messages;

    public static ChannelResponse of(Channel channel, List<UserResponse> participants) {
        return ChannelResponse.builder()
            .id(channel.getId())
            .type(channel.getType())
            .name(channel.getName())             
            .participants(participants)
            .build();
    }

    public static ChannelResponse of(Channel channel, String userId, List<UserResponse> participants) {
        List<UserResponse> recipients = participants.stream()
            .filter(participant -> !participant.getId().equals(userId))
            .collect(Collectors.toList());

        return ChannelResponse.builder()
            .id(channel.getId())
            .type(channel.getType())
            .name(StringUtils.hasText(channel.getName()) ?
                channel.getName() :
                recipients.stream().map(other -> other.getName()).collect(Collectors.joining(", ")))
            .imageUrl(channel.getType().equals(Type.GROUP) ? 
                "" : 
                recipients.stream().map(UserResponse::getImageUrl).collect(Collectors.joining()))
            .participants(participants)
            .recipients(recipients)
            .lastMessageContent(channel.getLastMessageContent())
            .lastMessageCreatedAt(channel.getLastMessageCreatedAt())
            .build();
    }

    public ChannelResponse setNumOfUnreadMessages(Integer numOfUnreadMessages) {
        this.numOfUnreadMessages = numOfUnreadMessages;

        return this;
    }

    public ChannelResponse setMessages(List<MessageResponse> messages) {
        this.messages = messages;

        return this;
    }
}
