package com.adewunmi.task_management_api.controller;

import com.adewunmi.task_management_api.dto.request.CommentRequest;
import com.adewunmi.task_management_api.dto.response.ApiResponse;
import com.adewunmi.task_management_api.dto.response.CommentResponse;
import com.adewunmi.task_management_api.service.TaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Task Comment Management
 * Provides endpoints for adding, retrieving, updating, and deleting comments on tasks
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Comments", description = "Endpoints for managing task comments")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskCommentController {

    private final TaskCommentService commentService;

    @PostMapping("/{taskId}/comments")
    @Operation(summary = "Add comment to task", description = "Adds a new comment to a specific task")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addComment(taskId, request);
        return new ResponseEntity<>(
                ApiResponse.success("Comment added successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}/comments")
    @Operation(summary = "Get task comments", description = "Retrieves all comments for a specific task with pagination")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getTaskComments(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentResponse> response = commentService.getTaskComments(taskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/comments/{commentId}")
    @Operation(summary = "Get comment by ID", description = "Retrieves a specific comment by its ID")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(
            @Parameter(description = "Comment ID") @PathVariable Long commentId) {
        CommentResponse response = commentService.getCommentById(commentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "Update comment", description = "Updates a comment (only by the author)")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @Parameter(description = "Comment ID") @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Deletes a comment (only by the author or admin)")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "Comment ID") @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }

    @GetMapping("/{taskId}/comments/count")
    @Operation(summary = "Get comment count", description = "Retrieves the total number of comments for a task")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(
            @Parameter(description = "Task ID") @PathVariable Long taskId) {
        Long count = commentService.getCommentCount(taskId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
