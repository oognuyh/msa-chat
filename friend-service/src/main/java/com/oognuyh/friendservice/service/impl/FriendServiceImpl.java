package com.oognuyh.friendservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.oognuyh.friendservice.model.Friend;
import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;
import com.oognuyh.friendservice.repository.FriendRepository;
import com.oognuyh.friendservice.repository.UserRepository;
import com.oognuyh.friendservice.service.FriendService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public List<FriendResponse> findFriendsByUserId(String userId) {
        return friendRepository.findFriendsByUserId(userId).stream()
            .map(FriendResponse::of)
            .map(friend -> friend.setDetails(userRepository.findUserById(friend.getId())))
            .collect(Collectors.toList());
    }

    public FriendResponse addNewFriend(AddingNewFriendRequest request) {
        return FriendResponse.of(
                friendRepository.save(Friend.builder()
                    .id(request.getFriendId())
                    .userId(request.getUserId())
                    .build()))
            .setDetails(userRepository.findUserById(request.getFriendId()));
    }

    public void deleteFriendByIdAndUserId(String id, String userId) {
        friendRepository.deleteFriendByIdAndUserId(id, userId);
    }
}
