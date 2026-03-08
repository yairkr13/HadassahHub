package com.hadassahhub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for rejecting resources in the admin moderation workflow.
 * Contains the rejection reason provided by the admin.
 */
public record RejectResourceRequestDTO(
    
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    String reason
) {
    
    /**
     * Creates a RejectResourceRequestDTO with normalized reason.
     * Trims whitespace from the reason.
     */
    public RejectResourceRequestDTO {
        reason = reason != null ? reason.trim() : null;
    }
    
    /**
     * Creates a rejection request with a predefined reason.
     */
    public static RejectResourceRequestDTO withReason(String reason) {
        return new RejectResourceRequestDTO(reason);
    }
    
    /**
     * Common rejection reasons for convenience.
     */
    public static final class CommonReasons {
        public static final String INAPPROPRIATE_CONTENT = "Content is not appropriate for academic use";
        public static final String BROKEN_LINK = "The provided URL is not accessible or broken";
        public static final String WRONG_COURSE = "Resource does not match the selected course";
        public static final String DUPLICATE_RESOURCE = "This resource has already been uploaded";
        public static final String POOR_QUALITY = "Resource quality does not meet standards";
        public static final String COPYRIGHT_VIOLATION = "Resource may violate copyright restrictions";
        public static final String SPAM_OR_IRRELEVANT = "Resource appears to be spam or irrelevant";
        
        private CommonReasons() {
            // Utility class
        }
    }
}