package com.oognuyh.imageservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.UUID;

import com.oognuyh.imageservice.config.KafkaProducerConfig;
import com.oognuyh.imageservice.config.KafkaTopicConfig;
import com.oognuyh.imageservice.config.MinioConfig;
import com.oognuyh.imageservice.payload.response.NewImageResponse;
import com.oognuyh.imageservice.service.ImageService;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ImageControllerTest {
    
    @Autowired private MockMvc mockMvc;

    @MockBean private ImageService imageService;

    @MockBean private MinioConfig minioConfig;

    @MockBean private KafkaProducerConfig kafkaProducerConfig;

    @MockBean private KafkaTopicConfig kafkaTopicConfig;

    @Test
    void givenUserIdAndAvatarWhenUploadThenReturnImageUrl() throws Exception {
        // given
        String userId = getRandomId();
        MockMultipartFile avatar = new MockMultipartFile(RandomString.make(), RandomString.make(), "image/png", RandomString.make().getBytes());
        BDDMockito.given(imageService.uploadAvatarByUserId(anyString(), any()))
            .willReturn(NewImageResponse.builder().imageUrl(RandomString.make(15)).build());
        
        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.multipart("/v1/images/avatars")
                .file("avatar", avatar.getBytes())
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then 
        resultActions
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").isNotEmpty());

        // verify
        BDDMockito.verify(imageService).uploadAvatarByUserId(anyString(), any());
    }

    @Test
    void givenUserIdAndAvatarIdWhenDeleteThenReturnStatusIsOk() throws Exception {
        // given
        String userId = getRandomId();
        String avatarId = getRandomId();
        BDDMockito.doNothing().when(imageService).deleteAvatarByAvatarId(anyString(), anyString());

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.delete("/v1/images/avatars/{}", avatarId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk());

        // verify
        BDDMockito.verify(imageService).deleteAvatarByAvatarId(anyString(), anyString());
    }

    private String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
