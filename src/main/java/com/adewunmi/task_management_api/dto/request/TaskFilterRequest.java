package com.adewunmi.task_management_api.dto.request;

import com.adewunmi.task_management_api.enums.TaskPriority;
import com.adewunmi.task_management_api.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for filtering and searching tasks")
public class TaskFilterRequest {

    @Schema(description = "Search term for title and description", example = "authentication")
    private String search;

    @Schema(description = "Filter by task status", example = "[\"TODO\", \"IN_PROGRESS\"]")
    private List<TaskStatus> statuses;

    @Schema(description = "Filter by task priority", example = "[\"HIGH\", \"URGENT\"]")
    private List<TaskPriority> priorities;

    @Schema(description = "Filter by assigned user ID", example = "1")
    private Long assignedToId;

    @Schema(description = "Filter by creator user ID", example = "2")
    private Long createdById;

    @Schema(description = "Filter by due date from", example = "2024-01-01T00:00:00")
    private LocalDateTime dueDateFrom;

    @Schema(description = "Filter by due date to", example = "2024-12-31T23:59:59")
    private LocalDateTime dueDateTo;

    @Schema(description = "Filter by tags (comma-separated)", example = "backend,security")
    private String tags;

    @Schema(description = "Filter overdue tasks only", example = "true")
    private Boolean overdue;

    @Schema(description = "Filter completed tasks only", example = "false")
    private Boolean completed;

    @Schema(description = "Page number (0-indexed)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "Page size", example = "20", defaultValue = "20")
    private Integer size = 20;

    @Schema(description = "Sort field", example = "dueDate", defaultValue = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Sort direction (asc/desc)", example = "desc", defaultValue = "desc")
    private String sortDirection = "desc";
}
