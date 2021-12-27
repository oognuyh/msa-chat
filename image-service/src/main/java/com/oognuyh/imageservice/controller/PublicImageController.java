package com.oognuyh.imageservice.controller;

import java.io.IOException;

import com.oognuyh.imageservice.service.ImageService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.minio.GetObjectResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class PublicImageController {
    private final ImageService imageService;
    
    @GetMapping("/avatars/{avatarId}")
    public ResponseEntity<byte[]> findAvatarByAvatarId(
        @PathVariable String avatarId
    ) throws IOException {
        GetObjectResponse avatar = imageService.findAvatarByAvatarId(avatarId);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, avatar.headers().get(HttpHeaders.CONTENT_TYPE))
            .body(avatar.readAllBytes());
    }
}
