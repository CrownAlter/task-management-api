package com.adewunmi.task_management_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating user information")
public class UserUpdateRequest {

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "Set of role IDs to assign to the user", example = "[1, 2]")
    private Set<Long> roleIds;
}
