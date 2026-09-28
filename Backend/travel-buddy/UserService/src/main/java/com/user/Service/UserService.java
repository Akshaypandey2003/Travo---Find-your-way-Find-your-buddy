package com.user.Service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.user.DTO.AuthResponse;
import com.user.DTO.LoginRequest;
import com.user.DTO.PageResponse;
import com.user.DTO.RegisterRequest;
import com.user.DTO.ResetPasswordRequest;
import com.user.DTO.UpdateUserRequest;
import com.user.DTO.UserResponse;
import com.user.DTO.MessageResponse;
import com.user.Entity.User;

public interface UserService 
{
    List<String> getPublicUserIds(int page, int size);

    public AuthResponse addUser(RegisterRequest request);;
    public User getUserByEmail(String email);
    public PageResponse<UserResponse> getUserByPreferences(Pageable pageable, List<String> preferences);
    public AuthResponse generateToken(LoginRequest user);

    UserResponse getUserById(String userId);

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(String userId, UpdateUserRequest request);

    void deleteUser(String userId);

    MessageResponse forgotPassword(String email);
    void resetPassword(ResetPasswordRequest request);
} 
    
   