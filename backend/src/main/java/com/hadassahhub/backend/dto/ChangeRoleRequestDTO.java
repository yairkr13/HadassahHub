package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for changing a user's role.
 * Contains the new role and optional reason.
 */
public record ChangeRoleRequestDTO(
    @NotNull(message = "New role is required")
    UserRole newRole,
    
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    String reason
) {
}
