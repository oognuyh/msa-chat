package com.oognuyh.chatservice.payload.response;

import com.oognuyh.chatservice.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    
    private String id;

    private String name;

    private String email;

    private String imageUrl;

    private Boolean isActive;

    private String statusMessage;

    public static UserResponse of(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .imageUrl(user.getImageUrl())
            .isActive(user.getIsActive())
            .statusMessage(user.getStatusMessage())
            .build();
    }
}