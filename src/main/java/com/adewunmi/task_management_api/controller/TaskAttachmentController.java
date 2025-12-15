package com.adewunmi.task_management_api.controller;

import com.adewunmi.task_management_api.dto.response.ApiResponse;
import com.adewunmi.task_management_api.dto.response.AttachmentResponse;
import com.adewunmi.task_management_api.service.TaskAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for Task Attachment Management
 * Provides endpoints for uploading, downloading, and managing file attachments on tasks
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Attachments", description = "Endpoints for managing task file attachments")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;

    @PostMapping("/{taskId}/attachments")
    @Operation(summary = "Upload attachment", description = "Uploads a file attachment to a specific task")
    public ResponseEntity<ApiResponse<AttachmentResponse>> uploadAttachment(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
        
        AttachmentResponse response = attachmentService.uploadAttachment(taskId, file);
        return new ResponseEntity<>(
                ApiResponse.success("File uploaded successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}/attachments")
    @Operation(summary = "Get task attachments", description = "Retrieves all attachments for a specific task with pagination")
    public ResponseEntity<ApiResponse<Page<AttachmentResponse>>> getTaskAttachments(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AttachmentResponse> response = attachmentService.getTaskAttachments(taskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{taskId}/attachments/list")
    @Operation(summary = "Get all task attachments", description = "Retrieves all attachments for a task without pagination")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getTaskAttachmentsList(
            @Parameter(description = "Task ID") @PathVariable Long taskId) {
        
        List<AttachmentResponse> response = attachmentService.getTaskAttachmentsList(taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/attachments/{attachmentId}")
    @Operation(summary = "Get attachment by ID", description = "Retrieves a specific attachment by its ID")
    public ResponseEntity<ApiResponse<AttachmentResponse>> getAttachmentById(
            @Parameter(description = "Attachment ID") @PathVariable Long attachmentId) {
        
        AttachmentResponse response = attachmentService.getAttachmentById(attachmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/attachments/{attachmentId}/download")
    @Operation(summary = "Download attachment", description = "Downloads a file attachment")
    public ResponseEntity<Resource> downloadAttachment(
            @Parameter(description = "Attachment ID") @PathVariable Long attachmentId) {
        
        // Get attachment metadata first
        AttachmentResponse attachmentInfo = attachmentService.getAttachmentById(attachmentId);
        
        // Load file as Resource
        Resource resource = attachmentService.downloadAttachment(attachmentId);
        
        // Try to determine file's content type
        String contentType = attachmentInfo.getMimeType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + attachmentInfo.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    @Operation(summary = "Delete attachment", description = "Deletes a file attachment (only by uploader or admin)")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @Parameter(description = "Attachment ID") @PathVariable Long attachmentId) {
        
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(ApiResponse.success("Attachment deleted successfully", null));
    }

    @GetMapping("/{taskId}/attachments/count")
    @Operation(summary = "Get attachment count", description = "Retrieves the total number of attachments for a task")
    public ResponseEntity<ApiResponse<Long>> getAttachmentCount(
            @Parameter(description = "Task ID") @PathVariable Long taskId) {
        
        Long count = attachmentService.getAttachmentCount(taskId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
