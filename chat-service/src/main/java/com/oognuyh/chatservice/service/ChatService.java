package com.oognuyh.chatservice.service;

import java.util.List;

import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;

public interface ChatService {
    
    List<ChannelResponse> findChannelsByUserId(String userId);
    List<MessageResponse> findMessagesByChannelId(String channelId, String userId);
    ChannelResponse findChannelBetweenUserIds(String currentUserId, String userId);
    ChannelResponse findChannelById(String channelId, String userId);
    ChannelResponse join(String userId, String channelId);
    void leave(String userId, String channelId);
    MessageResponse send(NewMessageRequest request);
    MessageResponse read(String messageId, String userId);
}
