package com.oognuyh.friendservice.service;

import java.util.List;

import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;

public interface FriendService {

    List<FriendResponse> findFriendsByUserId(String userId);
    FriendResponse addNewFriend(String userId, AddingNewFriendRequest request);
    void deleteFriendByIdAndUserId(String id, String userId);
}
