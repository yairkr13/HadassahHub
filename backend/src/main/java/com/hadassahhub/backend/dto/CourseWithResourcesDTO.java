package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;

/**
 * DTO for course with enhanced resource information.
 * Used for course details pages that include resource statistics.
 */
public record CourseWithResourcesDTO(
    Long id,
    String name,
    String description,
    CourseCategory category,
    Integer credits,
    StudyYear recommendedYear,
    ResourceStatsDTO resourceStats,
    int recentResourcesCount
) {
    
    /**
     * Checks if the course has any resources.
     */
    public boolean hasResources() {
        return resourceStats.hasResources();
    }
    
    /**
     * Gets the total number of approved resources.
     */
    public long getTotalResources() {
        return resourceStats.totalResources();
    }
}