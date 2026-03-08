package com.hadassahhub.backend.exception;

/**
 * Exception thrown when URL validation fails.
 */
public class InvalidUrlException extends RuntimeException {
    
    public InvalidUrlException(String message) {
        super(message);
    }
    
    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidUrlException(String url, String reason) {
        super("Invalid URL '" + url + "': " + reason);
    }
}