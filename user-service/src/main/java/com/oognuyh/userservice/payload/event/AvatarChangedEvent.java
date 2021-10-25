package com.oognuyh.userservice.payload.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarChangedEvent {
    
    private String userId;

    private String imageUrl;
}
