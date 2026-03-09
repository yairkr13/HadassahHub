package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating new resources.
 * Contains validation for URL-based resource creation in the MVP scope.
 */
public record CreateResourceRequestDTO(
    
    @NotNull(message = "Course ID is required")
    Long courseId,
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,
    
    @NotNull(message = "Resource type is required")
    ResourceType type,
    
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    @Pattern(
        regexp = "^https?://.*|^$", 
        message = "URL must be a valid HTTP or HTTPS URL"
    )
    String url,
    
    @Size(max = 50, message = "Academic year must not exceed 50 characters")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$|^$", 
        message = "Academic year must be in format YYYY-YYYY (e.g., 2024-2025) or empty"
    )
    String academicYear,
    
    @Size(max = 50, message = "Exam term must not exceed 50 characters")
    String examTerm
) {
    
    /**
     * Creates a CreateResourceRequestDTO with normalized values.
     * Trims whitespace and handles null/empty strings.
     */
    public CreateResourceRequestDTO {
        title = title != null ? title.trim() : null;
        url = url != null ? url.trim() : null;
        academicYear = academicYear != null && !academicYear.trim().isEmpty() ? academicYear.trim() : null;
        examTerm = examTerm != null && !examTerm.trim().isEmpty() ? examTerm.trim() : null;
    }
    
    /**
     * Validates that exam-specific fields are provided for exam resources.
     */
    public boolean isValidExamResource() {
        if (type == ResourceType.EXAM) {
            return academicYear != null && !academicYear.isEmpty();
        }
        return true;
    }
    
    /**
     * Validates that exactly one of url or file is provided (XOR logic).
     * 
     * @param hasFile true if a file is provided in the request
     * @return true if exactly one of url or file is provided
     */
    public boolean hasUrlOrFile(boolean hasFile) {
        boolean hasUrl = url != null && !url.trim().isEmpty();
        // XOR: exactly one must be true
        return hasUrl ^ hasFile;
    }
    
    /**
     * Creates a simple resource request for testing.
     */
    public static CreateResourceRequestDTO simple(Long courseId, String title, ResourceType type, String url) {
        return new CreateResourceRequestDTO(courseId, title, type, url, null, null);
    }
    
    /**
     * Creates an exam resource request with academic year and term.
     */
    public static CreateResourceRequestDTO exam(Long courseId, String title, String url, String academicYear, String examTerm) {
        return new CreateResourceRequestDTO(courseId, title, ResourceType.EXAM, url, academicYear, examTerm);
    }
}