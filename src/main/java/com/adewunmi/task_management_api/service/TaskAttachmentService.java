package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.response.AttachmentResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for Task Attachment operations
 * Handles file upload, download, and attachment management for tasks
 */
public interface TaskAttachmentService {
    
    /**
     * Upload an attachment to a task
     */
    AttachmentResponse uploadAttachment(Long taskId, MultipartFile file);
    
    /**
     * Get all attachments for a task
     */
    Page<AttachmentResponse> getTaskAttachments(Long taskId, Pageable pageable);
    
    /**
     * Get all attachments for a task (non-paginated)
     */
    List<AttachmentResponse> getTaskAttachmentsList(Long taskId);
    
    /**
     * Get attachment by ID
     */
    AttachmentResponse getAttachmentById(Long attachmentId);
    
    /**
     * Download an attachment file
     */
    Resource downloadAttachment(Long attachmentId);
    
    /**
     * Delete an attachment
     */
    void deleteAttachment(Long attachmentId);
    
    /**
     * Get total attachment count for a task
     */
    Long getAttachmentCount(Long taskId);
}
