package com.adewunmi.task_management_api.controller;

import com.adewunmi.task_management_api.dto.response.ApiResponse;
import com.adewunmi.task_management_api.dto.response.AuditLogResponse;
import com.adewunmi.task_management_api.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Audit Log Management
 * Provides endpoints for viewing audit trails and system activity logs
 * Restricted to admin users only
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Endpoints for viewing audit trails (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Get all audit logs", description = "Retrieves all audit logs for the current tenant (Admin only)")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> response = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs by entity", description = "Retrieves audit logs for a specific entity (Admin only)")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogsByEntity(
            @Parameter(description = "Entity type", example = "Task") @PathVariable String entityType,
            @Parameter(description = "Entity ID") @PathVariable Long entityId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> response = auditLogService.getAuditLogsByEntity(entityType, entityId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get audit logs by user", description = "Retrieves audit logs for a specific user (Admin only)")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> response = auditLogService.getAuditLogsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Get audit logs by action", description = "Retrieves audit logs for a specific action type (Admin only)")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogsByAction(
            @Parameter(description = "Action type", example = "CREATE_TASK") @PathVariable String action,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> response = auditLogService.getAuditLogsByAction(action, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
