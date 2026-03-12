package com.hadassahhub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for blocking a user.
 * Contains the reason for blocking.
 */
public record BlockUserRequestDTO(
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    String reason
) {
}
