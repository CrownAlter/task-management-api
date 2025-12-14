package com.adewunmi.task_management_api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active;
    private Boolean emailVerified;
    private Set<String> roles;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

}
