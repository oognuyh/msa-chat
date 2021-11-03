package com.oognuyh.chatservice.repository;

import com.oognuyh.chatservice.payload.response.UserResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserRepository {
    
    @GetMapping("/users/{id}")
    UserResponse findUserById(@PathVariable("id") String id);
}
