package com.hadassahhub.backend.exception;

/**
 * Exception thrown when file storage operations fail.
 * This includes I/O errors, directory creation failures, and other file system issues.
 */
public class FileStorageException extends RuntimeException {
    
    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
