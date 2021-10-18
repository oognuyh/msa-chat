package com.oognuyh.userservice.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private final KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl(keycloakProperties.getAuthServerUrl())
            .grantType(OAuth2Constants.PASSWORD)
            .realm(keycloakProperties.getMasterRealm())
            .clientId(keycloakProperties.getClientId())
            .username(keycloakProperties.getUsername())
            .password(keycloakProperties.getPassword())
            .resteasyClient(new ResteasyClientBuilder()
                .connectionPoolSize(10)
                .build())   
            .build();
    }
}
