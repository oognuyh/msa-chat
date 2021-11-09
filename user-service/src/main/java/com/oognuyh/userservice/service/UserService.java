package com.oognuyh.userservice.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oognuyh.userservice.payload.request.PasswordUpdateRequest;
import com.oognuyh.userservice.payload.request.StatusUpdateRequest;
import com.oognuyh.userservice.payload.request.UserUpdateRequest;
import com.oognuyh.userservice.payload.response.UserResponse;

public interface UserService {
    
    List<UserResponse> findUsersByQuery(String queryTerm);
    UserResponse findById(String id);
    void updateStatus(String userId, StatusUpdateRequest request) throws JsonProcessingException;
    void updatePassword(String userId, PasswordUpdateRequest request);
    UserResponse updateDetails(String userId, UserUpdateRequest request) throws JsonProcessingException;
}
