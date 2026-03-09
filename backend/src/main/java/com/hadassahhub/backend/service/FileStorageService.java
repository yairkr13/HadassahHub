package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.FileMetadata;
import com.hadassahhub.backend.exception.FileDownloadException;
import com.hadassahhub.backend.exception.FileStorageException;
import com.hadassahhub.backend.exception.FileSizeLimitExceededException;
import com.hadassahhub.backend.exception.InvalidFileTypeException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for file storage operations.
 * Handles file upload, validation, storage, retrieval, and deletion.
 */
public interface FileStorageService {
    
    /**
     * Stores an uploaded file and returns metadata.
     * Validates MIME type and file size before storage.
     * Generates a unique UUID-based filename to prevent collisions.
     * 
     * @param file The multipart file to store
     * @return FileMetadata containing stored file information
     * @throws InvalidFileTypeException if MIME type is not allowed
     * @throws FileSizeLimitExceededException if file exceeds size limit
     * @throws FileStorageException if I/O error occurs during storage
     */
    FileMetadata storeFile(MultipartFile file);
    
    /**
     * Loads a file as a Spring Resource for download.
     * Validates path safety before loading.
     * 
     * @param filePath The stored file path (relative to upload directory)
     * @return Resource containing file content
     * @throws FileDownloadException if file not found or I/O error occurs
     */
    Resource loadFileAsResource(String filePath);
    
    /**
     * Deletes a physical file from storage.
     * Logs errors but does not throw exceptions (graceful degradation).
     * 
     * @param filePath The stored file path (relative to upload directory)
     */
    void deleteFile(String filePath);
    
    /**
     * Validates MIME type against whitelist.
     * Uses content-based detection, not just file extension.
     * 
     * @param file The file to validate
     * @return true if MIME type is allowed, false otherwise
     */
    boolean isValidMimeType(MultipartFile file);
    
    /**
     * Validates file size against configured limit.
     * 
     * @param file The file to validate
     * @return true if size is within limit, false otherwise
     */
    boolean isValidFileSize(MultipartFile file);
}
