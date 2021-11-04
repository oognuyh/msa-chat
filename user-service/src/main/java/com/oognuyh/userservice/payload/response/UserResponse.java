package com.oognuyh.userservice.payload.response;

import java.util.Collections;

import org.keycloak.representations.idm.UserRepresentation;

import lombok.Data;

@Data
public class UserResponse {
    
    private String id;
    
    private String name;

    private String email;

    private String imageUrl;

    private String statusMessage;

    private Boolean isActive;

    private UserResponse(String id, String name, String email, String imageUrl, String statusMessage, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.statusMessage = statusMessage;
        this.isActive = isActive;
    }

    public static UserResponse of(UserRepresentation userRepresentation) {
        return new UserResponse(
            userRepresentation.getId(), 
            userRepresentation.getFirstName() + " " + userRepresentation.getLastName(),
            userRepresentation.getEmail(),
            userRepresentation.getAttributes() == null ? 
                "" :
                userRepresentation.getAttributes().getOrDefault("imageUrl", Collections.singletonList("")).get(0),
            userRepresentation.getAttributes() == null ?
                "" :
                userRepresentation.getAttributes().getOrDefault("statusMessage", Collections.singletonList("")).get(0),
            userRepresentation.getAttributes() == null ?
                false :
                userRepresentation.getAttributes().getOrDefault("status", Collections.singletonList("off")).get(0).equals("on")
        );
    }
}