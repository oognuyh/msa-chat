package com.oognuyh.friendservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.friendservice.model.Friend;
import com.oognuyh.friendservice.payload.event.NotificationEvent;
import com.oognuyh.friendservice.payload.event.NotificationEvent.NotificationType;
import com.oognuyh.friendservice.payload.event.UserChangedEvent;
import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;
import com.oognuyh.friendservice.payload.response.UserResponse;
import com.oognuyh.friendservice.repository.FriendRepository;
import com.oognuyh.friendservice.repository.UserRepository;
import com.oognuyh.friendservice.service.FriendService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final Resilience4JCircuitBreaker circuitBreaker;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.template.notification-topic}")
    private String NOTIFICATION_TOPIC;

    private UserResponse getUserDetails(String userId) {
        return circuitBreaker.run(() -> userRepository.findUserById(userId), throwable -> {
            log.error("Failed to load user({}) details because {}", userId, throwable.getMessage());

            return UserResponse.anonymous();
        });
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> findFriendsByUserId(String userId) {
        return friendRepository.findFriendsByUserId(userId).stream()
            .map(FriendResponse::of)
            .map(friend -> friend.setDetails(getUserDetails(friend.getId())))
            .collect(Collectors.toList());
    }

    @Transactional
    public FriendResponse addNewFriend(String userId, AddingNewFriendRequest request) {        
        return FriendResponse.of(
                friendRepository.save(Friend.builder()
                    .id(request.getFriendId())
                    .userId(userId)
                    .build()))
            .setDetails(getUserDetails(request.getFriendId()));
    }

    @Transactional
    public void deleteFriendByIdAndUserId(String id, String userId) {
        friendRepository.deleteFriendByIdAndUserId(id, userId);

        log.info("Successfully user({}) deleted friend({})", userId, id);
    }

    @Transactional(readOnly = true)
    @KafkaListener(
        topics = "${spring.kafka.template.user-changed-topic}", 
        groupId = "${spring.kafka.consumer.user-changed-group-id}",
        containerFactory = "userChangedListenerFactory"
    )
    public void onUserChanged(@Payload String payload) throws JsonProcessingException {
        UserChangedEvent userChangedEvent = objectMapper.readValue(payload, UserChangedEvent.class);

        log.info("Receive user changed event with user id ({})", userChangedEvent.getUserId());

        List<String> recipientIds = friendRepository.findFriendsById(userChangedEvent.getUserId()).stream()
            .map(Friend::getUserId)
            .collect(Collectors.toList());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
            .type(NotificationType.USER_CHANGED_IN_FRIENDS)
            .senderId(userChangedEvent.getUserId())
            .recipientIds(recipientIds)
            .build();

        log.info("Notify friends({}) that user({}) has changed", notificationEvent.getRecipientIds(), notificationEvent.getSenderId());

        if (recipientIds.size() > 0) {
            kafkaTemplate.send(NOTIFICATION_TOPIC, objectMapper.writeValueAsString(notificationEvent));

            log.info("Successfully notified friends({}) of {}", notificationEvent.getRecipientIds(), notificationEvent.getType());
        } else {
            log.info("There is no recipient");
        }
    }
}
