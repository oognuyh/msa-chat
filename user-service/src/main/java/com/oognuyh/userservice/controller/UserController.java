package com.oognuyh.userservice.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
import com.oognuyh.userservice.payload.request.StatusUpdateRequest;
import com.oognuyh.userservice.payload.request.UserUpdateRequest;
import com.oognuyh.userservice.payload.response.UserResponse;
import com.oognuyh.userservice.service.UserService;
import com.oognuyh.userservice.util.annotation.CurrentUserId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findUsersByQuery(
        @RequestParam(name = "queryTerm", required = false) String queryTerm
    ) {
        log.info("Find users with queryTerm({})", queryTerm);

        return new ResponseEntity<>(userService.findUsersByQuery(queryTerm), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUserById(
        @PathVariable String id
    ) {
        log.info("Find user with id {}", id);

        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateDetails(
        @CurrentUserId String currentUserId,
        @PathVariable String id, 
        @RequestBody UserUpdateRequest request
    ) throws JsonProcessingException {
        if (!currentUserId.equals(id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        log.info("Update user({}) details with {}", id, request);

        return new ResponseEntity<>(userService.updateDetails(id, request), HttpStatus.OK);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
        @CurrentUserId String currentUserId,
        @PathVariable String id,
        @RequestBody PasswordUpdateRequest request
    ) {
        if (!currentUserId.equals(id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        log.info("Update user({}) password", id);

        userService.updatePassword(id, request);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
        @CurrentUserId String currentUserId,
        @PathVariable String id,
        @RequestBody StatusUpdateRequest request
    ) throws JsonProcessingException {
        if (!currentUserId.equals(id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        log.info("Update user({}) status to {}", id, request.getStatus());

        userService.updateStatus(id, request);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
