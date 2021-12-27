package com.oognuyh.chatservice.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.oognuyh.chatservice.model.Channel;
import com.oognuyh.chatservice.model.Channel.Type;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
public class ChannelRepositoryTest {
    
    @Autowired private ChannelRepository channelRepository;
    

    @Test
    void givenTypeAndParticipantIdsWhenFindChannelThenReturnChannel() {
        // given
        List<String> participantIds = List.of(getRandomId(), getRandomId());
        Type type = Type.DIRECT;
        channelRepository.save(Channel.builder()
            .participantIds(participantIds)
            .type(type)
            .build()
        );
    
        // when
        Optional<Channel> channel = channelRepository.findChannelByTypeAndParticipantIds(type, participantIds);

        // then
        Assertions.assertThat(channel.isPresent()).isTrue();
        Assertions.assertThat(channel.get().getType()).isEqualTo(type);
        Assertions.assertThat(channel.get().getParticipantIds()).isEqualTo(participantIds);
    }

    @Test
    void givenTypeAndNameWhenFindChannelsThenReturnChannelsContainingName() {
        // given
        Type type = Type.GROUP;
        String nameToSearch = "something";
        channelRepository.saveAll(List.of(
            Channel.builder()
                .name(nameToSearch + "suffix")
                .type(type)
                .participantIds(List.of())
                .build(),
            Channel.builder()
                .name("prefix")
                .participantIds(List.of())
                .type(type)
                .build(),
            Channel.builder()
                .name("not be searched")
                .participantIds(List.of())
                .type(type)
                .build(),
            Channel.builder()
                .name(nameToSearch)
                .participantIds(List.of())
                .type(Type.DIRECT)
                .build()
        ));

        // when
        List<Channel> channels = channelRepository.findChannelsByTypeAndNameContainingIgnoreCase(type, nameToSearch);

        // then
        Assertions.assertThat(channels.size()).isGreaterThan(0);
        Assertions.assertThat(channels.stream().allMatch(channel -> channel.getName().contains(nameToSearch) && channel.getType().equals(type)))
            .isTrue();
    }

    @Test
    void givenParticipantIdWhenFindChannelsThenReturnChannelOrderedByLastMessageCreatedAt() {
        // given
        String participantId = getRandomId();
        channelRepository.saveAll(List.of(
            new Channel(null, null, List.of(participantId), "", new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), null),
            new Channel(null, null, List.of(participantId), "", new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), null),
            new Channel(null, null, List.of(participantId), "", new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000), null)
        ));

        // when
        List<Channel> channels = channelRepository.findChannelsByParticipantIdsOrderByLastMessageCreatedAtDesc(participantId);

        // then
        for (int i = 1; i < channels.size(); i++) {
            Assertions.assertThat(channels.get(i - 1).getLastMessageCreatedAt()).isAfter(channels.get(i).getLastMessageCreatedAt());
        }
    }

    @Test
    void givenParticipantIdWhenFindChannelsThenReturnParticipatingChannels() {
        // given
        String participantId = getRandomId();
        channelRepository.saveAll(List.of(
            Channel.builder()
                .type(Type.DIRECT)
                .participantIds(List.of(participantId, getRandomId()))
                .build(),
            Channel.builder()
                .participantIds(List.of(getRandomId(), getRandomId()))
                .type(Type.GROUP)
                .build(),
            Channel.builder()
                .participantIds(List.of(participantId, getRandomId(), getRandomId()))
                .type(Type.GROUP)
                .build()
        ));

        // when
        List<Channel> channels = channelRepository.findChannelsByParticipantIds(participantId);

        // then
        Assertions.assertThat(channels.size()).isGreaterThan(0);
        Assertions.assertThat(channels.stream().allMatch(channel -> channel.getParticipantIds().contains(participantId)))
            .isTrue();
    }

    String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
