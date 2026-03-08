package com.hadassahhub.backend.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String message,
        LocalDateTime timestamp
) {}