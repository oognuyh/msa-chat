package com.oognuyh.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate template) {
                Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                
                template.header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwt.getTokenValue()));
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {

            @Override
            public Exception decode(String methodKey, Response response) {
                
                switch (response.status()) {
                    case 400:
                        return new ResponseStatusException(HttpStatus.BAD_REQUEST, response.reason());
                    case 404:
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, response.reason());
                    default:
                        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.reason());
                }
            }
        };
    }
}
