package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;

import java.time.LocalDateTime;

/**
 * DTO for Resource entity responses.
 * Contains all resource information needed for course details page and resource management.
 */
public record ResourceDTO(
    Long id,
    String title,
    ResourceType type,
    String url,
    String academicYear,
    String examTerm,
    ResourceStatus status,
    String rejectionReason,
    Long courseId,
    String courseName,
    Long uploadedById,
    String uploaderName,
    Long approvedById,
    String approverName,
    LocalDateTime approvedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    // File upload fields
    Boolean isFileUpload,
    String fileName,
    Long fileSize,
    String mimeType,
    // Visibility and permission flags
    Boolean isOwner,
    Boolean canModerate
) {
    
    /**
     * Creates a simplified ResourceDTO for public course pages.
     * Only includes approved resources with minimal information.
     */
    public static ResourceDTO forPublicView(
            Long id,
            String title,
            ResourceType type,
            String url,
            String academicYear,
            String examTerm,
            String uploaderName,
            LocalDateTime createdAt,
            Boolean isFileUpload,
            String fileName,
            Long fileSize,
            String mimeType,
            Boolean isOwner,
            Boolean canModerate) {
        return new ResourceDTO(
            id, title, type, url, academicYear, examTerm,
            ResourceStatus.APPROVED, null, null, null,
            null, uploaderName, null, null, null,
            createdAt, null,
            isFileUpload, fileName, fileSize, mimeType,
            isOwner, canModerate
        );
    }
    
    /**
     * Creates a ResourceDTO for user's own resources.
     * Includes status and rejection reason for resource management.
     */
    public static ResourceDTO forOwnerView(
            Long id,
            String title,
            ResourceType type,
            String url,
            String academicYear,
            String examTerm,
            ResourceStatus status,
            String rejectionReason,
            String courseName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Boolean isFileUpload,
            String fileName,
            Long fileSize,
            String mimeType,
            Boolean isOwner,
            Boolean canModerate) {
        return new ResourceDTO(
            id, title, type, url, academicYear, examTerm,
            status, rejectionReason, null, courseName,
            null, null, null, null, null,
            createdAt, updatedAt,
            isFileUpload, fileName, fileSize, mimeType,
            isOwner, canModerate
        );
    }
    
    /**
     * Creates a ResourceDTO for admin moderation.
     * Includes all information needed for approval/rejection decisions.
     */
    public static ResourceDTO forAdminView(
            Long id,
            String title,
            ResourceType type,
            String url,
            String academicYear,
            String examTerm,
            ResourceStatus status,
            String rejectionReason,
            Long courseId,
            String courseName,
            Long uploadedById,
            String uploaderName,
            Long approvedById,
            String approverName,
            LocalDateTime approvedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Boolean isFileUpload,
            String fileName,
            Long fileSize,
            String mimeType,
            Boolean isOwner,
            Boolean canModerate) {
        return new ResourceDTO(
            id, title, type, url, academicYear, examTerm,
            status, rejectionReason, courseId, courseName,
            uploadedById, uploaderName, approvedById, approverName, approvedAt,
            createdAt, updatedAt,
            isFileUpload, fileName, fileSize, mimeType,
            isOwner, canModerate
        );
    }
    
    // Convenience methods for status checking
    public boolean isPending() {
        return status == ResourceStatus.PENDING;
    }
    
    public boolean isApproved() {
        return status == ResourceStatus.APPROVED;
    }
    
    public boolean isRejected() {
        return status == ResourceStatus.REJECTED;
    }
    
    public boolean isFileResource() {
        return Boolean.TRUE.equals(isFileUpload);
    }
}