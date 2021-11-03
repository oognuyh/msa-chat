package com.oognuyh.chatservice.repository;

import java.util.List;

import com.oognuyh.chatservice.model.Message;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findMessagesByChannelId(String channelId);
    List<Message> findMessagesByChannelIdAndUnreaderIds(String channelId, String unreaderId);
    Integer countMessagesByChannelIdAndUnreaderIds(String channelId, String unreaderId);
}
