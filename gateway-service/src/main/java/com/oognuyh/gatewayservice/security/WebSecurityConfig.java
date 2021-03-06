package com.oognuyh.gatewayservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf()
                .disable()
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/ws/**", "/actuator/**", "/api/*/actuator/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/images/avatars/**").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer()
                .jwt();

        return http.build();
    }
}
