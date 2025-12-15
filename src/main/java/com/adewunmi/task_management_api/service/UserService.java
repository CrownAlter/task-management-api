package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.PasswordChangeRequest;
import com.adewunmi.task_management_api.dto.request.UserUpdateRequest;
import com.adewunmi.task_management_api.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for User Management operations
 * Handles user CRUD, profile updates, and user administration
 */
public interface UserService {
    
    /**
     * Get current authenticated user's profile
     */
    UserResponse getCurrentUser();
    
    /**
     * Update current user's profile
     */
    UserResponse updateCurrentUser(UserUpdateRequest request);
    
    /**
     * Change current user's password
     */
    void changePassword(PasswordChangeRequest request);
    
    /**
     * Get user by ID (tenant isolated)
     */
    UserResponse getUserById(Long userId);
    
    /**
     * Get all users in current tenant with pagination
     */
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    /**
     * Search users by name or email
     */
    Page<UserResponse> searchUsers(String query, Pageable pageable);
    
    /**
     * Activate a user account
     */
    UserResponse activateUser(Long userId);
    
    /**
     * Deactivate a user account
     */
    UserResponse deactivateUser(Long userId);
    
    /**
     * Delete a user (soft delete)
     */
    void deleteUser(Long userId);
    
    /**
     * Update user roles (admin only)
     */
    UserResponse updateUserRoles(Long userId, UserUpdateRequest request);
    
    /**
     * Get user statistics
     */
    UserStatistics getUserStatistics(Long userId);
    
    /**
     * Inner class for user statistics
     */
    @lombok.Data
    @lombok.Builder
    class UserStatistics {
        private Long totalTasksCreated;
        private Long totalTasksAssigned;
        private Long completedTasks;
        private Long pendingTasks;
        private Long overdueTasks;
    }
}
