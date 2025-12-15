package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.CommentRequest;
import com.adewunmi.task_management_api.dto.response.CommentResponse;
import com.adewunmi.task_management_api.entity.Task;
import com.adewunmi.task_management_api.entity.TaskComment;
import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import com.adewunmi.task_management_api.exception.UnauthorizedException;
import com.adewunmi.task_management_api.multitenant.TenantContext;
import com.adewunmi.task_management_api.repository.TaskCommentRepository;
import com.adewunmi.task_management_api.repository.TaskRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for Task Comment Management
 * Provides functionality to add, retrieve, update, and delete comments on tasks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponse addComment(Long taskId, CommentRequest request) {
        log.info("Adding comment to task ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new BadRequestException("Tenant context not found");
        }
        
        // Verify task exists and belongs to tenant
        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        // Get current user
        CustomUserDetails currentUser = getCurrentUserDetails();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        
        // Create comment
        TaskComment comment = TaskComment.builder()
                .task(task)
                .user(user)
                .content(request.getContent())
                .edited(false)
                .build();
        
        TaskComment savedComment = commentRepository.save(comment);
        log.info("Comment added successfully with ID: {}", savedComment.getId());
        
        return mapToResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getTaskComments(Long taskId, Pageable pageable) {
        log.info("Fetching comments for task ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Verify task exists and belongs to tenant
        taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        Page<TaskComment> comments = commentRepository.findByTaskIdAndDeletedAtIsNull(taskId, pageable);
        return comments.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId) {
        log.info("Fetching comment with ID: {}", commentId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        TaskComment comment = commentRepository.findByIdAndTenantId(commentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        
        return mapToResponse(comment);
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        log.info("Updating comment with ID: {}", commentId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        TaskComment comment = commentRepository.findByIdAndTenantId(commentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        
        // Verify the current user is the author
        CustomUserDetails currentUser = getCurrentUserDetails();
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only edit your own comments");
        }
        
        // Update comment
        comment.setContent(request.getContent());
        comment.setEdited(true);
        
        TaskComment updatedComment = commentRepository.save(comment);
        log.info("Comment updated successfully with ID: {}", updatedComment.getId());
        
        return mapToResponse(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        TaskComment comment = commentRepository.findByIdAndTenantId(commentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        
        // Verify the current user is the author or has admin role
        CustomUserDetails currentUser = getCurrentUserDetails();
        boolean isAuthor = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAuthor && !isAdmin) {
            throw new UnauthorizedException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
        log.info("Comment deleted successfully with ID: {}", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCommentCount(Long taskId) {
        log.info("Fetching comment count for task ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Verify task exists and belongs to tenant
        taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        return commentRepository.countByTaskIdAndDeletedAtIsNull(taskId);
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
     * Map TaskComment entity to CommentResponse DTO
     */
    private CommentResponse mapToResponse(TaskComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .taskId(comment.getTask().getId())
                .user(mapUserToSummary(comment.getUser()))
                .edited(comment.getEdited())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /**
     * Map User entity to UserSummary DTO
     */
    private CommentResponse.UserSummary mapUserToSummary(User user) {
        return CommentResponse.UserSummary.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
