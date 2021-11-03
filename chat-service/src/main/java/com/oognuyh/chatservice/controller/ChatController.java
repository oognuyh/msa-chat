package com.oognuyh.chatservice.controller;

import java.util.List;

import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;
import com.oognuyh.chatservice.service.ChatService;
import com.oognuyh.chatservice.util.annotation.CurrentUserId;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelResponse>> findChannelsByUserId(
        @CurrentUserId String currentUserId
    ) {
        return ResponseEntity.ok().body(chatService.findChannelsByUserId(currentUserId));
    }

    @GetMapping("/channels/{id}")
    public ResponseEntity<ChannelResponse> findChannelById(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) {
        return ResponseEntity.ok().body(chatService.findChannelById(id, currentUserId));
    }

    @GetMapping("/channels/between")
    public ResponseEntity<ChannelResponse> findChannelBetweenUserIds(
        @CurrentUserId String currentUserId,
        @RequestParam("userId") String userId
    ) {
        return ResponseEntity.ok().body(chatService.findChannelBetweenUserIds(currentUserId, userId));
    }

    @GetMapping("/channels/{id}/messages")
    public ResponseEntity<List<MessageResponse>> findMessagesByChannelId(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) {
        return ResponseEntity.ok().body(chatService.findMessagesByChannelId(id, currentUserId));
    }

    @PostMapping("/channels/{id}")
    public ResponseEntity<ChannelResponse> join(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) {
        return ResponseEntity.ok().body(chatService.join(currentUserId, id));
    }

    @GetMapping("/channels/{channelId}/messages/{messageId}")
    public ResponseEntity<MessageResponse> read(
        @PathVariable("channelId") String channelId,
        @PathVariable("messageId") String messageId,
        @CurrentUserId String currentUserId
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<MessageResponse> send(
        @RequestBody NewMessageRequest request
    ) {
        return ResponseEntity.ok().body(chatService.send(request));
    }

    @DeleteMapping("/channels/{id}")
    public ResponseEntity<Void> leave(
        @CurrentUserId String currentUserId,
        @PathVariable("id") String id
    ) {
        chatService.leave(currentUserId, id);

        return ResponseEntity.ok().build();
    }
}
 