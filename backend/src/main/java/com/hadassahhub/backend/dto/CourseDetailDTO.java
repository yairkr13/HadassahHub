package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;

import java.util.List;

/**
 * Enhanced DTO for course details page with integrated resource information.
 * Provides comprehensive course information including resource statistics and recent resources.
 */
public record CourseDetailDTO(
    Long id,
    String name,
    String description,
    CourseCategory category,
    Integer credits,
    StudyYear recommendedYear,
    ResourceStatsDTO resourceStats,
    List<ResourceDTO> recentResources,
    int totalResourcesCount,
    boolean hasResources
) {
    
    /**
     * Creates a CourseDetailDTO from basic course info and resource data.
     */
    public static CourseDetailDTO from(
            CourseDTO course, 
            ResourceStatsDTO resourceStats, 
            List<ResourceDTO> recentResources) {
        
        return new CourseDetailDTO(
            course.id(),
            course.name(),
            course.description(),
            course.category(),
            course.credits(),
            course.recommendedYear(),
            resourceStats,
            recentResources,
            (int) resourceStats.totalResources(),
            resourceStats.hasResources()
        );
    }
    
    /**
     * Creates a CourseDetailDTO with empty resource information.
     */
    public static CourseDetailDTO withoutResources(CourseDTO course) {
        return new CourseDetailDTO(
            course.id(),
            course.name(),
            course.description(),
            course.category(),
            course.credits(),
            course.recommendedYear(),
            ResourceStatsDTO.empty(course.id(), course.name()),
            List.of(),
            0,
            false
        );
    }
    
    /**
     * Gets the number of recent resources shown.
     */
    public int getRecentResourcesCount() {
        return recentResources.size();
    }
    
    /**
     * Checks if the course has resources of a specific type.
     */
    public boolean hasResourcesOfType(com.hadassahhub.backend.enums.ResourceType type) {
        return resourceStats.hasResourcesOfType(type);
    }
    
    /**
     * Gets the count of resources for a specific type.
     */
    public long getResourceCountForType(com.hadassahhub.backend.enums.ResourceType type) {
        return resourceStats.getCountForType(type);
    }
}