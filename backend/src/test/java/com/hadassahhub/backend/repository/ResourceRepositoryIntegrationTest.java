package com.hadassahhub.backend.repository;

import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.*;
import com.hadassahhub.backend.specification.ResourceSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ResourceRepository with real database operations.
 * Tests complex filtering scenarios and performance with larger datasets.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ResourceRepositoryIntegrationTest {
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private Course course1;
    private Course course2;
    private User student1;
    private User student2;
    private User admin;
    
    @BeforeEach
    void setUp() {
        // Create test courses
        course1 = new Course("Computer Science", "Intro to CS", CourseCategory.CS_CORE, 4, StudyYear.Y1);
        course1 = courseRepository.save(course1);
        
        course2 = new Course("Data Structures", "Advanced DS", CourseCategory.CS_CORE, 4, StudyYear.Y2);
        course2 = courseRepository.save(course2);
        
        // Create test users
        student1 = new User("student1@edu.hac.ac.il", "password", "John Doe", UserRole.STUDENT);
        student1 = userRepository.save(student1);
        
        student2 = new User("student2@edu.hac.ac.il", "password", "Jane Smith", UserRole.STUDENT);
        student2 = userRepository.save(student2);
        
        admin = new User("admin@edu.hac.ac.il", "password", "Admin User", UserRole.ADMIN);
        admin = userRepository.save(admin);
    }
    
    @Test
    void testComplexFilteringScenarios() {
        // Create diverse resources
        Resource exam1 = createResource(course1, student1, "Final Exam 2024", ResourceType.EXAM, "2024-2025", "Moed A");
        exam1.approve(admin);
        exam1 = resourceRepository.save(exam1);
        
        Resource exam2 = createResource(course1, student2, "Final Exam 2023", ResourceType.EXAM, "2023-2024", "Moed A");
        exam2.approve(admin);
        exam2 = resourceRepository.save(exam2);
        
        Resource homework1 = createResource(course1, student1, "Assignment 1", ResourceType.HOMEWORK, "2024-2025", null);
        homework1 = resourceRepository.save(homework1); // Pending
        
        Resource summary1 = createResource(course2, student2, "Course Summary", ResourceType.SUMMARY, null, null);
        summary1.reject(admin, "Poor quality");
        summary1 = resourceRepository.save(summary1);
        
        // Test complex specification combinations
        Specification<Resource> complexSpec = ResourceSpecifications
            .hasCourseId(course1.getId())
            .and(ResourceSpecifications.isApproved())
            .and(ResourceSpecifications.hasType(ResourceType.EXAM))
            .and(ResourceSpecifications.hasAcademicYear("2024-2025"));
        
        List<Resource> results = resourceRepository.findAll(complexSpec);
        
        assertEquals(1, results.size());
        assertEquals(exam1.getId(), results.get(0).getId());
    }
    
    @Test
    void testResourceCourseUserRelationshipIntegrity() {
        // Create resource with relationships
        Resource resource = createResource(course1, student1, "Test Resource", ResourceType.LINK, null, null);
        resource = resourceRepository.save(resource);
        
        // Verify relationships are properly loaded
        Resource loaded = resourceRepository.findById(resource.getId()).orElseThrow();
        
        assertNotNull(loaded.getCourse());
        assertEquals(course1.getId(), loaded.getCourse().getId());
        assertEquals("Computer Science", loaded.getCourse().getName());
        
        assertNotNull(loaded.getUploadedBy());
        assertEquals(student1.getId(), loaded.getUploadedBy().getId());
        assertEquals("John Doe", loaded.getUploadedBy().getDisplayName());
    }
    
    @Test
    void testRepositoryPerformanceWithLargerDataset() {
        // Create larger dataset (50 resources)
        for (int i = 0; i < 50; i++) {
            Resource resource = createResource(
                i % 2 == 0 ? course1 : course2,
                i % 3 == 0 ? student1 : student2,
                "Resource " + i,
                ResourceType.values()[i % ResourceType.values().length],
                i % 2 == 0 ? "2024-2025" : "2023-2024",
                null
            );
            
            if (i % 3 == 0) {
                resource.approve(admin);
            } else if (i % 5 == 0) {
                resource.reject(admin, "Test rejection");
            }
            // Others remain pending
            
            resourceRepository.save(resource);
        }
        
        // Test performance of complex queries
        long startTime = System.currentTimeMillis();
        
        Specification<Resource> performanceSpec = ResourceSpecifications
            .hasCourseId(course1.getId())
            .and(ResourceSpecifications.isApproved())
            .and(ResourceSpecifications.hasAcademicYear("2024-2025"));
        
        List<Resource> results = resourceRepository.findAll(performanceSpec);
        
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;
        
        // Query should complete reasonably quickly (under 1 second for this dataset)
        assertTrue(queryTime < 1000, "Query took too long: " + queryTime + "ms");
        
        // Verify results are correct
        assertTrue(results.size() > 0);
        results.forEach(resource -> {
            assertEquals(course1.getId(), resource.getCourse().getId());
            assertEquals(ResourceStatus.APPROVED, resource.getStatus());
            assertEquals("2024-2025", resource.getAcademicYear());
        });
    }
    
    @Test
    void testIndexingAndQueryOptimization() {
        // Create resources that will test our indexes
        for (int i = 0; i < 20; i++) {
            Resource resource = createResource(course1, student1, "Resource " + i, ResourceType.EXAM, "2024-2025", null);
            if (i % 2 == 0) {
                resource.approve(admin);
            }
            resourceRepository.save(resource);
        }
        
        // Test queries that should use our indexes
        
        // Test course_id + status index
        long startTime = System.currentTimeMillis();
        List<Resource> courseStatusResults = resourceRepository.findByCourseIdAndStatus(course1.getId(), ResourceStatus.APPROVED);
        long queryTime1 = System.currentTimeMillis() - startTime;
        
        // Test course_id + type + status index
        startTime = System.currentTimeMillis();
        List<Resource> courseTypeStatusResults = resourceRepository.findByCourseIdAndStatusAndType(
            course1.getId(), ResourceStatus.APPROVED, ResourceType.EXAM);
        long queryTime2 = System.currentTimeMillis() - startTime;
        
        // Test uploaded_by index
        startTime = System.currentTimeMillis();
        List<Resource> userResults = resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(student1.getId());
        long queryTime3 = System.currentTimeMillis() - startTime;
        
        // All queries should be fast
        assertTrue(queryTime1 < 100, "Course+Status query too slow: " + queryTime1 + "ms");
        assertTrue(queryTime2 < 100, "Course+Type+Status query too slow: " + queryTime2 + "ms");
        assertTrue(queryTime3 < 100, "User query too slow: " + queryTime3 + "ms");
        
        // Verify results
        assertEquals(10, courseStatusResults.size());
        assertEquals(10, courseTypeStatusResults.size());
        assertEquals(20, userResults.size());
    }
    
    @Test
    void testPaginationWithLargeDataset() {
        // Create 100 resources
        for (int i = 0; i < 100; i++) {
            Resource resource = createResource(course1, student1, "Resource " + String.format("%03d", i), 
                ResourceType.EXAM, "2024-2025", null);
            resource.approve(admin);
            resourceRepository.save(resource);
        }
        
        // Test pagination
        Pageable pageable = PageRequest.of(0, 10);
        Page<Resource> firstPage = resourceRepository.findByCourseIdAndStatus(course1.getId(), ResourceStatus.APPROVED, pageable);
        
        assertEquals(10, firstPage.getContent().size());
        assertEquals(100, firstPage.getTotalElements());
        assertEquals(10, firstPage.getTotalPages());
        assertTrue(firstPage.hasNext());
        assertFalse(firstPage.hasPrevious());
        
        // Test second page
        Pageable secondPageable = PageRequest.of(1, 10);
        Page<Resource> secondPage = resourceRepository.findByCourseIdAndStatus(course1.getId(), ResourceStatus.APPROVED, secondPageable);
        
        assertEquals(10, secondPage.getContent().size());
        assertEquals(100, secondPage.getTotalElements());
        assertTrue(secondPage.hasNext());
        assertTrue(secondPage.hasPrevious());
        
        // Verify no overlap between pages
        List<Long> firstPageIds = firstPage.getContent().stream().map(Resource::getId).toList();
        List<Long> secondPageIds = secondPage.getContent().stream().map(Resource::getId).toList();
        
        assertTrue(firstPageIds.stream().noneMatch(secondPageIds::contains));
    }
    
    @Test
    void testSpecificationPerformanceComparison() {
        // Create test data
        for (int i = 0; i < 30; i++) {
            Resource resource = createResource(course1, student1, "Resource " + i, ResourceType.EXAM, "2024-2025", null);
            resource.approve(admin);
            resourceRepository.save(resource);
        }
        
        // Test different specification approaches
        
        // Approach 1: Multiple separate specifications
        long startTime = System.currentTimeMillis();
        Specification<Resource> spec1 = ResourceSpecifications.hasCourseId(course1.getId())
            .and(ResourceSpecifications.hasStatus(ResourceStatus.APPROVED))
            .and(ResourceSpecifications.hasType(ResourceType.EXAM));
        List<Resource> results1 = resourceRepository.findAll(spec1);
        long time1 = System.currentTimeMillis() - startTime;
        
        // Approach 2: Combined specification
        startTime = System.currentTimeMillis();
        Specification<Resource> spec2 = ResourceSpecifications.forCoursePageFiltering(
            course1.getId(), ResourceType.EXAM, "2024-2025", null);
        List<Resource> results2 = resourceRepository.findAll(spec2);
        long time2 = System.currentTimeMillis() - startTime;
        
        // Both should return same results
        assertEquals(results1.size(), results2.size());
        
        // Both should be reasonably fast
        assertTrue(time1 < 200, "Specification approach 1 too slow: " + time1 + "ms");
        assertTrue(time2 < 200, "Specification approach 2 too slow: " + time2 + "ms");
    }
    
    private Resource createResource(Course course, User uploader, String title, ResourceType type, 
                                  String academicYear, String examTerm) {
        Resource resource = new Resource(course, uploader, title, type, "https://example.com/" + title.replace(" ", ""));
        resource.setAcademicYear(academicYear);
        resource.setExamTerm(examTerm);
        return resource;
    }
}