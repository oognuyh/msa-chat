package com.oognuyh.imageservice.service.impl;

import java.io.InputStream;

import com.oognuyh.imageservice.payload.response.NewImageResponse;
import com.oognuyh.imageservice.service.ImageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public GetObjectResponse findAvatarByUserId(String userId) {
        try {
            GetObjectResponse avatar = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object("avatars/" + userId)
                .build());

            log.info("{}", avatar.headers());

            return avatar;
        } catch (Exception e) {
            log.error("Failed to get file: {}", e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public NewImageResponse uploadCurrentUserAvatar(String currentUserId, MultipartFile avatar) {
        try {
            InputStream avatarInputStream = avatar.getInputStream();

            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object("avatars/" + currentUserId)
                .stream(avatarInputStream, avatarInputStream.available(), -1)
                .contentType(avatar.getContentType())
                .build()
            );

            return NewImageResponse.builder()
                .imageUrl("/avatars/" + currentUserId)
                .build();
        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
