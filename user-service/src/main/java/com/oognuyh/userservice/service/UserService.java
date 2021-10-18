package com.oognuyh.userservice.service;

import java.util.List;

import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
import com.oognuyh.userservice.payload.request.UserUpdateRequest;
import com.oognuyh.userservice.payload.response.UserResponse;

public interface UserService {
    
    List<UserResponse> findAll();
    UserResponse findById(String id);
    void updateStatus(String id);
    void updatePassword(String id, PasswordUpdateRequest request);
    UserResponse updateInfo(String id, UserUpdateRequest request);
}
