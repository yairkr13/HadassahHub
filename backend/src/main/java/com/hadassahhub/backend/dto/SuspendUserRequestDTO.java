package com.hadassahhub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for suspending a user.
 * Contains the reason and expiration date.
 */
public record SuspendUserRequestDTO(
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    String reason,
    
    @NotNull(message = "Expiration date is required")
    String expiresAt  // ISO date-time string (e.g., "2024-03-20T10:00:00")
) {
}
