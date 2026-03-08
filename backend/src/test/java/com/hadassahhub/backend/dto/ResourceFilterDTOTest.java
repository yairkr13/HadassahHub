package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ResourceFilterDTOTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testFullResourceFilterDTO() {
        ResourceFilterDTO dto = new ResourceFilterDTO(
            1L, ResourceType.EXAM, ResourceStatus.APPROVED, "2024-2025", "Moed A",
            "final exam", 100L, 0, 20, "createdAt", "desc"
        );
        
        Set<ConstraintViolation<ResourceFilterDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        
        assertEquals(1L, dto.courseId());
        assertEquals(ResourceType.EXAM, dto.type());
        assertEquals(ResourceStatus.APPROVED, dto.status());
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
        assertEquals("final exam", dto.titleSearch());
        assertEquals(100L, dto.uploaderId());
        assertEquals(0, dto.page());
        assertEquals(20, dto.size());
        assertEquals("createdAt", dto.sortBy());
        assertEquals("desc", dto.sortDirection());
    }
    
    @Test
    void testNormalization() {
        ResourceFilterDTO dto = new ResourceFilterDTO(
            1L, ResourceType.EXAM, ResourceStatus.APPROVED, "  2024-2025  ", "  Moed A  ",
            "  final exam  ", 100L, -1, 0, "  title  ", "  asc  "
        );
        
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
        assertEquals("final exam", dto.titleSearch());
        assertEquals(0, dto.page()); // Negative page normalized to 0
        assertEquals(20, dto.size()); // Zero size normalized to default 20
        assertEquals("title", dto.sortBy());
        assertEquals("asc", dto.sortDirection());
    }
    
    @Test
    void testEmptyStringNormalization() {
        ResourceFilterDTO dto = new ResourceFilterDTO(
            1L, ResourceType.EXAM, ResourceStatus.APPROVED, "   ", "   ",
            "   ", 100L, null, null, "   ", "   "
        );
        
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
        assertNull(dto.titleSearch());
        assertEquals(0, dto.page()); // null normalized to 0
        assertEquals(20, dto.size()); // null normalized to 20
        assertEquals("createdAt", dto.sortBy()); // empty normalized to default
        assertEquals("desc", dto.sortDirection()); // empty normalized to default
    }
    
    @Test
    void testSizeBounds() {
        // Test size too large
        ResourceFilterDTO dto1 = new ResourceFilterDTO(
            1L, null, null, null, null, null, null, null, 200, null, null
        );
        assertEquals(100, dto1.size()); // Capped at 100
        
        // Test negative size
        ResourceFilterDTO dto2 = new ResourceFilterDTO(
            1L, null, null, null, null, null, null, null, -5, null, null
        );
        assertEquals(20, dto2.size()); // Normalized to default
    }
    
    @Test
    void testAcademicYearTooLong() {
        String longYear = "a".repeat(51);
        ResourceFilterDTO dto = new ResourceFilterDTO(
            1L, null, null, longYear, null, null, null, null, null, null, null
        );
        
        Set<ConstraintViolation<ResourceFilterDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Academic year filter must not exceed 50 characters")));
    }
    
    @Test
    void testExamTermTooLong() {
        String longTerm = "a".repeat(51);
        ResourceFilterDTO dto = new ResourceFilterDTO(
            1L, null, null, null, longTerm, null, null, null, null, null, null
        );
        
        Set<ConstraintViolation<ResourceFilterDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Exam term filter must not exceed 50 characters")));
    }
    
    @Test
    void testTitleSearchTooLong() {
        String longSearch = "a".repeat(101);
        ResourceFilterDTO dto = new ResourceFilterDTO(
            1L, null, null, null, null, longSearch, null, null, null, null, null
        );
        
        Set<ConstraintViolation<ResourceFilterDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title search must not exceed 100 characters")));
    }
    
    @Test
    void testForCourseResources() {
        ResourceFilterDTO dto = ResourceFilterDTO.forCourseResources(1L, ResourceType.EXAM, "2024-2025");
        
        assertEquals(1L, dto.courseId());
        assertEquals(ResourceType.EXAM, dto.type());
        assertEquals(ResourceStatus.APPROVED, dto.status());
        assertEquals("2024-2025", dto.academicYear());
        assertNull(dto.examTerm());
        assertNull(dto.titleSearch());
        assertNull(dto.uploaderId());
    }
    
    @Test
    void testForPendingModeration() {
        ResourceFilterDTO dto = ResourceFilterDTO.forPendingModeration(1L, ResourceType.HOMEWORK);
        
        assertEquals(1L, dto.courseId());
        assertEquals(ResourceType.HOMEWORK, dto.type());
        assertEquals(ResourceStatus.PENDING, dto.status());
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
        assertNull(dto.titleSearch());
        assertNull(dto.uploaderId());
    }
    
    @Test
    void testForUserResources() {
        ResourceFilterDTO dto = ResourceFilterDTO.forUserResources(100L, ResourceStatus.REJECTED);
        
        assertNull(dto.courseId());
        assertNull(dto.type());
        assertEquals(ResourceStatus.REJECTED, dto.status());
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
        assertNull(dto.titleSearch());
        assertEquals(100L, dto.uploaderId());
    }
    
    @Test
    void testWithTitleSearch() {
        ResourceFilterDTO dto = ResourceFilterDTO.withTitleSearch(1L, "exam");
        
        assertEquals(1L, dto.courseId());
        assertNull(dto.type());
        assertEquals(ResourceStatus.APPROVED, dto.status());
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
        assertEquals("exam", dto.titleSearch());
        assertNull(dto.uploaderId());
    }
    
    @Test
    void testWithPagination() {
        ResourceFilterDTO dto = ResourceFilterDTO.withPagination(1L, 2, 50);
        
        assertEquals(1L, dto.courseId());
        assertNull(dto.type());
        assertEquals(ResourceStatus.APPROVED, dto.status());
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
        assertNull(dto.titleSearch());
        assertNull(dto.uploaderId());
        assertEquals(2, dto.page());
        assertEquals(50, dto.size());
    }
    
    @Test
    void testHasFilters() {
        ResourceFilterDTO noFilters = new ResourceFilterDTO(
            null, null, null, null, null, null, null, null, null, null, null
        );
        assertFalse(noFilters.hasFilters());
        
        ResourceFilterDTO withCourseFilter = new ResourceFilterDTO(
            1L, null, null, null, null, null, null, null, null, null, null
        );
        assertTrue(withCourseFilter.hasFilters());
        
        ResourceFilterDTO withTypeFilter = new ResourceFilterDTO(
            null, ResourceType.EXAM, null, null, null, null, null, null, null, null, null
        );
        assertTrue(withTypeFilter.hasFilters());
        
        ResourceFilterDTO withStatusFilter = new ResourceFilterDTO(
            null, null, ResourceStatus.APPROVED, null, null, null, null, null, null, null, null
        );
        assertTrue(withStatusFilter.hasFilters());
    }
    
    @Test
    void testIsSearch() {
        ResourceFilterDTO noSearch = new ResourceFilterDTO(
            1L, null, null, null, null, null, null, null, null, null, null
        );
        assertFalse(noSearch.isSearch());
        
        ResourceFilterDTO withSearch = new ResourceFilterDTO(
            1L, null, null, null, null, "exam", null, null, null, null, null
        );
        assertTrue(withSearch.isSearch());
        
        ResourceFilterDTO emptySearch = new ResourceFilterDTO(
            1L, null, null, null, null, "", null, null, null, null, null
        );
        assertFalse(emptySearch.isSearch()); // Empty string normalized to null
    }
    
    @Test
    void testGetEffectiveSize() {
        ResourceFilterDTO dto1 = new ResourceFilterDTO(
            null, null, null, null, null, null, null, null, 50, null, null
        );
        assertEquals(50, dto1.getEffectiveSize());
        
        ResourceFilterDTO dto2 = new ResourceFilterDTO(
            null, null, null, null, null, null, null, null, 200, null, null
        );
        assertEquals(100, dto2.getEffectiveSize()); // Capped at 100
        
        ResourceFilterDTO dto3 = new ResourceFilterDTO(
            null, null, null, null, null, null, null, null, 0, null, null
        );
        assertEquals(20, dto3.getEffectiveSize()); // 0 normalized to default 20 in constructor, getEffectiveSize returns that
    }
    
    @Test
    void testGetEffectivePage() {
        ResourceFilterDTO dto1 = new ResourceFilterDTO(
            null, null, null, null, null, null, null, 5, null, null, null
        );
        assertEquals(5, dto1.getEffectivePage());
        
        ResourceFilterDTO dto2 = new ResourceFilterDTO(
            null, null, null, null, null, null, null, -1, null, null, null
        );
        assertEquals(0, dto2.getEffectivePage()); // Minimum 0
    }
}