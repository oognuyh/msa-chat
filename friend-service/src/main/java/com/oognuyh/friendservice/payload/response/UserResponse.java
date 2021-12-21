package com.oognuyh.friendservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;
    
    private String name;

    private String email;

    private String imageUrl;

    private Boolean isActive;

    private String statusMessage;

    public static UserResponse anonymous() {
        return new UserResponse(null, "anonymous", null, null, null, "Failed to load user details");
    }
}
