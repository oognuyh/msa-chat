package com.oognuyh.userservice.payload.response;

import java.util.Collections;

import org.keycloak.representations.idm.UserRepresentation;

import lombok.Data;

@Data
public class UserResponse {
    
    private String id;
    
    private String fullName;

    private String imageUrl;

    private Boolean isActive;

    private UserResponse(String id, String fullName, String imageUrl, Boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    public static UserResponse of(UserRepresentation userRepresentation) {
        return new UserResponse(
            userRepresentation.getId(), 
            userRepresentation.getFirstName() + " " + userRepresentation.getLastName(), 
            userRepresentation.getAttributes() == null ? 
                "" :
                userRepresentation.getAttributes().getOrDefault("imageUrl", Collections.singletonList("")).get(0),
            userRepresentation.getAttributes() == null ?
                false :
                userRepresentation.getAttributes().getOrDefault("status", Collections.singletonList("off")).get(0).equals("on")
        );
    }
}