package com.oognuyh.userservice.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.userservice.config.KeycloakProperties;
import com.oognuyh.userservice.payload.event.AvatarChangedEvent;
import com.oognuyh.userservice.payload.event.UserChangedEvent;
import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
import com.oognuyh.userservice.payload.request.StatusUpdateRequest;
import com.oognuyh.userservice.payload.request.UserUpdateRequest;
import com.oognuyh.userservice.payload.response.UserResponse;
import com.oognuyh.userservice.service.UserService;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements UserService {
    private final KeycloakProperties keycloakProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Keycloak keycloak;

    @Value("${spring.kafka.template.user-changed-topic}")
    private String USER_CHANGED_TOPIC;
    

    private UsersResource getUsersResource() {
        return keycloak
            .realm(keycloakProperties.getRealm())
            .users();
    }

    @Override
    public List<UserResponse> findUsersByQuery(String queryTerm) {
        return getUsersResource().search(queryTerm, 0, null, false).stream()
            .map(UserResponse::of)
            .peek(user -> log.info("Successfully found user({}) whose email or name matches {}", user, queryTerm))
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
    public UserResponse updateDetails(String userId, UserUpdateRequest request) throws JsonProcessingException {
        UserResource userResource = getUsersResource().get(userId);    
        UserRepresentation userRepresentation = userResource.toRepresentation();

        // Set props & attrs
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.singleAttribute("statusMessage", request.getStatusMessage());

        // Update user
        userResource.update(userRepresentation);

        log.info("Successfully changed user({}) details", userId);

        kafkaTemplate.send(USER_CHANGED_TOPIC, objectMapper.writeValueAsString(new UserChangedEvent(userId)));

        log.info("Notify other services that user({}) changed", userId);

        // Return the updated
        return UserResponse.of(getUsersResource().get(userId).toRepresentation());
    }

    @Override
    public void updatePassword(String userId, PasswordUpdateRequest request) {
        UserResource userResource = getUsersResource().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        // Set new password
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(request.getNewPassword());
        
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        
        // Update user
        userResource.update(userRepresentation);

        log.info("Successfully changed user({}) password", userId);
    }

    @Override
    public void updateStatus(String userId, StatusUpdateRequest request) throws JsonProcessingException {
        UserResource userResource = getUsersResource().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        
        // Change user status 
        userRepresentation.singleAttribute("status", request.getStatus());

        userResource.update(userRepresentation);

        log.info("Successfully changed user({}) status", userId);
        
        kafkaTemplate.send(USER_CHANGED_TOPIC, objectMapper.writeValueAsString(new UserChangedEvent(userId)));

        log.info("Notify other services that user({}) changed", userId);
    }

    @KafkaListener(
        topics = "${spring.kafka.template.avatar-changed-topic}", 
        groupId = "${spring.kafka.consumer.avatar-changed-group-id}",
        containerFactory = "avatarChangedListenerFactory"
    )
    private void onAvatarChanged(@Payload String payload) throws JsonMappingException, JsonProcessingException {
        AvatarChangedEvent event = objectMapper.readValue(payload, AvatarChangedEvent.class);

        log.info("Receive avatar changed event with {} and {}", event.getUserId(), event.getImageUrl());

        UserResource userResource = getUsersResource().get(event.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        userRepresentation.singleAttribute("imageUrl", event.getImageUrl());

        userResource.update(userRepresentation);

        log.info("Successfully changed user image url to {}", event.getImageUrl());
    }
}
