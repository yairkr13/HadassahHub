package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;

/**
 * DTO for filtering users in admin user management.
 */
public record UserFilterDTO(
    String search,      // Search by name or email
    UserRole role,      // Filter by role
    UserStatus status   // Filter by status
) {
    public UserFilterDTO {
        search = search != null && !search.trim().isEmpty() ? search.trim() : null;
    }
    
    public boolean hasFilters() {
        return search != null || role != null || status != null;
    }
}
