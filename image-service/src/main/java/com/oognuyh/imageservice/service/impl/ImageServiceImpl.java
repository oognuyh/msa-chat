package com.oognuyh.imageservice.service.impl;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.imageservice.payload.event.AvatarChangedEvent;
import com.oognuyh.imageservice.payload.response.NewImageResponse;
import com.oognuyh.imageservice.service.ImageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final MinioClient minioClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.template.avatar-changed-topic}")
    private String AVATAR_CHANGED_TOPIC;

    @Value("${minio.bucket}")
    private String BUCKET;

    @Value("${eureka.gateway-uri}")
    private String GATEWAY_URI;

    @Override
    public GetObjectResponse findAvatarByUserId(String userId) {
        try {
            GetObjectResponse avatar = minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET)
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
    public NewImageResponse uploadAvatarByUserId(String userId, MultipartFile avatar) {
        try {
            InputStream avatarInputStream = avatar.getInputStream();

            minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET)
                .object("avatars/" + userId)
                .stream(avatarInputStream, avatarInputStream.available(), -1)
                .contentType(avatar.getContentType())
                .build()
            );

            kafkaTemplate.send(
                AVATAR_CHANGED_TOPIC, 
                objectMapper.writeValueAsString(
                    AvatarChangedEvent.builder()
                        .userId(userId)
                        .imageUrl(GATEWAY_URI + "/images/avatars/" + userId)
                        .build()
                )
            );

            return NewImageResponse.builder()
                .imageUrl(GATEWAY_URI + "/images/avatars/" + userId)
                .build();
        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteAvatarByUserId(String userId) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET)
                .object(userId)
                .build()
            );

            kafkaTemplate.send(
                AVATAR_CHANGED_TOPIC, 
                objectMapper.writeValueAsString(
                    AvatarChangedEvent.builder()
                        .userId(userId)
                        .imageUrl("")
                        .build()
                )
            );
        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
