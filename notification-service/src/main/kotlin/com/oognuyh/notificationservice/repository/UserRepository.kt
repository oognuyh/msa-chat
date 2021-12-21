package com.oognuyh.notificationservice.repository

import com.oognuyh.notificationservice.payload.request.StatusUpdateRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "user-service")
interface UserRepository {

    @PutMapping("/v1/users/{id}/status")
    fun updateStatus(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authToken: String,
        @PathVariable("id") id: String,
        statusUpdateRequest: StatusUpdateRequest
    )
}