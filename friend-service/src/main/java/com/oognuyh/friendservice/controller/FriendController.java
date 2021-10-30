package com.oognuyh.friendservice.controller;

import java.util.List;

import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;
import com.oognuyh.friendservice.service.FriendService;
import com.oognuyh.friendservice.util.annotation.CurrentUserId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendResponse>> findFriendsByUserId(
        @CurrentUserId String currentUserId
    ) {
        return ResponseEntity.ok().body(friendService.findFriendsByUserId(currentUserId));
    }

    @PostMapping
    public ResponseEntity<FriendResponse> addNewFriend(
        @CurrentUserId String currentUserId,
        AddingNewFriendRequest request
    ) {
        if (!currentUserId.equals(request.getUserId())) 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        
        return ResponseEntity.ok().body(friendService.addNewFriend(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendByIdAndUserId(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) {
        friendService.deleteFriendByIdAndUserId(id, currentUserId);

        return ResponseEntity.ok().build();
    }
}