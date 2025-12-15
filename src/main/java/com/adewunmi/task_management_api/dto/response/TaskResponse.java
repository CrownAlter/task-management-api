package com.adewunmi.task_management_api.dto.response;

import com.adewunmi.task_management_api.enums.TaskPriority;
import com.adewunmi.task_management_api.enums.TaskStatus;
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
@Schema(description = "Response object containing task details")
public class TaskResponse {

    @Schema(description = "Task ID", example = "1")
    private Long id;

    @Schema(description = "Task title", example = "Implement user authentication")
    private String title;

    @Schema(description = "Task description")
    private String description;

    @Schema(description = "Task status", example = "TODO")
    private TaskStatus status;

    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Task due date")
    private LocalDateTime dueDate;

    @Schema(description = "User who created the task")
    private UserSummary createdBy;

    @Schema(description = "User assigned to the task")
    private UserSummary assignedTo;

    @Schema(description = "Comma-separated tags")
    private String tags;

    @Schema(description = "Task completion date")
    private LocalDateTime completedAt;

    @Schema(description = "Number of comments")
    private Integer commentCount;

    @Schema(description = "Number of attachments")
    private Integer attachmentCount;

    @Schema(description = "Task creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Task last update timestamp")
    private LocalDateTime updatedAt;

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
