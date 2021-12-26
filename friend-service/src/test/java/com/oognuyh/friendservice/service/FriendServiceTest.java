package com.oognuyh.friendservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.friendservice.config.TraceConfig;
import com.oognuyh.friendservice.model.Friend;
import com.oognuyh.friendservice.payload.event.UserChangedEvent;
import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;
import com.oognuyh.friendservice.payload.response.UserResponse;
import com.oognuyh.friendservice.repository.FriendRepository;
import com.oognuyh.friendservice.repository.UserRepository;
import com.oognuyh.friendservice.service.impl.FriendServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.SettableListenableFuture;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { TraceConfig.class, Resilience4JCircuitBreakerFactory.class, FriendServiceImpl.class })
public class FriendServiceTest {

    @Autowired private FriendServiceImpl friendService;

    @MockBean private FriendRepository friendRepository;

    @MockBean private UserRepository userRepository;

    @MockBean private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void givenUserIdWhenFindFriendsByUserIdThenReturnFriendsOfUser() {
        // given
        String userId = UUID.randomUUID().toString();
        String friendId = UUID.randomUUID().toString();
        BDDMockito.given(friendRepository.findFriendsByUserId(BDDMockito.anyString()))
                .willReturn(List.of(Friend.builder().id(friendId).userId(userId).build()));
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
                .willReturn(new UserResponse(friendId, "friend", "email", "", false, ""));

        // when
        List<FriendResponse> friends = friendService.findFriendsByUserId(userId);
        Optional<FriendResponse> friend = friends.stream().findFirst();

        // then
        Assertions.assertThat(friend.isPresent()).isTrue();
        Assertions.assertThat(friend.get().getUserId()).isEqualTo(userId);
        Assertions.assertThat(friend.get().getId()).isEqualTo(friendId);
        Assertions.assertThat(friend.get().getEmail()).isEqualTo("email");
        Assertions.assertThat(friend.get().getIsActive()).isFalse();

        // verify
        BDDMockito.verify(friendRepository)
                .findFriendsByUserId(BDDMockito.anyString());
        BDDMockito.verify(userRepository)
                .findUserById(BDDMockito.anyString());
    }

    @Test
    void givenUserIdWhenUserServiceNotAvailableThenReturnAnonymous() {
        // given
        String userId = UUID.randomUUID().toString();
        String friendId = UUID.randomUUID().toString();
        BDDMockito.given(friendRepository.findFriendsByUserId(BDDMockito.anyString()))
                .willReturn(List.of(Friend.builder().id(friendId).userId(userId).build()));
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
                .willThrow(RuntimeException.class);

        // when
        List<FriendResponse> friends = friendService.findFriendsByUserId(userId);
        Optional<FriendResponse> friend = friends.stream().findFirst();

        // then
        Assertions.assertThat(friend.isPresent()).isTrue();
        Assertions.assertThat(friend.get().getUserId()).isEqualTo(userId);
        Assertions.assertThat(friend.get().getId()).isEqualTo(friendId);
        Assertions.assertThat(friend.get().getName()).isEqualToIgnoringCase("anonymous");

        // verify
        BDDMockito.verify(friendRepository)
                .findFriendsByUserId(BDDMockito.anyString());
        BDDMockito.verify(userRepository)
                .findUserById(BDDMockito.anyString());
    }

    @Test
    void givenUserIdAndRequestWhenAddNewFriendThenReturnNewFriend() {
        // given
        String userId = UUID.randomUUID().toString();
        AddingNewFriendRequest request = new AddingNewFriendRequest(UUID.randomUUID().toString());
        BDDMockito.given(friendRepository.save(BDDMockito.any(Friend.class)))
                .willReturn(Friend.builder().id(request.getFriendId()).userId(userId).build());
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
                .willReturn(new UserResponse(request.getFriendId(), "name", "", "", false, ""));

        // when
        FriendResponse friend = friendService.addNewFriend(userId, request);

        // then
        Assertions.assertThat(friend).isNotNull();
        Assertions.assertThat(friend.getUserId()).isEqualTo(userId);
        Assertions.assertThat(friend.getId()).isEqualTo(request.getFriendId());

        // verify
        BDDMockito.verify(friendRepository)
                .save(BDDMockito.any(Friend.class));
        BDDMockito.verify(userRepository)
                .findUserById(BDDMockito.anyString());
    }

    @Test
    void givenIdAndUserIdWhenDeleteFriendByIdAndUserIdThenFriendsOfUserIsZero() {
        // given
        String userId = UUID.randomUUID().toString();
        String friendId = UUID.randomUUID().toString();
        Mockito.doNothing().when(friendRepository).deleteFriendByIdAndUserId(Mockito.isA(String.class), Mockito.isA(String.class));

        // when
        friendService.deleteFriendByIdAndUserId(friendId, userId);

        // then

        // verify
        Mockito.verify(friendRepository).deleteFriendByIdAndUserId(friendId, userId);
    }

    @Test
    void givenUserChangedEventWhenRecipientExistsThenNotify() throws JsonProcessingException {
        // given
        String changedUserId = UUID.randomUUID().toString();
        String payload = new ObjectMapper().writeValueAsString(UserChangedEvent.builder().userId(changedUserId).build());
        ReflectionTestUtils.setField(friendService, "NOTIFICATION_TOPIC", "notification-topic");

        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());

        BDDMockito.given(kafkaTemplate.send(BDDMockito.anyString(), BDDMockito.anyString()))
                .willReturn(future);
        BDDMockito.given(friendRepository.findFriendsById(BDDMockito.anyString()))
                .willReturn(List.of(Friend.builder().id(changedUserId).userId(UUID.randomUUID().toString()).build()));

        // when
        friendService.onUserChanged(payload);

        // then

        // verify
        BDDMockito.verify(kafkaTemplate).send(BDDMockito.anyString(), BDDMockito.anyString());
        BDDMockito.verify(friendRepository).findFriendsById(BDDMockito.anyString());
    }

    @Test
    void givenUserChangedEventWhenNoRecipientThenDoesntNotify() throws JsonProcessingException {
        // given
        String changedUserId = UUID.randomUUID().toString();
        String payload = new ObjectMapper().writeValueAsString(UserChangedEvent.builder().userId(changedUserId).build());
        ReflectionTestUtils.setField(friendService, "NOTIFICATION_TOPIC", "notification-topic");

        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());

        BDDMockito.given(friendRepository.findFriendsById(BDDMockito.anyString()))
                .willReturn(List.of());

        // when
        friendService.onUserChanged(payload);

        // then
        Assertions.assertThatExceptionOfType(WantedButNotInvoked.class)
                .isThrownBy(() -> BDDMockito.verify(kafkaTemplate).send(BDDMockito.anyString(), BDDMockito.anyString()));

        // verify
        BDDMockito.verify(friendRepository).findFriendsById(BDDMockito.anyString());
    }
}
