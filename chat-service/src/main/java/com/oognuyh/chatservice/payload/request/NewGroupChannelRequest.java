package com.oognuyh.chatservice.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NewGroupChannelRequest {
    
    @NotBlank
    private String name;
}
