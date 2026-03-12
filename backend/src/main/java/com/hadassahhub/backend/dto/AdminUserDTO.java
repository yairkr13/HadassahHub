package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * DTO for user list in admin user management.
 * Contains basic user information for table display.
 */
public record AdminUserDTO(
    Long id,
    String fullName,
    String email,
    UserRole role,
    UserStatus status,
    LocalDateTime registrationDate,
    LocalDateTime lastLogin,
    Integer resourcesUploaded,
    Boolean emailVerified
) {
}
