package com.oognuyh.imageservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.UUID;

import com.oognuyh.imageservice.payload.response.NewImageResponse;
import com.oognuyh.imageservice.service.impl.ImageServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.SettableListenableFuture;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import net.bytebuddy.utility.RandomString;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    
    @InjectMocks private ImageServiceImpl imageService;

    @Mock private KafkaTemplate<String, String> kafkaTemplate;

    @Mock private MinioClient minioClient;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(imageService, "BUCKET", "test");
        ReflectionTestUtils.setField(imageService, "GATEWAY_URI", "http://test.org");
        ReflectionTestUtils.setField(imageService, "AVATAR_CHANGED_TOPIC", "avatar-changed-topic");
    }

    @Test
    void givenAvatarIdWhenFindAvatarThenReturnAvatar() throws Exception {
        // given
        String avatarId = getRandomId();
        BDDMockito.given(minioClient.getObject(any(GetObjectArgs.class)))
            .willAnswer(i -> new GetObjectResponse(
                null,
                i.getArgument(0, GetObjectArgs.class).bucket(),
                i.getArgument(0, GetObjectArgs.class).region(),
                i.getArgument(0, GetObjectArgs.class).object(),
                null
            ));

        // when
        GetObjectResponse response = imageService.findAvatarByAvatarId(avatarId);

        // then
        Assertions.assertThat(response.object()).contains(avatarId);

        // verify
        BDDMockito.verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void givenUserIdAndAvatarWhenUploadThenNotifyAndReturnNewImage() throws Exception {
        // given
        String userId = getRandomId();
        MockMultipartFile avatar = new MockMultipartFile(RandomString.make(), RandomString.make(), "image/jpeg", "test".getBytes());
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());
        BDDMockito.given(minioClient.putObject(any(PutObjectArgs.class)))
            .willReturn(null);
        BDDMockito.given(kafkaTemplate.send(anyString(), anyString()))
            .willReturn(future);

        // when
        NewImageResponse response = imageService.uploadAvatarByUserId(userId, avatar);

        // then
        Assertions.assertThat(response.getImageUrl()).isNotBlank();

        // verify
        BDDMockito.verify(minioClient).putObject(any(PutObjectArgs.class));
        BDDMockito.verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void givenUserIdAndAvatarIdWhenDeleteThenDeleteAndNotify() throws Exception {
        // given
        String userId = getRandomId();
        String avatarId = getRandomId();
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException());
        BDDMockito.given(kafkaTemplate.send(anyString(), anyString()))
            .willReturn(future);
        BDDMockito.doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // when
        imageService.deleteAvatarByAvatarId(userId, avatarId);

        // then


        // verify
        BDDMockito.verify(kafkaTemplate).send(anyString(), anyString());
        BDDMockito.verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    private String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
