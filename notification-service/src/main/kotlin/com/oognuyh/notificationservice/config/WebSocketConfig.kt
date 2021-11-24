package com.oognuyh.notificationservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.oognuyh.notificationservice.payload.request.StatusUpdateRequest
import com.oognuyh.notificationservice.repository.UserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.converter.DefaultContentTypeResolver
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.util.MimeTypeUtils
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketConfig(
    private val userRepository: UserRepository,
    private val jwtDecoder: JwtDecoder
) : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/notifications")
    }

    override fun configureMessageConverters(messageConverters: MutableList<MessageConverter>): Boolean {
        val resolver = DefaultContentTypeResolver()
        val converter = MappingJackson2MessageConverter()

        resolver.defaultMimeType = MimeTypeUtils.APPLICATION_JSON
        converter.objectMapper = ObjectMapper()
        converter.contentTypeResolver = resolver
        messageConverters.add(converter)

        return false
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

                println(accessor)

                when (accessor?.command) {
                    StompCommand.CONNECT -> {
                        val authToken = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
                            ?: throw  ResponseStatusException(HttpStatus.UNAUTHORIZED)
                        val jwt = jwtDecoder.decode(authToken.substring("Bearer ".length))
                        val jwtConverter = JwtAuthenticationConverter()

                        accessor.user = jwtConverter.convert(jwt)

                        userRepository.updateStatus(authToken, jwt.subject, StatusUpdateRequest(("on")))
                    }
                    else -> {}
                }

                return message
            }
        })
    }
}