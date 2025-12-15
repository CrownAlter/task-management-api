package com.adewunmi.task_management_api.validation;

import com.adewunmi.task_management_api.enums.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskStatusValidator implements ConstraintValidator<ValidTaskStatus, TaskStatus> {

    @Override
    public void initialize(ValidTaskStatus constraintAnnotation) {
    }

    @Override
    public boolean isValid(TaskStatus status, ConstraintValidatorContext context) {
        if (status == null) {
            return true; // Use @NotNull for null validation
        }
        
        // Additional custom validation logic can be added here
        return true;
    }
}
