package com.oognuyh.userservice.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.oognuyh.userservice.config.KeycloakProperties;
import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
import com.oognuyh.userservice.payload.request.UserUpdateRequest;
import com.oognuyh.userservice.payload.response.UserResponse;
import com.oognuyh.userservice.service.UserService;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements UserService {
    private final KeycloakProperties keycloakProperties;
    private final Keycloak keycloak;

    private UsersResource getUsersResource() {
        return keycloak
            .realm(keycloakProperties.getRealm())
            .users();
    }

    @Override
    public List<UserResponse> findAll() {
        return getUsersResource().list()
            .stream()
            .peek(System.out::println)
            .map(UserResponse::of)
            .collect(Collectors.toList());
    }

    @Override
    public UserResponse findById(String id) {
        try {
            return UserResponse.of(getUsersResource().get(id).toRepresentation());
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user with " + id);
        } catch (Exception e) {
            log.error("{}", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserResponse updateInfo(String id, UserUpdateRequest request) {
        UserResource userResource = getUsersResource().get(id);    
        UserRepresentation userRepresentation = userResource.toRepresentation();

        // Set props & attrs
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.singleAttribute("imageUrl", request.getImageUrl());

        // Update user
        userResource.update(userRepresentation);

        // Return the updated
        return UserResponse.of(getUsersResource().get(id).toRepresentation());
    }

    @Override
    public void updatePassword(String id, PasswordUpdateRequest request) {
        UserResource userResource = getUsersResource().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        // Set new password
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(request.getNewPassword());
        
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        
        // Update user
        userResource.update(userRepresentation);
    }

    @Override
    public void updateStatus(String id) {
        UserResource userResource = getUsersResource().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        
        String previousStatus = "off";
        if (userRepresentation.getAttributes() != null) {
            previousStatus = userRepresentation.getAttributes().getOrDefault("status", Collections.singletonList("off")).get(0);
        }

        // Change user status 
        userRepresentation.singleAttribute("status", previousStatus.equals("off") ? "on" : "off");

        userResource.update(userRepresentation);
    }
}
