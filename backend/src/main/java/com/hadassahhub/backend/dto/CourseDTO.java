package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;

/**
 * Enhanced CourseDTO with resource statistics for course listings.
 */
public record CourseDTO(
        Long id,
        String name,
        String description,
        CourseCategory category,
        Integer credits,
        StudyYear recommendedYear, // null for electives
        ResourceStatsDTO resourceStats // null if not requested
) {
    
    /**
     * Creates a basic CourseDTO without resource statistics.
     */
    public static CourseDTO basic(
            Long id,
            String name,
            String description,
            CourseCategory category,
            Integer credits,
            StudyYear recommendedYear) {
        return new CourseDTO(id, name, description, category, credits, recommendedYear, null);
    }
    
    /**
     * Creates a CourseDTO with resource statistics.
     */
    public static CourseDTO withResources(
            Long id,
            String name,
            String description,
            CourseCategory category,
            Integer credits,
            StudyYear recommendedYear,
            ResourceStatsDTO resourceStats) {
        return new CourseDTO(id, name, description, category, credits, recommendedYear, resourceStats);
    }
    
    /**
     * Checks if this DTO includes resource statistics.
     */
    public boolean hasResourceStats() {
        return resourceStats != null;
    }
    
    /**
     * Gets the total number of resources (0 if no stats available).
     */
    public long getTotalResources() {
        return resourceStats != null ? resourceStats.totalResources() : 0;
    }
    
    /**
     * Checks if the course has any resources (false if no stats available).
     */
    public boolean hasResources() {
        return resourceStats != null && resourceStats.hasResources();
    }
}