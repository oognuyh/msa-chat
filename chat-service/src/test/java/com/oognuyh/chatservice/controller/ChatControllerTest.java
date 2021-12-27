package com.oognuyh.chatservice.controller;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oognuyh.chatservice.model.Channel.Type;
import com.oognuyh.chatservice.payload.request.NewGroupChannelRequest;
import com.oognuyh.chatservice.payload.request.NewMessageRequest;
import com.oognuyh.chatservice.payload.response.ChannelResponse;
import com.oognuyh.chatservice.payload.response.MessageResponse;
import com.oognuyh.chatservice.payload.response.UserResponse;
import com.oognuyh.chatservice.service.ChatService;

import org.hamcrest.Matchers;
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

import net.bytebuddy.utility.RandomString;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChatControllerTest {
    
    @Autowired private MockMvc mockMvc;

    @MockBean private ChatService chatService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenUserIdWhenFindChannelsThenReturnChannels() throws Exception {
        // given
        String userId = getRandomId();
        List<ChannelResponse> channelResponses = List.of(
            ChannelResponse.builder().participants(List.of(getUser(userId), getUser())).build(),
            ChannelResponse.builder().participants(List.of(getUser(userId), getUser())).build(),
            ChannelResponse.builder().participants(List.of(getUser(userId), getUser())).build()
        );
        BDDMockito.given(chatService.findChannelsByUserId(BDDMockito.anyString()))
            .willReturn(channelResponses);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/channels")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(channelResponses.size()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.*.participants[?(@.id == '" + userId + "')]", Matchers.hasSize(channelResponses.size())));
    }

    @Test
    void givenQueryTermWhenSearchThenReturnChannelsContainingQueryTerm() throws Exception {
        // given
        String userId = getRandomId();
        String queryTerm = RandomString.make(4);
        List<ChannelResponse> channelResponses = List.of(
            ChannelResponse.builder().name(queryTerm + "suffix").build(),
            ChannelResponse.builder().name("prefix" + queryTerm).build(),
            ChannelResponse.builder().name(queryTerm).build()
        );
        BDDMockito.given(chatService.search(BDDMockito.anyString()))
            .willReturn(channelResponses);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/channels/search")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId)))
                .queryParam("queryTerm", queryTerm))
            .andDo(MockMvcResultHandlers.print());
        
        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(channelResponses.size()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.name =~ /.*" + queryTerm + ".*/)].name", Matchers.hasSize(channelResponses.size())));

        // verify
        BDDMockito.verify(chatService).search(BDDMockito.anyString());
    }

    @Test
    void givenUserIdAndChannelIdWhenFindChannelThenReturnChannel() throws Exception {
        // given
        String userId = getRandomId();
        String channelId = getRandomId();
        ChannelResponse channelResponse = ChannelResponse.builder().id(channelId).participants(List.of(getUser(userId), getUser())).build();
        BDDMockito.given(chatService.findChannelById(BDDMockito.anyString(), BDDMockito.anyString()))
            .willReturn(channelResponse);
    
        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/channels/{}", channelId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());
        
        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(channelId)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.participants[?(@.id == '" + userId + "')]", Matchers.hasSize(1)));

        // verify
        BDDMockito.verify(chatService).findChannelById(BDDMockito.anyString(), BDDMockito.anyString());
    }

    @Test
    void givenUserIdsWhenFindChannelThenReturnDirectChannel() throws Exception {
        // given
        String userId = getRandomId();
        String anotherId = getRandomId();
        String channelId = getRandomId();
        List<UserResponse> participants = List.of(getUser(userId), getUser(anotherId));
        ChannelResponse channelResponse = ChannelResponse.builder().id(channelId).participants(participants).type(Type.DIRECT).build();
        BDDMockito.given(chatService.findChannelBetweenUserIds(BDDMockito.anyString(), BDDMockito.anyString()))
            .willReturn(channelResponse);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/channels/between")
                .queryParam("userId", anotherId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.equalTo(Type.DIRECT.toString())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.participants[*].id", Matchers.equalTo(List.of(userId, anotherId))));

        // verify
        BDDMockito.verify(chatService).findChannelBetweenUserIds(BDDMockito.anyString(), BDDMockito.anyString());
    }

    @Test
    void givenRequestWhenCreateNewGroupChannelThenReturnNewChannel() throws Exception {
        // given
        String userId = getRandomId();
        String channelName = RandomString.make();
        NewGroupChannelRequest request = new NewGroupChannelRequest(channelName);
        BDDMockito.given(chatService.createNewGroupChannel(BDDMockito.anyString(), BDDMockito.any(NewGroupChannelRequest.class)))
            .willAnswer(invocation -> ChannelResponse.builder()
                .name(invocation.getArgument(1, NewGroupChannelRequest.class).getName())
                .type(Type.GROUP)
                .participants(List.of(getUser(invocation.getArgument(0, String.class))))
                .build());

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.post("/v1/channels")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(channelName)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.equalTo(Type.GROUP.toString())));

        // verify
        BDDMockito.verify(chatService).createNewGroupChannel(userId, request);
    }

    @Test
    void givenChannelIdWhenJoinThenReturnChannel() throws Exception {
        // given
        String userId = getRandomId();
        String channelId = getRandomId();
        ChannelResponse channelResponse = ChannelResponse.builder()
            .id(channelId)
            .participants(List.of(getUser(), getUser(), getUser(userId)))
            .type(Type.GROUP)
            .build();
        BDDMockito.given(chatService.join(BDDMockito.anyString(), BDDMockito.anyString()))
            .willReturn(channelResponse);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.post("/v1/channels/{}", channelId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(channelId)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.participants[?(@.id == '" + userId + "')]", Matchers.hasSize(1)));

        // verify
        BDDMockito.verify(chatService).join(BDDMockito.anyString(), BDDMockito.anyString());
    }

    @Test
    void givenChannelIdAndMessageIdWhenReadThenReturnMessage() throws Exception {
        // given
        String userId = getRandomId();
        String channelId = getRandomId();
        String messageId = getRandomId();
        MessageResponse messageResponse = MessageResponse.builder().id(messageId).channelId(channelId).build();
        BDDMockito.given(chatService.read(BDDMockito.anyString(), BDDMockito.anyString()))
            .willReturn(messageResponse);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("/v1/channels/{}/messages/{}", channelId, messageId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.channelId", Matchers.equalTo(channelId)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(messageId)));

        // verify
        BDDMockito.verify(chatService).read(BDDMockito.anyString(), BDDMockito.anyString());
    }

    @Test
    void givenChannelIdAndRequestWhenSendThenReturnMessage() throws Exception {
        // given
        String userId = getRandomId();
        String channelId = getRandomId();
        NewMessageRequest request = NewMessageRequest.builder()
            .senderId(userId)
            .channelId(channelId)
            .senderName(RandomString.make())
            .content(RandomString.make())
            .build();
        MessageResponse messageResponse = MessageResponse.builder()
            .id(getRandomId())
            .channelId(channelId)
            .senderId(userId)
            .content(request.getContent())
            .build();
        BDDMockito.given(chatService.send(BDDMockito.any(NewMessageRequest.class)))
            .willReturn(messageResponse);

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.post("/v1/channels/{}/messages", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.channelId", Matchers.equalTo(channelId)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.senderId", Matchers.equalTo(userId)));

        // verify
        BDDMockito.verify(chatService).send(BDDMockito.any(NewMessageRequest.class));
    }

    @Test
    void givenChannelIdWhenLeaveThenReturnStatusIsOk() throws Exception {
        // given
        String channelId = getRandomId();
        String userId = getRandomId();
        BDDMockito.doNothing().when(chatService).leave(BDDMockito.anyString(), BDDMockito.anyString());

        // when
        ResultActions resultActions = mockMvc
            .perform(MockMvcRequestBuilders.delete("/v1/channels/{}", channelId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(userId))))
            .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk());

        // verify
        BDDMockito.verify(chatService).leave(BDDMockito.anyString(), BDDMockito.anyString());
    }

    private UserResponse getUser() {
        return getUser(getRandomId());
    }

    private UserResponse getUser(String id) {
        return new UserResponse(id, RandomString.make(10), RandomString.make(10), RandomString.make(10), false, RandomString.make());
    }

    private String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
