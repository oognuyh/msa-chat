package com.oognuyh.chatservice.config;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

// @Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // private final JwtDecoder jwtDecoder;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);

        return false;
    }

    // @Override
    // public void configureClientInboundChannel(ChannelRegistration registration) {
    //     registration.interceptors(new ChannelInterceptor(){

    //         @Override
    //         public Message<?> preSend(Message<?> message, MessageChannel channel) {
    //             StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    //             if (StompCommand.CONNECT.equals(accessor.getCommand())) {
    //                 List<String> authorization = accessor.getNativeHeader(HttpHeaders.AUTHORIZATION);
                    
    //                 log.info("Authorization header: {}", authorization);
                    
    //                 String authToken = authorization.get(0).substring("Bearer ".length());

    //                 log.info("auth token: {}", authToken);

    //                 JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    //                 Jwt jwt = jwtDecoder.decode(authToken);
                    
    //                 accessor.setUser(converter.convert(jwt));
    //             }

    //             return message;
    //         }
    //     });
    // }
}
