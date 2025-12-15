package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.response.AuditLogResponse;
import com.adewunmi.task_management_api.entity.AuditLog;
import com.adewunmi.task_management_api.entity.Tenant;
import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.multitenant.TenantContext;
import com.adewunmi.task_management_api.repository.AuditLogRepository;
import com.adewunmi.task_management_api.repository.TenantRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementation for Audit Logging
 * Provides comprehensive audit trail functionality for all system operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Async
    public void logAction(String action, String entityType, Long entityId, String details, String ipAddress) {
        try {
            Long tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                log.warn("Cannot log action: Tenant context not found");
                return;
            }
            
            Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
            if (tenant == null) {
                log.warn("Cannot log action: Tenant not found with ID: {}", tenantId);
                return;
            }
            
            // Get current user (may be null for system actions)
            User user = null;
            try {
                CustomUserDetails currentUser = getCurrentUserDetails();
                if (currentUser != null) {
                    user = userRepository.findById(currentUser.getId()).orElse(null);
                }
            } catch (Exception e) {
                log.debug("No authenticated user found for audit log");
            }
            
            AuditLog auditLog = AuditLog.builder()
                    .tenant(tenant)
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .details(details)
                    .ipAddress(ipAddress)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", action, entityType, entityId);
        } catch (Exception e) {
            log.error("Error creating audit log", e);
            // Don't throw exception to avoid breaking the main operation
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        log.info("Fetching audit logs");
        
        Long tenantId = TenantContext.getCurrentTenant();
        Page<AuditLog> logs = auditLogRepository.findByTenantIdOrderByTimestampDesc(tenantId, pageable);
        
        return logs.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByEntity(String entityType, Long entityId, Pageable pageable) {
        log.info("Fetching audit logs for entity: {} - {}", entityType, entityId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Page<AuditLog> logs = auditLogRepository.findByTenantIdAndEntityTypeAndEntityIdOrderByTimestampDesc(
                tenantId, entityType, entityId, pageable);
        
        return logs.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByUser(Long userId, Pageable pageable) {
        log.info("Fetching audit logs for user: {}", userId);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Page<AuditLog> logs = auditLogRepository.findByTenantIdAndUserIdOrderByTimestampDesc(
                tenantId, userId, pageable);
        
        return logs.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByAction(String action, Pageable pageable) {
        log.info("Fetching audit logs for action: {}", action);
        
        Long tenantId = TenantContext.getCurrentTenant();
        Page<AuditLog> logs = auditLogRepository.findByTenantIdAndActionOrderByTimestampDesc(
                tenantId, action, pageable);
        
        return logs.map(this::mapToResponse);
    }

    /**
     * Get current authenticated user details
     */
    private CustomUserDetails getCurrentUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof CustomUserDetails)) {
                return null;
            }
            
            return (CustomUserDetails) principal;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Map AuditLog entity to AuditLogResponse DTO
     */
    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .user(log.getUser() != null ? mapUserToSummary(log.getUser()) : null)
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .timestamp(log.getTimestamp())
                .build();
    }

    /**
     * Map User entity to UserSummary DTO
     */
    private AuditLogResponse.UserSummary mapUserToSummary(User user) {
        return AuditLogResponse.UserSummary.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
