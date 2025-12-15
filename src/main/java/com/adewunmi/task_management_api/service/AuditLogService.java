package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Audit Logging
 * Tracks and records all important operations in the system
 */
public interface AuditLogService {
    
    /**
     * Log an action performed by a user
     */
    void logAction(String action, String entityType, Long entityId, String details, String ipAddress);
    
    /**
     * Get all audit logs for current tenant with pagination
     */
    Page<AuditLogResponse> getAuditLogs(Pageable pageable);
    
    /**
     * Get audit logs for a specific entity
     */
    Page<AuditLogResponse> getAuditLogsByEntity(String entityType, Long entityId, Pageable pageable);
    
    /**
     * Get audit logs for a specific user
     */
    Page<AuditLogResponse> getAuditLogsByUser(Long userId, Pageable pageable);
    
    /**
     * Get audit logs by action type
     */
    Page<AuditLogResponse> getAuditLogsByAction(String action, Pageable pageable);
}
