package com.adewunmi.task_management_api.controller;

import com.adewunmi.task_management_api.dto.request.LoginRequest;
import com.adewunmi.task_management_api.dto.request.RefreshTokenRequest;
import com.adewunmi.task_management_api.dto.request.RegisterRequest;
import com.adewunmi.task_management_api.dto.response.AuthResponse;
import com.adewunmi.task_management_api.dto.response.UserResponse;
import com.adewunmi.task_management_api.service.AuthService;
import com.adewunmi.task_management_api.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and registration endpoints")

public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new organization and admin user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success("Registration successful", response),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login to existing account")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user information")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}