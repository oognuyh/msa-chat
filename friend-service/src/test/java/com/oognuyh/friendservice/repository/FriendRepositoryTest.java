package com.oognuyh.friendservice.repository;

import com.oognuyh.friendservice.model.Friend;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class FriendRepositoryTest {

    @Autowired private FriendRepository friendRepository;

    @Test
    void givenUserIdWhenFindFriendsByUserIdThenReturnFriendsOfUser() {
        // given
        final String userId = UUID.randomUUID().toString();
        friendRepository.saveAll(List.of(
            Friend.builder().id(UUID.randomUUID().toString()).userId(userId).build(),
            Friend.builder().id(UUID.randomUUID().toString()).userId(userId).build(),
            Friend.builder().id(UUID.randomUUID().toString()).userId(UUID.randomUUID().toString()).build()
        ));

        // when
        List<Friend> friends = friendRepository.findFriendsByUserId(userId);

        // then
        Assertions.assertThat(friends.size()).isGreaterThan(0);
        Assertions.assertThat(friends.stream().allMatch(friend -> friend.getUserId().equals(userId))).isTrue();
    }

    @Test
    void givenIdWhenFindFriendsByIdThenReturnRelated() {
        // given
        final String id = UUID.randomUUID().toString();
        friendRepository.saveAll(List.of(
                Friend.builder().id(id).userId(UUID.randomUUID().toString()).build(),
                Friend.builder().id(id).userId(UUID.randomUUID().toString()).build(),
                Friend.builder().id(id).userId(UUID.randomUUID().toString()).build()
        ));

        // when
        List<Friend> friends = friendRepository.findFriendsById(id);

        // then
        Assertions.assertThat(friends.size()).isGreaterThan(0);
        Assertions.assertThat(friends.stream().allMatch(friend -> friend.getId().equals(id))).isTrue();
    }

    @Test
    void givenIdAndUserIdWhenDeleteFriendByIdAndUserIdThenFriendsOfUserIsZero() {
        // given
        final String id = UUID.randomUUID().toString();
        final String anotherId = UUID.randomUUID().toString();
        friendRepository.saveAll(List.of(
                Friend.builder().id(id).userId(anotherId).build(),
                Friend.builder().id(anotherId).userId(id).build()
        ));

        // when
        friendRepository.deleteFriendByIdAndUserId(id, anotherId);

        // then
        Assertions.assertThat(friendRepository.findFriendsByUserId(anotherId).size()).isZero();
    }
}