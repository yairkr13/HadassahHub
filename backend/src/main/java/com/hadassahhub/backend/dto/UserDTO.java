package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.UserRole;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String email,
        String displayName,
        UserRole role,
        Integer pointsBalance,
        LocalDateTime createdAt
) {}