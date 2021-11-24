package com.oognuyh.notificationservice.config

import feign.codec.ErrorDecoder
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Configuration
@EnableFeignClients(basePackages = ["com.oognuyh.notificationservice.repository"])
class FeignConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder {
        return ErrorDecoder { _, response ->
            when (response?.status()) {
                400 -> ResponseStatusException(HttpStatus.BAD_REQUEST)
                401 -> ResponseStatusException(HttpStatus.UNAUTHORIZED)
                403 -> ResponseStatusException(HttpStatus.FORBIDDEN)
                404 -> ResponseStatusException(HttpStatus.NOT_FOUND)
                else -> ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}