package com.oognuyh.chatservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class User {
    
    @Id
    private String id;

    private String name;

    private String email;

    private String imageUrl;

    private Boolean isActive;

    private String statusMessage;
}
