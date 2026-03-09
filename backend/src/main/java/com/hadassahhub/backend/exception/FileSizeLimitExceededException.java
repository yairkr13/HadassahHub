package com.hadassahhub.backend.exception;

/**
 * Exception thrown when an uploaded file exceeds the maximum allowed size.
 */
public class FileSizeLimitExceededException extends RuntimeException {
    
    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}
