package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResourceStatsDTOTest {
    
    @Test
    void testFullResourceStatsDTO() {
        Map<String, Long> yearStats = Map.of("2024-2025", 5L, "2023-2024", 3L);
        Map<ResourceType, Long> typeStats = Map.of(
            ResourceType.EXAM, 3L,
            ResourceType.HOMEWORK, 2L,
            ResourceType.SUMMARY, 2L,
            ResourceType.LINK, 1L
        );
        
        ResourceStatsDTO dto = new ResourceStatsDTO(
            1L, "Computer Science", 8L, 3L, 2L, 2L, 1L, 2L,
            yearStats, typeStats
        );
        
        assertEquals(1L, dto.courseId());
        assertEquals("Computer Science", dto.courseName());
        assertEquals(8L, dto.totalResources());
        assertEquals(3L, dto.examCount());
        assertEquals(2L, dto.homeworkCount());
        assertEquals(2L, dto.summaryCount());
        assertEquals(1L, dto.linkCount());
        assertEquals(2L, dto.pendingCount());
        assertEquals(yearStats, dto.resourcesByAcademicYear());
        assertEquals(typeStats, dto.resourcesByType());
    }
    
    @Test
    void testEmptyStats() {
        ResourceStatsDTO dto = ResourceStatsDTO.empty(1L, "Computer Science");
        
        assertEquals(1L, dto.courseId());
        assertEquals("Computer Science", dto.courseName());
        assertEquals(0L, dto.totalResources());
        assertEquals(0L, dto.examCount());
        assertEquals(0L, dto.homeworkCount());
        assertEquals(0L, dto.summaryCount());
        assertEquals(0L, dto.linkCount());
        assertEquals(0L, dto.pendingCount());
        assertTrue(dto.resourcesByAcademicYear().isEmpty());
        assertTrue(dto.resourcesByType().isEmpty());
    }
    
    @Test
    void testBasicStats() {
        ResourceStatsDTO dto = ResourceStatsDTO.basic(
            1L, "Computer Science", 10L, 4L, 3L, 2L, 1L
        );
        
        assertEquals(1L, dto.courseId());
        assertEquals("Computer Science", dto.courseName());
        assertEquals(10L, dto.totalResources());
        assertEquals(4L, dto.examCount());
        assertEquals(3L, dto.homeworkCount());
        assertEquals(2L, dto.summaryCount());
        assertEquals(1L, dto.linkCount());
        assertEquals(0L, dto.pendingCount());
        assertTrue(dto.resourcesByAcademicYear().isEmpty());
        assertTrue(dto.resourcesByType().isEmpty());
    }
    
    @Test
    void testHasResources() {
        ResourceStatsDTO emptyStats = ResourceStatsDTO.empty(1L, "Course");
        assertFalse(emptyStats.hasResources());
        
        ResourceStatsDTO withResources = ResourceStatsDTO.basic(1L, "Course", 5L, 2L, 1L, 1L, 1L);
        assertTrue(withResources.hasResources());
    }
    
    @Test
    void testHasResourcesOfType() {
        ResourceStatsDTO dto = ResourceStatsDTO.basic(1L, "Course", 5L, 2L, 1L, 0L, 1L);
        
        assertTrue(dto.hasResourcesOfType(ResourceType.EXAM));
        assertTrue(dto.hasResourcesOfType(ResourceType.HOMEWORK));
        assertFalse(dto.hasResourcesOfType(ResourceType.SUMMARY));
        assertTrue(dto.hasResourcesOfType(ResourceType.LINK));
    }
    
    @Test
    void testGetCountForType() {
        ResourceStatsDTO dto = ResourceStatsDTO.basic(1L, "Course", 8L, 3L, 2L, 2L, 1L);
        
        assertEquals(3L, dto.getCountForType(ResourceType.EXAM));
        assertEquals(2L, dto.getCountForType(ResourceType.HOMEWORK));
        assertEquals(2L, dto.getCountForType(ResourceType.SUMMARY));
        assertEquals(1L, dto.getCountForType(ResourceType.LINK));
    }
    
    @Test
    void testHasPendingResources() {
        ResourceStatsDTO noPending = ResourceStatsDTO.basic(1L, "Course", 5L, 2L, 1L, 1L, 1L);
        assertFalse(noPending.hasPendingResources());
        
        ResourceStatsDTO withPending = new ResourceStatsDTO(
            1L, "Course", 5L, 2L, 1L, 1L, 1L, 3L, Map.of(), Map.of()
        );
        assertTrue(withPending.hasPendingResources());
    }
}