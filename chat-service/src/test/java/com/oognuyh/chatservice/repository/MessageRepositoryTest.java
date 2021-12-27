package com.oognuyh.chatservice.repository;

import java.util.List;
import java.util.UUID;

import com.oognuyh.chatservice.model.Message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import net.bytebuddy.utility.RandomString;

@DataMongoTest
public class MessageRepositoryTest {
    
    @Autowired private MessageRepository messageRepository;

    private String channelId = getRandomId();

    private String userId = getRandomId();

    private List<Message> messages = List.of(
        Message.of(channelId, getRandomId(), RandomString.make(), List.of(getRandomId())),
        Message.of(channelId, getRandomId(), RandomString.make(), List.of(getRandomId(), getRandomId())),
        Message.of(channelId, getRandomId(), RandomString.make(), List.of(userId, getRandomId())),
        Message.of(getRandomId(), getRandomId(), RandomString.make(), List.of(userId))
    );

    @BeforeEach
    void init() {
        messageRepository.saveAll(messages);
    }

    @Test
    void givenChannelIdWhenFindMessagesThenReturnMessages() {
        // given
        
        
        // when
        List<Message> messages = messageRepository.findMessagesByChannelId(this.channelId);
        
        // then
        Assertions.assertThat(messages.size())
            .isEqualTo(this.messages.stream()
                .filter(message -> message.getChannelId().equals(this.channelId))
                .count());
    }

    @Test
    void givenChannelIdAndUserIdWhenFindMessagesThenReturnUnreadMessages() {
        // given

        
        // when
        List<Message> messages = messageRepository.findMessagesByChannelIdAndUnreaderIds(this.channelId, this.userId);
        
        // then
        Assertions.assertThat(messages.size())
            .isEqualTo(this.messages.stream()
                .filter(message -> message.getChannelId().equals(this.channelId) && message.getUnreaderIds().contains(this.userId))
                .count());
    }

    @Test
    void givenChannelIdAndUserIdWhenCountMessagesThenReturnNumOfUnreadMessages() {
        // given

        
        // when
        int numOfUnreadMessages = messageRepository.countMessagesByChannelIdAndUnreaderIds(this.channelId, this.userId);
        
        // then
        Assertions.assertThat(numOfUnreadMessages)
            .isEqualTo(this.messages.stream()
                .filter(message -> message.getChannelId().equals(this.channelId) && message.getUnreaderIds().contains(this.userId))
                .count());
    }

    private String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
