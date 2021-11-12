package com.oognuyh.imageservice.service;

import com.oognuyh.imageservice.payload.response.NewImageResponse;

import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectResponse;

public interface ImageService {
    
    GetObjectResponse findAvatarById(String id);
    NewImageResponse uploadAvatarByUserId(String userId, MultipartFile avatar);
    void deleteAvatarById(String id);
}
