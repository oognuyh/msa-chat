package com.oognuyh.imageservice.payload.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarChangedEvent {
    
    private String userId;

    private String imageUrl;
}
