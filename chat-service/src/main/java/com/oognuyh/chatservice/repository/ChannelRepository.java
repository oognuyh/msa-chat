package com.oognuyh.chatservice.repository;

import java.util.List;
import java.util.Optional;

import com.oognuyh.chatservice.model.Channel;
import com.oognuyh.chatservice.model.Channel.Type;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelRepository extends MongoRepository<Channel, String> {
    
    Optional<Channel> findChannelByTypeAndParticipantIds(Type type, List<String> participantIds);
    List<Channel> findChannelsByTypeAndNameContainingIgnoreCase(Type type, String name);
    List<Channel> findChannelsByParticipantIdsOrderByLastMessageCreatedAtDesc(String participantId);
    List<Channel> findChannelsByParticipantIds(String participantId);
}
