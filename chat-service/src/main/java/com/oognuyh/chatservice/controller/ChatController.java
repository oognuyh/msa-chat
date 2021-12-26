package com.oognuyh.chatservice.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oognuyh.chatservice.payload.request.NewGroupChannelRequest;
import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;
import com.oognuyh.chatservice.service.ChatService;
import com.oognuyh.chatservice.util.annotation.CurrentUserId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/channels")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findChannelsByUserId(
        @CurrentUserId String currentUserId
    ) {
        log.info("Find channels of user({}) ", currentUserId);

        return ResponseEntity.ok().body(chatService.findChannelsByUserId(currentUserId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChannelResponse>> search(
        @RequestParam(defaultValue = "", required = false) String queryTerm
    ) {
        log.info("Search channels by {}", queryTerm);        

        return ResponseEntity.ok().body(chatService.search(queryTerm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelResponse> findChannelById(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) {
        log.info("Find channel({}) of user({})", id, currentUserId);

        return ResponseEntity.ok().body(chatService.findChannelById(id, currentUserId));
    }

    @GetMapping("/between")
    public ResponseEntity<ChannelResponse> findChannelBetweenUserIds(
        @CurrentUserId String currentUserId,
        @RequestParam("userId") String userId
    ) {
        log.info("Find channel between Users({}, {})", currentUserId, userId);

        return ResponseEntity.ok().body(chatService.findChannelBetweenUserIds(currentUserId, userId));
    }

    @PostMapping
    public ResponseEntity<ChannelResponse> createGroupChannel(
        @CurrentUserId String currentUserId,
        @RequestBody NewGroupChannelRequest request
    ) {
        log.info("Create new group channel with name ({})", request.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createNewGroupChannel(currentUserId, request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ChannelResponse> join(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) throws JsonProcessingException {
        log.info("User({}) joins channel({})", currentUserId, id);

        return ResponseEntity.ok().body(chatService.join(currentUserId, id));
    }

    @GetMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<MessageResponse> read(
        @PathVariable("channelId") String channelId,
        @PathVariable("messageId") String messageId,
        @CurrentUserId String currentUserId
    ) {
        log.info("User({}) reads message({}) in channel(channelId)", currentUserId, messageId, channelId);

        return ResponseEntity.ok().body(chatService.read(messageId, currentUserId));
    }

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<MessageResponse> send(
        @RequestBody NewMessageRequest request,
        @PathVariable String channelId
    ) throws JsonProcessingException {
        log.info("Send new message({}) to channel({})", request, channelId);

        return ResponseEntity.ok().body(chatService.send(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> leave(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) throws JsonProcessingException {
        log.info("User({}) leaves channel({})", currentUserId, id);

        chatService.leave(currentUserId, id);

        return ResponseEntity.ok().build();
    }
}
 