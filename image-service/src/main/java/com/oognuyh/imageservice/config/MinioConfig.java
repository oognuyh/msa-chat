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
    private String ENDPOINT;
    private String ACCESS_KEY;
    private String SECRET_KEY;
    private String BUCKET;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = new MinioClient.Builder()
                .endpoint(ENDPOINT) 
                .credentials(ACCESS_KEY, SECRET_KEY) 
                .build();

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(BUCKET)
                    .build()
                );
            }

            return minioClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
