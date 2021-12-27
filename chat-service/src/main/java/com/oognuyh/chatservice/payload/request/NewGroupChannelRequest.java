package com.oognuyh.chatservice.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewGroupChannelRequest {
    
    @NotBlank
    private String name;
}
