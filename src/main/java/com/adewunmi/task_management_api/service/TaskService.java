package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.TaskFilterRequest;
import com.adewunmi.task_management_api.dto.request.TaskRequest;
import com.adewunmi.task_management_api.dto.response.TaskResponse;
import com.adewunmi.task_management_api.enums.TaskStatus;
import org.springframework.data.domain.Page;

public interface TaskService {
    
    /**
     * Create a new task
     */
    TaskResponse createTask(TaskRequest request);
    
    /**
     * Get a task by ID (with tenant isolation)
     */
    TaskResponse getTaskById(Long taskId);
    
    /**
     * Update an existing task
     */
    TaskResponse updateTask(Long taskId, TaskRequest request);
    
    /**
     * Delete a task (soft delete)
     */
    void deleteTask(Long taskId);
    
    /**
     * Get all tasks with filtering, search, pagination and sorting
     */
    Page<TaskResponse> getAllTasks(TaskFilterRequest filterRequest);
    
    /**
     * Assign a task to a user
     */
    TaskResponse assignTask(Long taskId, Long userId);
    
    /**
     * Unassign a task
     */
    TaskResponse unassignTask(Long taskId);
    
    /**
     * Update task status
     */
    TaskResponse updateTaskStatus(Long taskId, TaskStatus status);
    
    /**
     * Get tasks assigned to current user
     */
    Page<TaskResponse> getMyTasks(TaskFilterRequest filterRequest);
    
    /**
     * Get tasks created by current user
     */
    Page<TaskResponse> getTasksCreatedByMe(TaskFilterRequest filterRequest);
}
