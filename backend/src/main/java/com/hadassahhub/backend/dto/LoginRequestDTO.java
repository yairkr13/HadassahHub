package com.hadassahhub.backend.dto;

public record LoginRequestDTO(
        String email,
        String password
) {}