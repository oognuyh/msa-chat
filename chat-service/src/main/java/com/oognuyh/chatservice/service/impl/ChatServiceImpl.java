package com.oognuyh.chatservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.chatservice.model.Channel;
import com.oognuyh.chatservice.model.Channel.Type;
import com.oognuyh.chatservice.model.Message;
import com.oognuyh.chatservice.payload.event.NotificationEvent;
import com.oognuyh.chatservice.payload.event.NotificationEvent.NotificationType;
import com.oognuyh.chatservice.payload.event.UserChangedEvent;
import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;
import com.oognuyh.chatservice.payload.response.UserResponse;
import com.oognuyh.chatservice.repository.ChannelRepository;
import com.oognuyh.chatservice.repository.MessageRepository;
import com.oognuyh.chatservice.repository.UserRepository;
import com.oognuyh.chatservice.service.ChatService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.template.notification-topic}")
    private String NOTIFICATION_TOPIC;

    @Override
    public List<ChannelResponse> findChannelsByUserId(String userId) {
        return channelRepository.findChannelsByParticipantIdsOrderByLastMessageCreatedAtDesc(userId).stream()
            .map(channel -> ChannelResponse.of(channel, userId, getParticipants(channel.getParticipantIds())))
            .map(channelResponse -> channelResponse.setNumOfUnreadMessages(
                messageRepository.countMessagesByChannelIdAndUnreaderIds(channelResponse.getId(), userId)))
            .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse findChannelById(String channelId, String userId) {
        messageRepository.saveAll(
            messageRepository.findMessagesByChannelIdAndUnreaderIds(channelId, userId).stream()
                .map(unreadMessage -> unreadMessage.read(userId))
                .collect(Collectors.toList())
        );

        return channelRepository.findById(channelId)
            .map(channel -> ChannelResponse.of(channel, userId, getParticipants(channel.getParticipantIds())))
            .map(channelResponse -> channelResponse.setMessages(messageRepository.findMessagesByChannelId(channelId).stream()
                .map(MessageResponse::of)
                .collect(Collectors.toList())))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<MessageResponse> findMessagesByChannelId(String channelId, String userId) {
        messageRepository.saveAll(
            messageRepository.findMessagesByChannelIdAndUnreaderIds(channelId, userId).stream()
                .map(unreadMessage -> unreadMessage.read(userId))
                .collect(Collectors.toList())
        );

        return messageRepository.findMessagesByChannelId(channelId).stream()
            .map(MessageResponse::of)
            .collect(Collectors.toList());
    }

    @Override
    public ChannelResponse findChannelBetweenUserIds(String currentUserId, String userId) {
        List<String> participantIds = List.of(currentUserId, userId).stream().sorted().collect(Collectors.toList());
        Optional<Channel> existingChannel = channelRepository.findChannelByTypeAndParticipantIds(Type.DIRECT, participantIds);
        
        if (existingChannel.isPresent()) {
            return existingChannel
                .map(channel -> ChannelResponse.of(channel, currentUserId, getParticipants(participantIds)))
                .get();
        } else {
            Channel newChannel = channelRepository.save(
                Channel.builder()
                    .type(Type.DIRECT)
                    .participantIds(participantIds)
                    .build()
            );

            return ChannelResponse.of(newChannel, currentUserId, getParticipants(participantIds));
        }
    }

    @Override
    public MessageResponse send(NewMessageRequest request) throws JsonProcessingException {
        Message newMessage = messageRepository.save(request.toEntity());

        channelRepository.save(
            channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .updateLastMessage(newMessage)
        );
        
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String senderName = jwt.getClaimAsString("name");

        kafkaTemplate.send(
            NOTIFICATION_TOPIC, 
            objectMapper.writeValueAsString(NotificationEvent.builder()
                .type(NotificationType.NEW_MESSAGE)
                .senderId(request.getSenderId())
                .message(String.format("%s: %s", senderName, newMessage.getContent()))
                .recipientIds(newMessage.getUnreaderIds())
                .channelId(newMessage.getChannelId())
                .messageId(newMessage.getId())
                .build())
        );

        return MessageResponse.of(newMessage);
    }

    @Override
    public MessageResponse read(String messageId, String userId) {
        return messageRepository.findById(messageId)
            .map(message -> messageRepository.save(message.read(userId)))
            .map(MessageResponse::of)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public ChannelResponse join(String userId, String channelId) {
        Channel channel = channelRepository.save(
            channelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .join(userId)
        );
        
        // kafkaTemplate.send(NOTIFICATION_TOPIC, NotificationEvent);

        return ChannelResponse.of(channel, userId, getParticipants(channel.getParticipantIds()));
    }

    @Override
    public void leave(String userId, String channelId) {
        channelRepository.save(
            channelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .leave(userId)
        );
        
        // kafkaTemplate.send("notification-topic", NotificationEvent);
    }

    private List<UserResponse> getParticipants(List<String> participantIds) {
        return participantIds.stream()
            .map(userRepository::findUserById)
            .collect(Collectors.toList());
    }

    @KafkaListener(
        topics = "${spring.kafka.template.user-changed-topic}", 
        groupId = "${spring.kafka.consumer.user-changed-group-id}",
        containerFactory = "userChangedListenerFactory"
    )
    private void onUserChanged(@Payload String payload) throws JsonMappingException, JsonProcessingException {
        UserChangedEvent userChangedEvent = objectMapper.readValue(payload, UserChangedEvent.class);

        List<String> recipientIds = channelRepository.findChannelsByParticipantIds(userChangedEvent.getUserId()).stream()
            .flatMap(channel -> channel.getParticipantIds().stream())
            .distinct()
            .filter(participantId -> !participantId.equals(userChangedEvent.getUserId()))
            .collect(Collectors.toList());

        NotificationEvent notificationEvent = NotificationEvent.builder()
            .type(NotificationType.USER_CHANGED_IN_CHANNELS)
            .senderId(userChangedEvent.getUserId())
            .recipientIds(recipientIds)
            .build();

        kafkaTemplate.send(NOTIFICATION_TOPIC, objectMapper.writeValueAsString(notificationEvent));
    }
}
