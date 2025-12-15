package com.adewunmi.task_management_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskStatusValidator.class)
@Documented
public @interface ValidTaskStatus {
    String message() default "Invalid task status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
