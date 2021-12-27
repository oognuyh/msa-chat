package com.oognuyh.imageservice.controller;

import static org.mockito.ArgumentMatchers.anyString;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.oognuyh.imageservice.config.KafkaProducerConfig;
import com.oognuyh.imageservice.config.KafkaTopicConfig;
import com.oognuyh.imageservice.config.MinioConfig;
import com.oognuyh.imageservice.service.ImageService;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.minio.GetObjectResponse;
import okhttp3.Headers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PublicImageControllerTest {
    
    @Autowired private MockMvc mockMvc;

    @MockBean private ImageService imageService;

    @MockBean private MinioConfig minioConfig;

    @MockBean private KafkaProducerConfig kafkaProducerConfig;

    @MockBean private KafkaTopicConfig kafkaTopicConfig;

    @Test
    void givenAvatarIdWhenFindAvatarThenReturnAvatar() throws Exception {
        // given
        String avatarId = getRandomId();
        BDDMockito.given(imageService.findAvatarByAvatarId(anyString()))
            .willReturn(new GetObjectResponse(
                Headers.of(HttpHeaders.CONTENT_TYPE, "image/png"),
                "test",
                "test",
                "test",
                new ByteArrayInputStream(RandomString.make(30).getBytes(StandardCharsets.UTF_8))    
            ));

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/images/avatars/{}", avatarId))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.CONTENT_TYPE))
            .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "image/png"));

        // verify
        BDDMockito.verify(imageService).findAvatarByAvatarId(anyString());
    }

    private String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
