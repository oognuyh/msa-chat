package com.oognuyh.chatservice.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oognuyh.chatservice.payload.request.NewGroupChannelRequest;
import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;

public interface ChatService {
    
    List<ChannelResponse> findChannelsByUserId(String userId);
    List<ChannelResponse> search(String queryTerm);
    ChannelResponse findChannelBetweenUserIds(String currentUserId, String userId);
    ChannelResponse findChannelById(String channelId, String userId);
    ChannelResponse join(String userId, String channelId) throws JsonProcessingException;
    ChannelResponse createNewGroupChannel(String userId, NewGroupChannelRequest request);
    void leave(String userId, String channelId) throws JsonProcessingException;
    MessageResponse send(NewMessageRequest request) throws JsonProcessingException;
    MessageResponse read(String messageId, String userId);
}
