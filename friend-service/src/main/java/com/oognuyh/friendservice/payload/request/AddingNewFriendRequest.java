package com.oognuyh.friendservice.payload.request;

import lombok.Data;

@Data
public class AddingNewFriendRequest {

    private String userId;
    private String friendId;
}
