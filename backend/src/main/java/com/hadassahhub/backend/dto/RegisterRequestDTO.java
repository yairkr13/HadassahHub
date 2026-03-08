package com.hadassahhub.backend.dto;

public record RegisterRequestDTO(
        String email,
        String password,
        String displayName
) {}