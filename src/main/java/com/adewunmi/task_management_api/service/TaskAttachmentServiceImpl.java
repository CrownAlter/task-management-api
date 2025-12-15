package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.response.AttachmentResponse;
import com.adewunmi.task_management_api.entity.Task;
import com.adewunmi.task_management_api.entity.TaskAttachment;
import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import com.adewunmi.task_management_api.exception.UnauthorizedException;
import com.adewunmi.task_management_api.multitenant.TenantContext;
import com.adewunmi.task_management_api.repository.TaskAttachmentRepository;
import com.adewunmi.task_management_api.repository.TaskRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Task Attachment Management
 * Provides functionality to upload, download, and manage file attachments on tasks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    public AttachmentResponse uploadAttachment(Long taskId, MultipartFile file) {
        log.info("Uploading attachment to task ID: {}", taskId);
        
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
        
        // Store file
        String filePath = fileStorageService.storeFile(file, tenantId);
        
        // Create attachment record
        TaskAttachment attachment = TaskAttachment.builder()
                .task(task)
                .uploadedBy(user)
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();
        
        TaskAttachment savedAttachment = attachmentRepository.save(attachment);
        log.info("Attachment uploaded successfully with ID: {}", savedAttachment.getId());
        
        return mapToResponse(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttachmentResponse> getTaskAttachments(Long taskId, Pageable pageable) {
        log.info("Fetching attachments for task ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Verify task exists and belongs to tenant
        taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        Page<TaskAttachment> attachments = attachmentRepository.findByTaskIdAndDeletedAtIsNull(taskId, pageable);
        return attachments.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getTaskAttachmentsList(Long taskId) {
        log.info("Fetching all attachments for task ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Verify task exists and belongs to tenant
        taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        List<TaskAttachment> attachments = attachmentRepository.findByTaskIdAndDeletedAtIsNullOrderByCreatedAtDesc(taskId);
        return attachments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(Long attachmentId) {
        log.info("Fetching attachment with ID: {}", attachmentId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        TaskAttachment attachment = attachmentRepository.findByIdAndTenantId(attachmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));
        
        return mapToResponse(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadAttachment(Long attachmentId) {
        log.info("Downloading attachment with ID: {}", attachmentId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        TaskAttachment attachment = attachmentRepository.findByIdAndTenantId(attachmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));
        
        return fileStorageService.loadFileAsResource(attachment.getFilePath());
    }

    @Override
    public void deleteAttachment(Long attachmentId) {
        log.info("Deleting attachment with ID: {}", attachmentId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        TaskAttachment attachment = attachmentRepository.findByIdAndTenantId(attachmentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));
        
        // Verify the current user is the uploader or has admin role
        CustomUserDetails currentUser = getCurrentUserDetails();
        boolean isUploader = attachment.getUploadedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isUploader && !isAdmin) {
            throw new UnauthorizedException("You can only delete your own attachments");
        }
        
        // Delete file from storage
        try {
            fileStorageService.deleteFile(attachment.getFilePath());
        } catch (Exception e) {
            log.error("Error deleting file from storage: {}", attachment.getFilePath(), e);
        }
        
        // Delete attachment record
        attachmentRepository.delete(attachment);
        log.info("Attachment deleted successfully with ID: {}", attachmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAttachmentCount(Long taskId) {
        log.info("Fetching attachment count for task ID: {}", taskId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        
        // Verify task exists and belongs to tenant
        taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        return attachmentRepository.countByTaskIdAndDeletedAtIsNull(taskId);
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
     * Map TaskAttachment entity to AttachmentResponse DTO
     */
    private AttachmentResponse mapToResponse(TaskAttachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .mimeType(attachment.getMimeType())
                .taskId(attachment.getTask().getId())
                .uploadedBy(mapUserToSummary(attachment.getUploadedBy()))
                .downloadUrl("/api/v1/tasks/attachments/" + attachment.getId() + "/download")
                .createdAt(attachment.getCreatedAt())
                .build();
    }

    /**
     * Map User entity to UserSummary DTO
     */
    private AttachmentResponse.UserSummary mapUserToSummary(User user) {
        return AttachmentResponse.UserSummary.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
