package com.hadassahhub.backend.dto;

public record AuthResponseDTO(
        String token,
        UserDTO user
) {}