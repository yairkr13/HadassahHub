package com.hadassahhub.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for file upload functionality.
 * Binds to application.properties with prefix "file.upload".
 */
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    
    /**
     * Directory where uploaded files will be stored.
     * Default: ./uploads/resources/
     */
    private String uploadDir = "./uploads/resources/";
    
    /**
     * Maximum file size in bytes.
     * Default: 10485760 bytes (10MB)
     */
    private long maxFileSize = 10485760L;
    
    /**
     * List of allowed MIME types for file uploads.
     * Default: PDF, PNG, JPEG, DOCX, TXT
     */
    private List<String> allowedMimeTypes = new ArrayList<>(List.of(
        "application/pdf",
        "image/png",
        "image/jpeg",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain"
    ));
    
    // Getters and setters
    
    public String getUploadDir() {
        return uploadDir;
    }
    
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public List<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }
    
    public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }
}
