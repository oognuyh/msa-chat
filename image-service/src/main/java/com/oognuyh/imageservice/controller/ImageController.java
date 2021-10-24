package com.oognuyh.imageservice.controller;

import java.io.IOException;

import com.oognuyh.imageservice.payload.response.NewImageResponse;
import com.oognuyh.imageservice.service.ImageService;
import com.oognuyh.imageservice.util.annotation.CurrentUserId;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/avatars/{userId}")
    public ResponseEntity<byte[]> findAvatarByUserId(
        @PathVariable("userId") String userId
    ) throws IOException {
        GetObjectResponse avatar = imageService.findAvatarByUserId(userId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, avatar.headers().get(HttpHeaders.CONTENT_TYPE))
            .body(avatar.readAllBytes());
    }

    @PostMapping("/avatars")
    public ResponseEntity<NewImageResponse> uploadCurrentUserAvatar(
        @CurrentUserId String currentUserId,
        @RequestParam MultipartFile avatar
    ) {
        return ResponseEntity.ok(imageService.uploadCurrentUserAvatar(currentUserId, avatar));
    }
}
