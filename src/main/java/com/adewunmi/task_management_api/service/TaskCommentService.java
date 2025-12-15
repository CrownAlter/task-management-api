package com.adewunmi.task_management_api.service;

import com.adewunmi.task_management_api.dto.request.CommentRequest;
import com.adewunmi.task_management_api.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Task Comment operations
 * Handles comment creation, retrieval, updates, and deletion on tasks
 */
public interface TaskCommentService {
    
    /**
     * Add a comment to a task
     */
    CommentResponse addComment(Long taskId, CommentRequest request);
    
    /**
     * Get all comments for a task with pagination
     */
    Page<CommentResponse> getTaskComments(Long taskId, Pageable pageable);
    
    /**
     * Get a specific comment by ID
     */
    CommentResponse getCommentById(Long commentId);
    
    /**
     * Update a comment (only by the author)
     */
    CommentResponse updateComment(Long commentId, CommentRequest request);
    
    /**
     * Delete a comment (only by the author or admin)
     */
    void deleteComment(Long commentId);
    
    /**
     * Get total comment count for a task
     */
    Long getCommentCount(Long taskId);
}
