package com.adewunmi.task_management_api.dto.request;

import com.adewunmi.task_management_api.enums.TaskPriority;
import com.adewunmi.task_management_api.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a task")
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Schema(description = "Task title", example = "Implement user authentication")
    private String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Schema(description = "Task description", example = "Implement JWT-based authentication for the API")
    private String description;

    @NotNull(message = "Task status is required")
    @Schema(description = "Task status", example = "TODO")
    private TaskStatus status;

    @NotNull(message = "Task priority is required")
    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Task due date", example = "2024-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "ID of the user to assign the task to", example = "1")
    private Long assignedToId;

    @Schema(description = "Comma-separated tags", example = "backend,authentication,security")
    private String tags;
}
