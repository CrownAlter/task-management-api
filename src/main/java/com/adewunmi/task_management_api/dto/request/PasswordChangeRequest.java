package com.adewunmi.task_management_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for changing user password")
public class PasswordChangeRequest {

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password", example = "OldPassword123!")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "New password", example = "NewPassword123!")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Confirm new password", example = "NewPassword123!")
    private String confirmPassword;
}
