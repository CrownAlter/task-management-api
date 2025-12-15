package com.adewunmi.task_management_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing audit log details")
public class AuditLogResponse {

    @Schema(description = "Audit log ID", example = "1")
    private Long id;

    @Schema(description = "Action performed", example = "CREATE_TASK")
    private String action;

    @Schema(description = "Entity type", example = "Task")
    private String entityType;

    @Schema(description = "Entity ID", example = "123")
    private Long entityId;

    @Schema(description = "User who performed the action")
    private UserSummary user;

    @Schema(description = "Additional details about the action")
    private String details;

    @Schema(description = "IP address of the user")
    private String ipAddress;

    @Schema(description = "Timestamp of the action")
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}
