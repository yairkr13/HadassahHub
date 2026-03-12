package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * DTO for detailed user information in admin user management.
 * Contains full user details including activity statistics.
 */
public record AdminUserDetailDTO(
    Long id,
    String fullName,
    String email,
    UserRole role,
    UserStatus status,
    LocalDateTime registrationDate,
    LocalDateTime lastLogin,
    Boolean emailVerified,
    Integer resourcesUploaded,
    Integer resourcesApproved,
    Integer resourcesPending,
    Integer resourcesRejected,
    Integer totalDownloads,
    LocalDateTime lastActivity
) {
}
