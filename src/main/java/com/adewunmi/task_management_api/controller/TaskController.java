package com.adewunmi.task_management_api.controller;

import com.adewunmi.task_management_api.dto.request.TaskFilterRequest;
import com.adewunmi.task_management_api.dto.request.TaskRequest;
import com.adewunmi.task_management_api.dto.response.ApiResponse;
import com.adewunmi.task_management_api.dto.response.TaskResponse;
import com.adewunmi.task_management_api.enums.TaskStatus;
import com.adewunmi.task_management_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task in the current tenant")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(
                ApiResponse.success("Task created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a task by its ID (tenant isolated)")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Updates an existing task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Soft deletes a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks with filtering, search, pagination and sorting")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Task statuses") @RequestParam(required = false) java.util.List<TaskStatus> statuses,
            @Parameter(description = "Task priorities") @RequestParam(required = false) java.util.List<com.adewunmi.task_management_api.enums.TaskPriority> priorities,
            @Parameter(description = "Assigned user ID") @RequestParam(required = false) Long assignedToId,
            @Parameter(description = "Creator user ID") @RequestParam(required = false) Long createdById,
            @Parameter(description = "Due date from") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dueDateFrom,
            @Parameter(description = "Due date to") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dueDateTo,
            @Parameter(description = "Tags") @RequestParam(required = false) String tags,
            @Parameter(description = "Show overdue tasks only") @RequestParam(required = false) Boolean overdue,
            @Parameter(description = "Show completed tasks only") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        TaskFilterRequest filterRequest = TaskFilterRequest.builder()
                .search(search)
                .statuses(statuses)
                .priorities(priorities)
                .assignedToId(assignedToId)
                .createdById(createdById)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .tags(tags)
                .overdue(overdue)
                .completed(completed)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<TaskResponse> response = taskService.getAllTasks(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/assign/{userId}")
    @Operation(summary = "Assign task to user", description = "Assigns a task to a specific user")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        TaskResponse response = taskService.assignTask(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Task assigned successfully", response));
    }

    @PostMapping("/{id}/unassign")
    @Operation(summary = "Unassign task", description = "Removes the assignment from a task")
    public ResponseEntity<ApiResponse<TaskResponse>> unassignTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        TaskResponse response = taskService.unassignTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task unassigned successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Updates the status of a task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam TaskStatus status) {
        TaskResponse response = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", response));
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get my tasks", description = "Retrieves tasks assigned to the current user")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getMyTasks(
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Task statuses") @RequestParam(required = false) java.util.List<TaskStatus> statuses,
            @Parameter(description = "Task priorities") @RequestParam(required = false) java.util.List<com.adewunmi.task_management_api.enums.TaskPriority> priorities,
            @Parameter(description = "Due date from") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dueDateFrom,
            @Parameter(description = "Due date to") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dueDateTo,
            @Parameter(description = "Tags") @RequestParam(required = false) String tags,
            @Parameter(description = "Show overdue tasks only") @RequestParam(required = false) Boolean overdue,
            @Parameter(description = "Show completed tasks only") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        TaskFilterRequest filterRequest = TaskFilterRequest.builder()
                .search(search)
                .statuses(statuses)
                .priorities(priorities)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .tags(tags)
                .overdue(overdue)
                .completed(completed)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<TaskResponse> response = taskService.getMyTasks(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/created-by-me")
    @Operation(summary = "Get tasks created by me", description = "Retrieves tasks created by the current user")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksCreatedByMe(
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Task statuses") @RequestParam(required = false) java.util.List<TaskStatus> statuses,
            @Parameter(description = "Task priorities") @RequestParam(required = false) java.util.List<com.adewunmi.task_management_api.enums.TaskPriority> priorities,
            @Parameter(description = "Assigned user ID") @RequestParam(required = false) Long assignedToId,
            @Parameter(description = "Due date from") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dueDateFrom,
            @Parameter(description = "Due date to") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dueDateTo,
            @Parameter(description = "Tags") @RequestParam(required = false) String tags,
            @Parameter(description = "Show overdue tasks only") @RequestParam(required = false) Boolean overdue,
            @Parameter(description = "Show completed tasks only") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        TaskFilterRequest filterRequest = TaskFilterRequest.builder()
                .search(search)
                .statuses(statuses)
                .priorities(priorities)
                .assignedToId(assignedToId)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .tags(tags)
                .overdue(overdue)
                .completed(completed)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<TaskResponse> response = taskService.getTasksCreatedByMe(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
