package com.oognuyh.imageservice.service;

import com.oognuyh.imageservice.payload.response.NewImageResponse;

import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectResponse;

public interface ImageService {
    
    GetObjectResponse findAvatarByAvatarId(String avatarId);
    NewImageResponse uploadAvatarByUserId(String userId, MultipartFile avatar);
    void deleteAvatarByAvatarId(String userId, String avatarId);
}
