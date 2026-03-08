package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceType;

import java.util.Map;

/**
 * DTO for resource statistics on course pages.
 * Provides aggregated information about resources for a course.
 */
public record ResourceStatsDTO(
    Long courseId,
    String courseName,
    long totalResources,
    long examCount,
    long homeworkCount,
    long summaryCount,
    long linkCount,
    long pendingCount,
    Map<String, Long> resourcesByAcademicYear,
    Map<ResourceType, Long> resourcesByType
) {
    
    /**
     * Creates empty stats for a course with no resources.
     */
    public static ResourceStatsDTO empty(Long courseId, String courseName) {
        return new ResourceStatsDTO(
            courseId, courseName, 0L, 0L, 0L, 0L, 0L, 0L,
            Map.of(), Map.of()
        );
    }
    
    /**
     * Creates basic stats without detailed breakdowns.
     */
    public static ResourceStatsDTO basic(
            Long courseId, 
            String courseName, 
            long totalResources, 
            long examCount, 
            long homeworkCount, 
            long summaryCount, 
            long linkCount) {
        return new ResourceStatsDTO(
            courseId, courseName, totalResources, 
            examCount, homeworkCount, summaryCount, linkCount, 0L,
            Map.of(), Map.of()
        );
    }
    
    /**
     * Checks if the course has any resources.
     */
    public boolean hasResources() {
        return totalResources > 0;
    }
    
    /**
     * Checks if the course has resources of a specific type.
     */
    public boolean hasResourcesOfType(ResourceType type) {
        return switch (type) {
            case EXAM -> examCount > 0;
            case HOMEWORK -> homeworkCount > 0;
            case SUMMARY -> summaryCount > 0;
            case LINK -> linkCount > 0;
        };
    }
    
    /**
     * Gets the count for a specific resource type.
     */
    public long getCountForType(ResourceType type) {
        return switch (type) {
            case EXAM -> examCount;
            case HOMEWORK -> homeworkCount;
            case SUMMARY -> summaryCount;
            case LINK -> linkCount;
        };
    }
    
    /**
     * Checks if there are pending resources awaiting approval.
     */
    public boolean hasPendingResources() {
        return pendingCount > 0;
    }
}