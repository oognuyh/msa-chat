package com.oognuyh.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    
    private String authServerUrl;
    private String masterRealm;
    private String realm;
    private String clientId;
    private String username;
    private String password;
}
