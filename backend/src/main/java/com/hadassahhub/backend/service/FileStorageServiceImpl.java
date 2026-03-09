package com.hadassahhub.backend.service;

import com.hadassahhub.backend.config.FileUploadProperties;
import com.hadassahhub.backend.dto.FileMetadata;
import com.hadassahhub.backend.exception.FileDownloadException;
import com.hadassahhub.backend.exception.FileStorageException;
import com.hadassahhub.backend.exception.FileSizeLimitExceededException;
import com.hadassahhub.backend.exception.InvalidFileTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementation of FileStorageService for local filesystem storage.
 * Handles file validation, secure storage, and retrieval operations.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    
    private final Path fileStorageLocation;
    private final FileUploadProperties fileUploadProperties;
    
    public FileStorageServiceImpl(FileUploadProperties fileUploadProperties) {
        this.fileUploadProperties = fileUploadProperties;
        this.fileStorageLocation = Paths.get(fileUploadProperties.getUploadDir())
                .toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("File storage directory initialized: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }
    
    @Override
    public FileMetadata storeFile(MultipartFile file) {
        // Validate file
        if (!isValidFileSize(file)) {
            throw new FileSizeLimitExceededException(
                "File size exceeds maximum limit of " + fileUploadProperties.getMaxFileSize() + " bytes"
            );
        }
        
        if (!isValidMimeType(file)) {
            throw new InvalidFileTypeException(
                "File type not allowed. Supported types: PDF, PNG, JPEG, DOCX, TXT"
            );
        }
        
        // Get original filename and sanitize
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        originalFilename = sanitizeFilename(originalFilename);
        
        // Generate unique stored filename
        String storedFilename = generateStoredFilename(originalFilename);
        
        try {
            // Validate path safety
            Path targetLocation = this.fileStorageLocation.resolve(storedFilename);
            validatePath(targetLocation);
            
            // Copy file to target location
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            logger.info("File stored successfully: originalName={}, storedName={}, size={}", 
                originalFilename, storedFilename, file.getSize());
            
            // Return metadata
            return new FileMetadata(
                originalFilename,
                storedFilename,
                storedFilename,  // Relative path
                file.getSize(),
                file.getContentType()
            );
            
        } catch (IOException ex) {
            logger.error("Failed to store file: originalName={}, error={}", originalFilename, ex.getMessage(), ex);
            throw new FileStorageException("Failed to store file: " + originalFilename, ex);
        }
    }
    
    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            validatePath(file);
            
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                logger.debug("File loaded successfully: {}", filePath);
                return resource;
            } else {
                throw new FileDownloadException("File not found: " + filePath);
            }
        } catch (MalformedURLException ex) {
            logger.error("Failed to load file: path={}, error={}", filePath, ex.getMessage(), ex);
            throw new FileDownloadException("Failed to load file: " + filePath, ex);
        }
    }
    
    @Override
    public void deleteFile(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            validatePath(file);
            
            Files.deleteIfExists(file);
            logger.info("File deleted successfully: {}", filePath);
        } catch (IOException ex) {
            // Log error but don't throw exception (graceful degradation)
            logger.warn("Failed to delete file: path={}, error={}", filePath, ex.getMessage());
        }
    }
    
    @Override
    public boolean isValidMimeType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        return fileUploadProperties.getAllowedMimeTypes().contains(contentType);
    }
    
    @Override
    public boolean isValidFileSize(MultipartFile file) {
        return file.getSize() <= fileUploadProperties.getMaxFileSize();
    }
    
    /**
     * Sanitizes filename by removing path traversal characters and dangerous patterns.
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "file";
        }
        
        // Remove path traversal patterns
        String sanitized = filename.replaceAll("\\.\\./", "")
                                  .replaceAll("\\.\\\\", "")
                                  .replaceAll(":", "")
                                  .replaceAll("^/+", "")
                                  .replaceAll("^\\\\+", "");
        
        // If sanitization removed everything, use default
        if (sanitized.isEmpty()) {
            sanitized = "file";
        }
        
        return sanitized;
    }
    
    /**
     * Generates a unique filename using UUID to prevent collisions.
     */
    private String generateStoredFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        
        if (extension != null && !extension.isEmpty()) {
            return uuid + "." + extension;
        }
        return uuid;
    }
    
    /**
     * Extracts file extension from filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    /**
     * Validates that the resolved path is within the upload directory.
     * Prevents path traversal attacks.
     */
    private void validatePath(Path path) {
        if (!path.normalize().startsWith(this.fileStorageLocation)) {
            throw new FileStorageException("Cannot store or access file outside designated directory");
        }
    }
}
