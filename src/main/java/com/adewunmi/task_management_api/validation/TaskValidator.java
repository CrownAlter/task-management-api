package com.adewunmi.task_management_api.validation;

import com.adewunmi.task_management_api.dto.request.TaskRequest;
import com.adewunmi.task_management_api.enums.TaskStatus;
import com.adewunmi.task_management_api.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskValidator {

    /**
     * Validate task request for creation
     */
    public void validateTaskCreation(TaskRequest request) {
        validateBasicFields(request);
        validateDueDate(request.getDueDate());
        validateTags(request.getTags());
    }

    /**
     * Validate task request for update
     */
    public void validateTaskUpdate(TaskRequest request) {
        validateBasicFields(request);
        validateDueDate(request.getDueDate());
        validateTags(request.getTags());
    }

    /**
     * Validate status transition
     */
    public void validateStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new BadRequestException("Task is already in " + currentStatus + " status");
        }

        // Define valid status transitions
        switch (currentStatus) {
            case TODO:
                // Can transition to any status from TODO
                break;
            case IN_PROGRESS:
                if (newStatus == TaskStatus.TODO) {
                    // Allow going back to TODO
                    break;
                }
                if (newStatus != TaskStatus.IN_REVIEW && newStatus != TaskStatus.COMPLETED && newStatus != TaskStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Cannot transition from IN_PROGRESS to " + newStatus + ". Valid transitions: IN_REVIEW, COMPLETED, CANCELLED, TODO");
                }
                break;
            case IN_REVIEW:
                if (newStatus == TaskStatus.TODO || newStatus == TaskStatus.IN_PROGRESS) {
                    // Allow going back
                    break;
                }
                if (newStatus != TaskStatus.COMPLETED && newStatus != TaskStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Cannot transition from IN_REVIEW to " + newStatus + ". Valid transitions: COMPLETED, CANCELLED, IN_PROGRESS, TODO");
                }
                break;
            case COMPLETED:
                // Can reopen completed tasks
                if (newStatus == TaskStatus.CANCELLED) {
                    throw new BadRequestException("Cannot cancel a completed task. Valid transitions: TODO, IN_PROGRESS, IN_REVIEW");
                }
                break;
            case CANCELLED:
                // Can reopen cancelled tasks
                if (newStatus == TaskStatus.COMPLETED) {
                    throw new BadRequestException("Cannot complete a cancelled task. Valid transitions: TODO, IN_PROGRESS, IN_REVIEW");
                }
                break;
        }
    }

    /**
     * Validate basic task fields
     */
    private void validateBasicFields(TaskRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Task title cannot be empty");
        }

        if (request.getTitle().length() < 3) {
            throw new BadRequestException("Task title must be at least 3 characters long");
        }

        if (request.getTitle().length() > 200) {
            throw new BadRequestException("Task title cannot exceed 200 characters");
        }

        if (request.getDescription() != null && request.getDescription().length() > 5000) {
            throw new BadRequestException("Task description cannot exceed 5000 characters");
        }

        if (request.getStatus() == null) {
            throw new BadRequestException("Task status is required");
        }

        if (request.getPriority() == null) {
            throw new BadRequestException("Task priority is required");
        }
    }

    /**
     * Validate due date
     */
    private void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now().minusDays(1))) {
            throw new BadRequestException("Due date cannot be in the past");
        }
    }

    /**
     * Validate tags format
     */
    private void validateTags(String tags) {
        if (tags != null && !tags.trim().isEmpty()) {
            if (tags.length() > 500) {
                throw new BadRequestException("Tags cannot exceed 500 characters");
            }

            // Validate tag format (comma-separated, alphanumeric with hyphens)
            String[] tagArray = tags.split(",");
            for (String tag : tagArray) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty() && !trimmedTag.matches("^[a-zA-Z0-9\\-_]+$")) {
                    throw new BadRequestException(
                            "Invalid tag format: '" + trimmedTag + "'. Tags must contain only letters, numbers, hyphens, and underscores");
                }
            }
        }
    }

    /**
     * Validate that a task can be assigned
     */
    public void validateTaskAssignment(TaskStatus taskStatus) {
        if (taskStatus == TaskStatus.COMPLETED) {
            throw new BadRequestException("Cannot reassign a completed task");
        }
        if (taskStatus == TaskStatus.CANCELLED) {
            throw new BadRequestException("Cannot assign a cancelled task");
        }
    }

    /**
     * Validate task deletion
     */
    public void validateTaskDeletion(TaskStatus taskStatus) {
        // Allow deletion of any task, but you can add restrictions if needed
        // For example, prevent deletion of completed tasks
        // if (taskStatus == TaskStatus.COMPLETED) {
        //     throw new BadRequestException("Cannot delete a completed task");
        // }
    }
}
