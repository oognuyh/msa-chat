package com.oognuyh.chatservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oognuyh.chatservice.config.TraceConfig;
import com.oognuyh.chatservice.model.Channel;
import com.oognuyh.chatservice.model.Channel.Type;
import com.oognuyh.chatservice.model.Message;
import com.oognuyh.chatservice.payload.request.NewGroupChannelRequest;
import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;
import com.oognuyh.chatservice.payload.response.UserResponse;
import com.oognuyh.chatservice.repository.ChannelRepository;
import com.oognuyh.chatservice.repository.MessageRepository;
import com.oognuyh.chatservice.repository.UserRepository;
import com.oognuyh.chatservice.service.impl.ChatServiceImpl;

import org.assertj.core.api.Assertions;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.concurrent.SettableListenableFuture;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { TraceConfig.class, Resilience4JCircuitBreakerFactory.class, ChatServiceImpl.class })
public class ChatServiceTest {
    
    @Autowired private ChatService chatService;

    @MockBean private ChannelRepository channelRepository;

    @MockBean private MessageRepository messageRepository;

    @MockBean private UserRepository userRepository;

    @MockBean private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void givenUserIdWhenFindChannelsThenReturnChannels() {
        // given
        String userId = getRandomId();
        List<Channel> channels = List.of(
            new Channel(getRandomId(), null, List.of(userId, getRandomId(), getRandomId()), null, null, Type.GROUP),
            new Channel(getRandomId(), null, List.of(userId, getRandomId()), null, null, Type.DIRECT),
            new Channel(getRandomId(), null, List.of(userId, getRandomId()), null, null, Type.DIRECT)
        );
        BDDMockito.given(channelRepository.findChannelsByParticipantIdsOrderByLastMessageCreatedAtDesc(BDDMockito.anyString()))
            .willReturn(channels);
        BDDMockito.given(messageRepository.countMessagesByChannelIdAndUnreaderIds(BDDMockito.anyString(), BDDMockito.anyString()))
            .willReturn(new Random().nextInt(10));
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
            .willAnswer(invocation -> new UserResponse(invocation.getArgument(0, String.class), null, null, null, null, null));

        // when
        List<ChannelResponse> channelResponses = chatService.findChannelsByUserId(userId);

        // then
        Assertions.assertThat(channelResponses.size()).isEqualTo(channels.size());

        // verify
        BDDMockito.verify(channelRepository).findChannelsByParticipantIdsOrderByLastMessageCreatedAtDesc(BDDMockito.anyString());
        BDDMockito.verify(messageRepository, BDDMockito.times(channels.size())).countMessagesByChannelIdAndUnreaderIds(BDDMockito.anyString(), BDDMockito.anyString());
        BDDMockito.verify(userRepository, BDDMockito.times(channels.stream().mapToInt(channel -> channel.getParticipantIds().size()).sum())).findUserById(BDDMockito.anyString());
    }

    @Test
    void givenChannelIdAndUserIdWhenFindChannelThenReadAllMessagesAndReturnChannel() {
        // given
        String channelId = getRandomId();
        String userId = getRandomId();
        List<String> participantIds = List.of(userId, getRandomId());
        List<Message> unreadMessages = List.of(
            Message.of(channelId, userId, RandomString.make(), participantIds),
            Message.of(channelId, userId, RandomString.make(), participantIds)
        );
        Channel channel = new Channel(channelId, null, participantIds, null, null, Type.DIRECT);
        BDDMockito.given(messageRepository.findMessagesByChannelIdAndUnreaderIds(channelId, userId))
            .willReturn(unreadMessages);
        BDDMockito.given(messageRepository.saveAll(BDDMockito.anyList()))
            .willReturn(List.of());
        BDDMockito.given(messageRepository.findMessagesByChannelId(BDDMockito.anyString()))
            .willReturn(unreadMessages.stream().map(message -> {
                message.getUnreaderIds().remove(userId);
                return message;
            }).collect(Collectors.toList()));
        BDDMockito.given(channelRepository.findById(channelId))
            .willReturn(Optional.of(channel));
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
            .willAnswer(invocation -> new UserResponse(invocation.getArgument(0, String.class), null, null, null, null, null));

        // when
        ChannelResponse channelResponse = chatService.findChannelById(channelId, userId);

        // then
        Assertions.assertThat(channelResponse.getId()).isEqualTo(channelId);

        // verify
        BDDMockito.verify(messageRepository).findMessagesByChannelIdAndUnreaderIds(channelId, userId);
        BDDMockito.verify(messageRepository).saveAll(BDDMockito.anyList());
        BDDMockito.verify(messageRepository).findMessagesByChannelId(BDDMockito.anyString());
        BDDMockito.verify(channelRepository).findById(channelId);
        BDDMockito.verify(userRepository, BDDMockito.times(channel.getParticipantIds().size())).findUserById(BDDMockito.anyString());
    }

    @Test
    void givenQueryTermWhenSearchThenReturnChannelsContainingQueryTerm() {
        // given
        String queryTerm = RandomString.make(5);
        List<Channel> channels = List.of(
            new Channel(getRandomId(), queryTerm + "suffix", List.of(), "", null, Type.GROUP),
            new Channel(getRandomId(), queryTerm, List.of(), "", null, Type.GROUP),
            new Channel(getRandomId(), "prefix" + queryTerm, List.of(), "", null, Type.GROUP)
        );
        BDDMockito.given(channelRepository.findChannelsByTypeAndNameContainingIgnoreCase(BDDMockito.any(Type.class), BDDMockito.anyString()))
            .willReturn(channels);

        // when
        List<ChannelResponse> channelResponses = chatService.search(queryTerm);

        // then
        Assertions.assertThat(channelResponses.size()).isEqualTo(channels.size());

        // verify
        BDDMockito.verify(channelRepository).findChannelsByTypeAndNameContainingIgnoreCase(BDDMockito.any(Type.class), BDDMockito.anyString());
    }

    @Test
    void givenUserIdsWhenFindChannelThenReturnNewChannelIfNotExists() {
        // given
        String currentUserId = getRandomId();
        String userId = getRandomId();
        BDDMockito.given(channelRepository.findChannelByTypeAndParticipantIds(BDDMockito.any(Type.class), BDDMockito.anyList()))
            .willReturn(Optional.empty());
        BDDMockito.given(channelRepository.save(BDDMockito.any(Channel.class)))
            .willAnswer(invocation -> invocation.getArgument(0, Channel.class));
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
            .willAnswer(invocation -> new UserResponse(invocation.getArgument(0, String.class), null, null, null, null, null));

        // when
        ChannelResponse channelResponse = chatService.findChannelBetweenUserIds(currentUserId, userId);

        // then
        Assertions.assertThat(channelResponse.getType()).isEqualTo(Type.DIRECT);
        Assertions.assertThat(channelResponse.getParticipants().stream().map(UserResponse::getId).allMatch(participantId -> participantId.equals(userId) || participantId.equals(currentUserId)))
            .isTrue();

        // verify
        BDDMockito.verify(channelRepository).findChannelByTypeAndParticipantIds(BDDMockito.any(Type.class), BDDMockito.anyList());
        BDDMockito.verify(channelRepository).save(BDDMockito.any(Channel.class));
        BDDMockito.verify(userRepository, BDDMockito.times(2)).findUserById(BDDMockito.anyString());
    }

    @Test
    void givenRequestWhenSendThenNotifyParticipantsAndReturnMessage() throws JsonProcessingException {
        // given
        String senderId = getRandomId();
        String senderName = "sender";
        String channelId = getRandomId();
        List<String> unreaderIds = List.of(getRandomId(), getRandomId());
        NewMessageRequest request = new NewMessageRequest(senderId, senderName, channelId, "content", unreaderIds);
        Channel channel = new Channel();
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());
        BDDMockito.given(messageRepository.save(BDDMockito.any(Message.class)))
            .willAnswer(invocation -> invocation.getArgument(0, Message.class));
        BDDMockito.given(channelRepository.findById(BDDMockito.anyString()))
            .willReturn(Optional.of(channel));
        BDDMockito.given(channelRepository.save(BDDMockito.any(Channel.class)))
            .willAnswer(invocation -> invocation.getArgument(0, Channel.class));
        BDDMockito.given(kafkaTemplate.send(BDDMockito.anyString(), BDDMockito.anyString()))
            .willReturn(future);
        
        // when
        MessageResponse messageResponse = chatService.send(request);

        // then
        Assertions.assertThat(messageResponse.getSenderId()).isEqualTo(senderId);

        // verify
        BDDMockito.verify(messageRepository).save(BDDMockito.any(Message.class));
        BDDMockito.verify(channelRepository).findById(BDDMockito.anyString());
        BDDMockito.verify(channelRepository).save(BDDMockito.any(Channel.class));
        BDDMockito.verify(kafkaTemplate).send(BDDMockito.anyString(), BDDMockito.anyString());
    }

    @Test
    void givenMessageIdAndUserIdWhenReadThenReturnMessage() {
        // given
        String messageId = getRandomId();
        String userId = getRandomId();
        String channelId = getRandomId();
        Message message = new Message(messageId, channelId, getRandomId(), "content", new ArrayList<>(List.of(userId, getRandomId(), getRandomId())), new Date());
        BDDMockito.given(messageRepository.findById(BDDMockito.anyString()))
            .willReturn(Optional.of(message));
        BDDMockito.given(messageRepository.save(BDDMockito.any(Message.class)))
            .willAnswer(invocation -> invocation.getArgument(0, Message.class));

        // when
        MessageResponse messageResponse = chatService.read(messageId, userId);

        // then
        Assertions.assertThat(messageResponse.getId()).isEqualTo(messageId);

        // verify
        BDDMockito.verify(messageRepository).findById(BDDMockito.anyString());
        BDDMockito.verify(messageRepository).save(BDDMockito.any(Message.class));
    }

    @Test
    void givenUserIdAndRequestWhenCreateNewGroupChannelThenReturnChannel() {
        // given
        String userId = getRandomId();
        NewGroupChannelRequest request = new NewGroupChannelRequest("something");
        BDDMockito.given(channelRepository.save(BDDMockito.any(Channel.class)))
            .willReturn(Channel.builder().name(request.getName()).participantIds(List.of(userId)).type(Type.GROUP).build());
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
            .willReturn(new UserResponse(userId, null, null, null, null, null));

        // when
        ChannelResponse channelResponse = chatService.createNewGroupChannel(userId, request);

        // then
        Assertions.assertThat(channelResponse.getParticipants()).isNotNull();
        Assertions.assertThat(channelResponse.getParticipants().stream()
            .map(UserResponse::getId)
            .anyMatch(participantId -> participantId.equals(userId))).isTrue();

        // verify
        BDDMockito.verify(channelRepository).save(BDDMockito.any(Channel.class));
        BDDMockito.verify(userRepository).findUserById(BDDMockito.anyString());
    }

    @Test
    void givenUserIdAndChannelIdWhenJoinThenNotfifyAndReturnChannel() throws JsonProcessingException {
        // given
        String userId = getRandomId();
        String channelId = getRandomId();
        Channel channel = Channel.builder()
            .participantIds(List.of(userId, getRandomId()))
            .type(Type.GROUP)
            .build();
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());
        BDDMockito.given(channelRepository.save(BDDMockito.any(Channel.class)))
            .willReturn(channel.leave(userId));
        BDDMockito.given(channelRepository.findById(BDDMockito.anyString()))
            .willReturn(Optional.of(channel));
        BDDMockito.given(kafkaTemplate.send(BDDMockito.anyString(), BDDMockito.anyString()))
                    .willReturn(future);
        BDDMockito.given(userRepository.findUserById(BDDMockito.anyString()))
            .willAnswer(invocation -> new UserResponse((String) invocation.getArguments()[0], null, null, null, null, null));

        // when
        ChannelResponse channelResponse = chatService.join(userId, channelId);

        // then
        Assertions.assertThat(channelResponse.getParticipants().stream().map(UserResponse::getId).anyMatch(participantId -> participantId.equals(userId)))
            .isTrue();
        

        // verify
        BDDMockito.verify(channelRepository).save(BDDMockito.any(Channel.class));
        BDDMockito.verify(channelRepository).findById(BDDMockito.anyString());
        BDDMockito.verify(kafkaTemplate).send(BDDMockito.anyString(), BDDMockito.anyString());
        BDDMockito.verify(userRepository, BDDMockito.times(2)).findUserById(BDDMockito.anyString());
    }
    
    @Test
    void givenUserIdAndChannelIdWhenLeaveThenNotfiyParticipants() throws JsonProcessingException {
        // given
        String userId = getRandomId();
        String channelId = getRandomId();
        Channel channel = Channel.builder()
            .participantIds(List.of(userId, getRandomId(), getRandomId()))
            .type(Type.GROUP)
            .build();
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());
        BDDMockito.given(channelRepository.save(BDDMockito.any(Channel.class)))
            .willReturn(channel.leave(userId));
        BDDMockito.given(channelRepository.findById(BDDMockito.anyString()))
            .willReturn(Optional.of(channel));
        BDDMockito.given(kafkaTemplate.send(BDDMockito.anyString(), BDDMockito.anyString()))
                    .willReturn(future);

        // when
        chatService.leave(userId, channelId);

        // then


        // verify
        BDDMockito.verify(kafkaTemplate).send(BDDMockito.anyString(), BDDMockito.anyString());
    }

    String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
