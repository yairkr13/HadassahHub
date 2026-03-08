package com.hadassahhub.backend.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 */
public class UnauthorizedResourceAccessException extends RuntimeException {
    
    public UnauthorizedResourceAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedResourceAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnauthorizedResourceAccessException(Long resourceId, Long userId) {
        super("User " + userId + " is not authorized to access resource " + resourceId);
    }
}