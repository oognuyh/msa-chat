package com.oognuyh.userservice.controller;

import java.util.List;

import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findUsersByQuery(
        @RequestParam(name = "queryTerm", required = false) String queryTerm
    ) {
        return ResponseEntity.ok(userService.findUsersByQuery(queryTerm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUserById(
        @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateDetails(
        @CurrentUserId String currentUserId,
        @PathVariable String id, 
        @RequestBody UserUpdateRequest request
    ) {
        if (!currentUserId.equals(id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(userService.updateDetails(id, request));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
        @CurrentUserId String currentUserId,
        @PathVariable String id,
        @RequestBody PasswordUpdateRequest request
    ) {
        if (!currentUserId.equals(id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        userService.updatePassword(id, request);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
        @CurrentUserId String currentUserId,
        @PathVariable String id
    ) {
        if (!currentUserId.equals(id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        userService.updateStatus(id);

        return ResponseEntity.ok().build();
    }
}
