package com.hadassahhub.backend.exception;

/**
 * Exception thrown when file download operations fail.
 * This includes file not found errors and I/O errors during file retrieval.
 */
public class FileDownloadException extends RuntimeException {
    
    public FileDownloadException(String message) {
        super(message);
    }
    
    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
