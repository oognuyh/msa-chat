package com.oognuyh.userservice.payload.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserChangedEvent {
    
    private String userId;
}
