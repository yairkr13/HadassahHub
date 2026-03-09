package com.hadassahhub.backend.exception;

/**
 * Exception thrown when an uploaded file has an unsupported MIME type.
 */
public class InvalidFileTypeException extends RuntimeException {
    
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
