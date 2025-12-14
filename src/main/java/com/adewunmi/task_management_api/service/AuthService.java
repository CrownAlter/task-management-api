package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.LoginRequest;
import com.adewunmi.task_management_api.dto.request.RefreshTokenRequest;
import com.adewunmi.task_management_api.dto.request.RegisterRequest;
import com.adewunmi.task_management_api.dto.response.AuthResponse;
import com.adewunmi.task_management_api.dto.response.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    UserResponse getCurrentUser();
}
