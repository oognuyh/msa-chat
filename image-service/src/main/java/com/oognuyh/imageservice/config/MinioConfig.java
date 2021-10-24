package com.oognuyh.imageservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Setter;

@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = new MinioClient.Builder()
                .endpoint(endpoint) 
                .credentials(accessKey, secretKey) 
                .build();

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build()
                );
            }

            return minioClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
