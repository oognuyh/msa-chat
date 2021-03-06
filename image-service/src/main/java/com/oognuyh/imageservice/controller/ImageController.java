package com.oognuyh.imageservice.controller;

import com.oognuyh.imageservice.payload.response.NewImageResponse;
import com.oognuyh.imageservice.service.ImageService;
import com.oognuyh.imageservice.util.annotation.CurrentUserId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/avatars")
    public ResponseEntity<NewImageResponse> uploadCurrentUserAvatar(
        @CurrentUserId String currentUserId,
        @RequestParam MultipartFile avatar
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.uploadAvatarByUserId(currentUserId, avatar));
    }

    @DeleteMapping("/avatars/{avatarId}")
    public ResponseEntity<Void> deleteAvatarByAvatarId(
        @CurrentUserId String currentUserId,
        @PathVariable String avatarId
    ) { 
        imageService.deleteAvatarByAvatarId(currentUserId, avatarId);

        return ResponseEntity.ok().build();
    }
}
