package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.PasswordChangeRequest;
import com.adewunmi.task_management_api.dto.request.UserUpdateRequest;
import com.adewunmi.task_management_api.dto.response.UserResponse;
import com.adewunmi.task_management_api.entity.Role;
import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.enums.TaskStatus;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import com.adewunmi.task_management_api.multitenant.TenantContext;
import com.adewunmi.task_management_api.repository.RoleRepository;
import com.adewunmi.task_management_api.repository.TaskRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for User Management
 * Provides comprehensive user administration and profile management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        log.info("Fetching current user profile");
        CustomUserDetails currentUser = getCurrentUserDetails();
        
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        log.info("Updating current user profile");
        CustomUserDetails currentUser = getCurrentUserDetails();
        
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        
        updateUserFields(user, request);
        
        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for user ID: {}", updatedUser.getId());
        
        return mapToResponse(updatedUser);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        log.info("Changing password for current user");
        
        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation do not match");
        }
        
        CustomUserDetails currentUser = getCurrentUserDetails();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Validate new password strength
        validatePasswordStrength(request.getNewPassword());
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed successfully for user ID: {}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users in tenant with pagination");
        
        Long tenantId = TenantContext.getCurrentTenant();
        Page<User> users = userRepository.findByTenantIdAndDeletedAtIsNull(tenantId, pageable);
        
        return users.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        log.info("Searching users with query: {}", query);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Page<User> users = userRepository.searchByNameOrEmail(query, tenantId, pageable);
        
        return users.map(this::mapToResponse);
    }

    @Override
    public UserResponse activateUser(Long userId) {
        log.info("Activating user with ID: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setActive(true);
        User updatedUser = userRepository.save(user);
        
        log.info("User activated successfully: {}", userId);
        return mapToResponse(updatedUser);
    }

    @Override
    public UserResponse deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Prevent deactivating self
        CustomUserDetails currentUser = getCurrentUserDetails();
        if (user.getId().equals(currentUser.getId())) {
            throw new BadRequestException("Cannot deactivate your own account");
        }
        
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        
        log.info("User deactivated successfully: {}", userId);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Prevent deleting self
        CustomUserDetails currentUser = getCurrentUserDetails();
        if (user.getId().equals(currentUser.getId())) {
            throw new BadRequestException("Cannot delete your own account");
        }
        
        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);
    }

    @Override
    public UserResponse updateUserRoles(Long userId, UserUpdateRequest request) {
        log.info("Updating roles for user ID: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User roles updated successfully for user ID: {}", userId);
        
        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics(Long userId) {
        log.info("Fetching statistics for user ID: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Long totalTasksCreated = taskRepository.countByCreatedByIdAndTenantIdAndDeletedAtIsNull(userId, tenantId);
        Long totalTasksAssigned = taskRepository.countByAssignedToIdAndTenantIdAndDeletedAtIsNull(userId, tenantId);
        Long completedTasks = taskRepository.countByAssignedToIdAndStatusAndTenantIdAndDeletedAtIsNull(
                userId, TaskStatus.COMPLETED, tenantId);
        Long pendingTasks = taskRepository.countByAssignedToIdAndStatusInAndTenantIdAndDeletedAtIsNull(
                userId, Set.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.IN_REVIEW), tenantId);
        Long overdueTasks = taskRepository.countByAssignedToIdAndDueDateBeforeAndStatusNotAndTenantIdAndDeletedAtIsNull(
                userId, LocalDateTime.now(), TaskStatus.COMPLETED, tenantId);
        
        return UserStatistics.builder()
                .totalTasksCreated(totalTasksCreated)
                .totalTasksAssigned(totalTasksAssigned)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .overdueTasks(overdueTasks)
                .build();
    }

    /**
     * Get current authenticated user details
     */
    private CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new BadRequestException("Invalid user details");
        }
        
        return (CustomUserDetails) principal;
    }

    /**
     * Update user fields from request
     */
    private void updateUserFields(User user, UserUpdateRequest request) {
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Check if email is already taken by another user in the same tenant
            userRepository.findByEmailAndTenantId(request.getEmail(), user.getTenant().getId())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(user.getId())) {
                            throw new BadRequestException("Email is already taken");
                        }
                    });
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
    }

    /**
     * Validate password strength
     */
    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new BadRequestException(
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.getActive())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
