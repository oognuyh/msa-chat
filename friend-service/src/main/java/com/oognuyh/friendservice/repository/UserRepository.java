package com.oognuyh.friendservice.repository;

import com.oognuyh.friendservice.payload.response.UserResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserRepository {
    
    @GetMapping("/v1/users/{id}")
    UserResponse findUserById(@PathVariable("id") String id);
}
