package com.oognuyh.friendservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.friendservice.model.Friend;
import com.oognuyh.friendservice.payload.event.NotificationEvent;
import com.oognuyh.friendservice.payload.event.NotificationEvent.NotificationType;
import com.oognuyh.friendservice.payload.event.UserChangedEvent;
import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;
import com.oognuyh.friendservice.repository.FriendRepository;
import com.oognuyh.friendservice.repository.UserRepository;
import com.oognuyh.friendservice.service.FriendService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;    

    @Value("${spring.kafka.template.notification-topic}")
    private String NOTIFICATION_TOPIC;

    public List<FriendResponse> findFriendsByUserId(String userId) {
        return friendRepository.findFriendsByUserId(userId).stream()
            .map(FriendResponse::of)
            .map(friend -> friend.setDetails(userRepository.findUserById(friend.getId())))
            .collect(Collectors.toList());
    }

    public FriendResponse addNewFriend(String userId, AddingNewFriendRequest request) {        
        return FriendResponse.of(
                friendRepository.save(Friend.builder()
                    .id(request.getFriendId())
                    .userId(userId)
                    .build()))
            .setDetails(userRepository.findUserById(request.getFriendId()));
    }

    public void deleteFriendByIdAndUserId(String id, String userId) {
        friendRepository.deleteFriendByIdAndUserId(id, userId);
    }

    @KafkaListener(
        topics = "${spring.kafka.template.user-changed-topic}", 
        groupId = "${spring.kafka.consumer.user-changed-group-id}",
        containerFactory = "userChangedListenerFactory"
    )
    private void onUserChanged(@Payload String payload) throws JsonMappingException, JsonProcessingException {
        UserChangedEvent userChangedEvent = objectMapper.readValue(payload, UserChangedEvent.class);

        log.info("event: {}", userChangedEvent);

        List<String> recipientIds = friendRepository.findFriendsById(userChangedEvent.getUserId()).stream()
            .map(friend -> friend.getUserId())
            .collect(Collectors.toList());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
            .type(NotificationType.USER_CHANGED_IN_FRIENDS)
            .senderId(userChangedEvent.getUserId())
            .recipientIds(recipientIds)
            .build();

        kafkaTemplate.send(NOTIFICATION_TOPIC, objectMapper.writeValueAsString(notificationEvent));
    }
}
