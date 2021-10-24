package com.oognuyh.imageservice.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewImageResponse {
    
    private String imageUrl;
}
