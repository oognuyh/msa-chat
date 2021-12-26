package com.oognuyh.friendservice.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.friendservice.payload.request.AddingNewFriendRequest;
import com.oognuyh.friendservice.payload.response.FriendResponse;
import com.oognuyh.friendservice.service.FriendService;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FriendControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private FriendService friendService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenUserIdWhenFindFriendsByUserIdThenReturnFriendsOfUser() throws Exception {
        // given
        BDDMockito.given(friendService.findFriendsByUserId(BDDMockito.anyString()))
                        .willReturn(List.of(
                                FriendResponse.builder()
                                        .id("id")
                                        .userId("userId")
                                        .email("friend@email.com")
                                        .name("friend")
                                        .statusMessage("status message")
                                        .build())
                        );

        // when
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/v1/friends")
                        .with(jwt().jwt(jwt -> jwt.subject("userId"))))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("id"));

        // verify
        BDDMockito.verify(friendService).findFriendsByUserId(BDDMockito.anyString());
    }

    @Test
    void givenUserIdAndRequestWhenAddNewFriendThenReturnNewFriend() throws Exception {
        // given
        AddingNewFriendRequest request = new AddingNewFriendRequest("id");
        BDDMockito.given(friendService.addNewFriend(BDDMockito.anyString(), BDDMockito.any(AddingNewFriendRequest.class)))
                .willReturn(FriendResponse.builder().userId("userId").id("id").build());

        // when
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.post("/v1/friends")
                        .with(jwt().jwt(jwt -> jwt.subject("userId")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("userId"));

        // verify
        BDDMockito.verify(friendService).addNewFriend(BDDMockito.anyString(), BDDMockito.any(AddingNewFriendRequest.class));
    }

    @Test
    void givenIdAndUserIdWhenDeleteFriendByIdAndUserIdThenReturnStatusOk() throws Exception {
        // given
        Mockito.doNothing().when(friendService).deleteFriendByIdAndUserId(Mockito.anyString(), Mockito.anyString());

        // when
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.delete("/v1/friends/id")
                        .with(jwt().jwt(jwt -> jwt.subject("userId"))))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        // verify
        Mockito.verify(friendService).deleteFriendByIdAndUserId(Mockito.anyString(), Mockito.anyString());
    }
}
