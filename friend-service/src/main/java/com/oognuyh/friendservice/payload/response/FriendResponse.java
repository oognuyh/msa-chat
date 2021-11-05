package com.oognuyh.friendservice.payload.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oognuyh.friendservice.model.Friend;

import lombok.Data;

@Data
public class FriendResponse {

    private String id;

    private String name;

    private String imageUrl;

    @JsonProperty("isActive")
    private Boolean isActive;

    private String userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date createdAt;

    private FriendResponse(String id, String userId, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static FriendResponse of(Friend friend) {
        return new FriendResponse(
            friend.getId(),
            friend.getUserId(),
            friend.getCreatedAt()
        );
    }

    public FriendResponse setDetails(UserResponse userResponse) {
        this.name = userResponse.getName();
        this.imageUrl = userResponse.getImageUrl();
        this.isActive = userResponse.getIsActive();

        return this;
    }
}
