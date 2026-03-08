package com.hadassahhub.backend.exception;

import com.hadassahhub.backend.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnumValue(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        
        String message;
        if (paramName.equals("category")) {
            message = "Category must be one of: CS_CORE, CS_ELECTIVE, GENERAL_ELECTIVE";
        } else if (paramName.equals("year")) {
            message = "Year must be one of: Y1, Y2, Y3";
        } else {
            message = "Invalid parameter value: " + invalidValue;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid " + paramName + " value",
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Course not found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();
        HttpStatus status;
        String error;

        // Handle authentication-specific errors
        if (message.contains("Email already exists")) {
            status = HttpStatus.CONFLICT;
            error = "Email already exists";
        } else if (message.contains("Invalid credentials")) {
            status = HttpStatus.UNAUTHORIZED;
            error = "Invalid credentials";
        } else if (message.contains("Email must be from") || message.contains("domain")) {
            status = HttpStatus.BAD_REQUEST;
            error = "Invalid email domain";
        } else if (message.contains("required")) {
            status = HttpStatus.BAD_REQUEST;
            error = "Validation error";
        } else {
            status = HttpStatus.BAD_REQUEST;
            error = "Invalid request";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                error,
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid credentials",
                "The provided credentials are invalid",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data integrity violation";
        
        // Check if it's a unique constraint violation (likely email)
        if (ex.getMessage() != null && ex.getMessage().contains("email")) {
            message = "Email already exists";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "Conflict",
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        // Handle JWT and other authentication runtime exceptions
        if (ex.getMessage() != null && (ex.getMessage().contains("JWT") || ex.getMessage().contains("token"))) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "Authentication error",
                    "Invalid or expired token",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Generic runtime exception handling
        ErrorResponse errorResponse = new ErrorResponse(
                "Internal server error",
                "An unexpected error occurred",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    // Resource-specific exception handlers
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Resource not found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(UnauthorizedResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedResourceAccessException(UnauthorizedResourceAccessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Unauthorized resource access",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid URL",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}