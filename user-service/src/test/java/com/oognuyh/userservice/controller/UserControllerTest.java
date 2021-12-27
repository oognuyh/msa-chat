package com.oognuyh.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.userservice.config.KafkaTopicConfig;
import com.oognuyh.userservice.config.KeycloakConfig;
import com.oognuyh.userservice.config.KeycloakProperties;
import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
import com.oognuyh.userservice.payload.request.StatusUpdateRequest;
import com.oognuyh.userservice.payload.request.UserUpdateRequest;
import com.oognuyh.userservice.payload.response.UserResponse;
import com.oognuyh.userservice.service.UserService;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
public class UserControllerTest {
    
    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;

    @MockBean private KafkaTopicConfig kafkaTopicConfig;

    @MockBean private KeycloakConfig keycloakConfig;

    @MockBean private KeycloakProperties keycloakProperties;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenQueryTermWhenSearchThenReturnUsersContainingQueryTerm() throws Exception {
        // given
        String userId = getRandomId();
        String queryTerm = RandomString.make();
        List<UserResponse> userResponses = List.of(
            UserResponse.builder().email(queryTerm + "suffix").build(),
            UserResponse.builder().name("prefix" + queryTerm + "suffix").build()
        );
        BDDMockito.given(userService.findUsersByQuery(queryTerm))
            .willReturn(userResponses);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/users")
                .param("queryTerm", queryTerm)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(userResponses.size()));

        // verify
        BDDMockito.verify(userService).findUsersByQuery(queryTerm);
    }

    @Test
    void givenUserIdWhenFindUserThenReturnUser() throws Exception {
        // given
        String userId = getRandomId();
        UserResponse userResponse = UserResponse.builder()
            .id(userId)
            .name(RandomString.make())
            .build();
        BDDMockito.given(userService.findById(anyString()))
            .willReturn(userResponse);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/users/{}", userId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userResponse.getName()));

        // verify
        BDDMockito.verify(userService).findById(anyString());
    }

    @Test
    void givenUserIdAndRequestWhenUpdateDetailsThenReturnUpdatedUser() throws Exception {
        // given
        String userId = getRandomId();
        UserUpdateRequest request = UserUpdateRequest.builder()
            .email(RandomString.make())
            .firstName(RandomString.make(5))
            .lastName(RandomString.make(10))
            .build();
        BDDMockito.given(userService.updateDetails(anyString(), any(UserUpdateRequest.class)))
            .willReturn(UserResponse.builder()
                .id(userId)
                .email(request.getEmail()).name(request.getFirstName() + " " + request.getLastName())
                .build());

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.put("/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(request.getFirstName() + " " + request.getLastName()));

        // verify
        BDDMockito.verify(userService).updateDetails(anyString(), any(UserUpdateRequest.class));
    }

    @Test
    void givenUserIdAndRequestWhenUpdatePasswordThenReturnStatusOk() throws JsonProcessingException, Exception {
        // given
        String userId = getRandomId();
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .newPassword("newPassword")
            .passwordConfirmation("newPassword")
            .build();
        BDDMockito.doNothing().when(userService).updatePassword(anyString(), any(PasswordUpdateRequest.class));

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.put("/v1/users/{id}/password", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk());

        // verify
        BDDMockito.verify(userService).updatePassword(anyString(), any(PasswordUpdateRequest.class));
    }

    @Test
    void givenUserIdAndRequestWhenUpdateStatusThenReturnStatusOk() throws JsonProcessingException, Exception {
        // given
        String userId = getRandomId();
        StatusUpdateRequest request = StatusUpdateRequest.builder()
            .status("on")
            .build();
        BDDMockito.doNothing().when(userService).updateStatus(anyString(), any(StatusUpdateRequest.class));

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.put("/v1/users/{id}/status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk());

        // verify
        BDDMockito.verify(userService).updateStatus(anyString(), any(StatusUpdateRequest.class));
    }

    private String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
