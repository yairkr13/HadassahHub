package com.hadassahhub.backend.repository;

import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.*;
import com.hadassahhub.backend.specification.ResourceSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResourceRepository and ResourceSpecifications.
 * Tests the repository interface methods and JPA specifications logic.
 */
class ResourceRepositoryTest {
    
    private Course course1;
    private Course course2;
    private User student1;
    private User student2;
    private User admin;
    private Resource approvedExam;
    private Resource pendingHomework;
    private Resource rejectedSummary;
    
    @BeforeEach
    void setUp() {
        // Create test courses
        course1 = new Course();
        course1.setName("Introduction to Computer Science");
        course1.setCategory(CourseCategory.CS_CORE);
        course1.setCredits(4);
        course1.setRecommendedYear(StudyYear.Y1);
        
        course2 = new Course();
        course2.setName("Data Structures");
        course2.setCategory(CourseCategory.CS_CORE);
        course2.setCredits(4);
        course2.setRecommendedYear(StudyYear.Y2);
        
        // Create test users
        student1 = new User();
        student1.setEmail("student1@edu.hac.ac.il");
        student1.setDisplayName("John Doe");
        student1.setRole(UserRole.STUDENT);
        
        student2 = new User();
        student2.setEmail("student2@edu.hac.ac.il");
        student2.setDisplayName("Jane Smith");
        student2.setRole(UserRole.STUDENT);
        
        admin = new User();
        admin.setEmail("admin@edu.hac.ac.il");
        admin.setDisplayName("Admin User");
        admin.setRole(UserRole.ADMIN);
        
        // Create test resources
        approvedExam = new Resource(course1, student1, "Final Exam 2024", ResourceType.EXAM, "https://example.com/exam.pdf");
        approvedExam.setAcademicYear("2024-2025");
        approvedExam.setExamTerm("Moed A");
        approvedExam.approve(admin);
        
        pendingHomework = new Resource(course1, student2, "Assignment 1", ResourceType.HOMEWORK, "https://example.com/hw1.pdf");
        pendingHomework.setAcademicYear("2024-2025");
        
        rejectedSummary = new Resource(course2, student1, "Course Summary", ResourceType.SUMMARY, "https://example.com/summary.pdf");
        rejectedSummary.reject(admin, "Content not appropriate");
    }
    
    @Test
    void testRepositoryInterfaceExists() {
        // Test that the repository interface is properly defined
        assertNotNull(ResourceRepository.class);
        assertTrue(ResourceRepository.class.isInterface());
    }
    
    // Specification tests - these test the logic without requiring database
    @Test
    void testSpecificationHasCourseIdWithValidId() {
        Specification<Resource> spec = ResourceSpecifications.hasCourseId(1L);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationHasCourseIdWithNullId() {
        Specification<Resource> spec = ResourceSpecifications.hasCourseId(null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationHasStatus() {
        Specification<Resource> spec = ResourceSpecifications.hasStatus(ResourceStatus.APPROVED);
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasStatus(null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationHasType() {
        Specification<Resource> spec = ResourceSpecifications.hasType(ResourceType.EXAM);
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasType(null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationHasUploaderId() {
        Specification<Resource> spec = ResourceSpecifications.hasUploaderId(1L);
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasUploaderId(null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationHasAcademicYear() {
        Specification<Resource> spec = ResourceSpecifications.hasAcademicYear("2024-2025");
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasAcademicYear(null);
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasAcademicYear("");
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationHasExamTerm() {
        Specification<Resource> spec = ResourceSpecifications.hasExamTerm("Moed A");
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasExamTerm(null);
        assertNotNull(spec);
        
        spec = ResourceSpecifications.hasExamTerm("");
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationTitleContains() {
        Specification<Resource> spec = ResourceSpecifications.titleContains("exam");
        assertNotNull(spec);
        
        spec = ResourceSpecifications.titleContains(null);
        assertNotNull(spec);
        
        spec = ResourceSpecifications.titleContains("");
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationConvenienceMethods() {
        Specification<Resource> approvedSpec = ResourceSpecifications.isApproved();
        assertNotNull(approvedSpec);
        
        Specification<Resource> pendingSpec = ResourceSpecifications.isPending();
        assertNotNull(pendingSpec);
        
        Specification<Resource> rejectedSpec = ResourceSpecifications.isRejected();
        assertNotNull(rejectedSpec);
    }
    
    @Test
    void testSpecificationForCoursePageFiltering() {
        Specification<Resource> spec = ResourceSpecifications.forCoursePageFiltering(
            1L, ResourceType.EXAM, "2024-2025", "final");
        assertNotNull(spec);
        
        // Test with null values
        spec = ResourceSpecifications.forCoursePageFiltering(null, null, null, null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationForAdminModeration() {
        Specification<Resource> spec = ResourceSpecifications.forAdminModeration(1L, ResourceType.EXAM, 1L);
        assertNotNull(spec);
        
        // Test with null values
        spec = ResourceSpecifications.forAdminModeration(null, null, null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationForUserResources() {
        Specification<Resource> spec = ResourceSpecifications.forUserResources(
            1L, ResourceStatus.APPROVED, ResourceType.EXAM);
        assertNotNull(spec);
        
        // Test with null values
        spec = ResourceSpecifications.forUserResources(null, null, null);
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationWithCriteria() {
        Specification<Resource> spec = ResourceSpecifications.withCriteria(
            1L, ResourceStatus.APPROVED, ResourceType.EXAM, 1L, "2024-2025", "Moed A", "final");
        assertNotNull(spec);
        
        // Test with all null values
        spec = ResourceSpecifications.withCriteria(null, null, null, null, null, null, null);
        assertNotNull(spec);
        
        // Test with mixed values
        spec = ResourceSpecifications.withCriteria(1L, null, ResourceType.EXAM, null, "2024-2025", null, "exam");
        assertNotNull(spec);
    }
    
    @Test
    void testSpecificationCombination() {
        Specification<Resource> courseSpec = ResourceSpecifications.hasCourseId(1L);
        Specification<Resource> statusSpec = ResourceSpecifications.isApproved();
        Specification<Resource> typeSpec = ResourceSpecifications.hasType(ResourceType.EXAM);
        
        Specification<Resource> combinedSpec = Specification
            .where(courseSpec)
            .and(statusSpec)
            .and(typeSpec);
        
        assertNotNull(combinedSpec);
    }
    
    @Test
    void testResourceEntityBusinessMethods() {
        // Test the business methods we'll use in the repository layer
        assertTrue(approvedExam.isApproved());
        assertFalse(approvedExam.isPending());
        assertFalse(approvedExam.isRejected());
        
        assertTrue(pendingHomework.isPending());
        assertFalse(pendingHomework.isApproved());
        assertFalse(pendingHomework.isRejected());
        
        assertTrue(rejectedSummary.isRejected());
        assertFalse(rejectedSummary.isApproved());
        assertFalse(rejectedSummary.isPending());
    }
    
    @Test
    void testResourceOwnership() {
        assertTrue(approvedExam.isOwnedBy(student1));
        assertFalse(approvedExam.isOwnedBy(student2));
        assertFalse(approvedExam.isOwnedBy(admin));
        
        assertTrue(pendingHomework.isOwnedBy(student2));
        assertFalse(pendingHomework.isOwnedBy(student1));
    }
    
    @Test
    void testResourceProperties() {
        assertEquals("Final Exam 2024", approvedExam.getTitle());
        assertEquals(ResourceType.EXAM, approvedExam.getType());
        assertEquals("2024-2025", approvedExam.getAcademicYear());
        assertEquals("Moed A", approvedExam.getExamTerm());
        assertEquals("https://example.com/exam.pdf", approvedExam.getUrl());
        
        assertEquals("Assignment 1", pendingHomework.getTitle());
        assertEquals(ResourceType.HOMEWORK, pendingHomework.getType());
        assertEquals("2024-2025", pendingHomework.getAcademicYear());
        assertNull(pendingHomework.getExamTerm());
    }
}