package com.oognuyh.imageservice.service;

import com.oognuyh.imageservice.payload.response.NewImageResponse;

import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectResponse;

public interface ImageService {
    
    GetObjectResponse findAvatarByUserId(String userId);
    NewImageResponse uploadCurrentUserAvatar(String currentUserId, MultipartFile avatar);
}
