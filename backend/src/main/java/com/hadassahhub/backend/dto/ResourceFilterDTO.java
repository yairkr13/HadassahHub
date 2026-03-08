package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import jakarta.validation.constraints.Size;

/**
 * DTO for filtering resources in search and listing operations.
 * Used for course page filtering and admin moderation interfaces.
 */
public record ResourceFilterDTO(
    
    Long courseId,
    
    ResourceType type,
    
    ResourceStatus status,
    
    @Size(max = 50, message = "Academic year filter must not exceed 50 characters")
    String academicYear,
    
    @Size(max = 50, message = "Exam term filter must not exceed 50 characters")
    String examTerm,
    
    @Size(max = 100, message = "Title search must not exceed 100 characters")
    String titleSearch,
    
    Long uploaderId,
    
    // Pagination parameters
    Integer page,
    Integer size,
    String sortBy,
    String sortDirection
) {
    
    /**
     * Creates a ResourceFilterDTO with normalized values.
     */
    public ResourceFilterDTO {
        academicYear = academicYear != null && !academicYear.trim().isEmpty() ? academicYear.trim() : null;
        examTerm = examTerm != null && !examTerm.trim().isEmpty() ? examTerm.trim() : null;
        titleSearch = titleSearch != null && !titleSearch.trim().isEmpty() ? titleSearch.trim() : null;
        sortBy = sortBy != null && !sortBy.trim().isEmpty() ? sortBy.trim() : "createdAt";
        sortDirection = sortDirection != null && !sortDirection.trim().isEmpty() ? sortDirection.trim() : "desc";
        page = page != null && page >= 0 ? page : 0;
        size = size != null && size > 0 ? (size > 100 ? 100 : size) : 20;
    }
    
    /**
     * Creates a filter for approved resources on course pages.
     */
    public static ResourceFilterDTO forCourseResources(Long courseId, ResourceType type, String academicYear) {
        return new ResourceFilterDTO(
            courseId, type, ResourceStatus.APPROVED, academicYear, null, null, null,
            null, null, null, null
        );
    }
    
    /**
     * Creates a filter for pending resources in admin moderation.
     */
    public static ResourceFilterDTO forPendingModeration(Long courseId, ResourceType type) {
        return new ResourceFilterDTO(
            courseId, type, ResourceStatus.PENDING, null, null, null, null,
            null, null, null, null
        );
    }
    
    /**
     * Creates a filter for user's own resources.
     */
    public static ResourceFilterDTO forUserResources(Long uploaderId, ResourceStatus status) {
        return new ResourceFilterDTO(
            null, null, status, null, null, null, uploaderId,
            null, null, null, null
        );
    }
    
    /**
     * Creates a search filter with title search.
     */
    public static ResourceFilterDTO withTitleSearch(Long courseId, String titleSearch) {
        return new ResourceFilterDTO(
            courseId, null, ResourceStatus.APPROVED, null, null, titleSearch, null,
            null, null, null, null
        );
    }
    
    /**
     * Creates a filter with pagination.
     */
    public static ResourceFilterDTO withPagination(Long courseId, int page, int size) {
        return new ResourceFilterDTO(
            courseId, null, ResourceStatus.APPROVED, null, null, null, null,
            page, size, null, null
        );
    }
    
    /**
     * Checks if any filters are applied.
     */
    public boolean hasFilters() {
        return courseId != null || type != null || status != null || 
               academicYear != null || examTerm != null || titleSearch != null || uploaderId != null;
    }
    
    /**
     * Checks if this is a search operation (has title search).
     */
    public boolean isSearch() {
        return titleSearch != null && !titleSearch.isEmpty();
    }
    
    /**
     * Gets the effective page size with bounds checking.
     */
    public int getEffectiveSize() {
        return Math.min(Math.max(size, 1), 100);
    }
    
    /**
     * Gets the effective page number with bounds checking.
     */
    public int getEffectivePage() {
        return Math.max(page, 0);
    }
}