package com.hadassahhub.backend.integration;

import com.hadassahhub.backend.dto.CreateResourceRequestDTO;
import com.hadassahhub.backend.dto.ResourceDTO;
import com.hadassahhub.backend.dto.ResourceFilterDTO;
import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.*;
import com.hadassahhub.backend.repository.CourseRepository;
import com.hadassahhub.backend.repository.ResourceRepository;
import com.hadassahhub.backend.repository.UserRepository;
import com.hadassahhub.backend.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for course-resource integration functionality.
 * Tests enhanced course endpoints with resource statistics and filtering.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseResourceIntegrationTest {
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private Course javaCourse;
    private Course mathCourse;
    private User studentUser;
    private User adminUser;
    
    @BeforeEach
    void setUp() {
        // Create test courses
        javaCourse = createCourse("Advanced Java Programming", CourseCategory.CS_CORE, 4, StudyYear.Y2);
        mathCourse = createCourse("Linear Algebra", CourseCategory.GENERAL_ELECTIVE, 3, StudyYear.Y1);
        
        // Create test users
        studentUser = createUser("student@test.com", "Test Student", UserRole.STUDENT);
        adminUser = createUser("admin@test.com", "Test Admin", UserRole.ADMIN);
        
        // Create sample resources for Java course
        createApprovedResource(javaCourse, "Java Final Exam 2023", ResourceType.EXAM, "2022-2023", "Moed A");
        createApprovedResource(javaCourse, "Java Final Exam 2024", ResourceType.EXAM, "2023-2024", "Moed A");
        createApprovedResource(javaCourse, "Java Midterm 2024", ResourceType.EXAM, "2023-2024", "Midterm");
        createApprovedResource(javaCourse, "Assignment 1", ResourceType.HOMEWORK, "2023-2024", null);
        createApprovedResource(javaCourse, "Assignment 2", ResourceType.HOMEWORK, "2023-2024", null);
        createApprovedResource(javaCourse, "Course Summary", ResourceType.SUMMARY, null, null);
        createApprovedResource(javaCourse, "Official Documentation", ResourceType.LINK, null, null);
        
        // Create sample resources for Math course
        createApprovedResource(mathCourse, "Linear Algebra Final 2024", ResourceType.EXAM, "2023-2024", "Moed A");
        createApprovedResource(mathCourse, "Problem Set 1", ResourceType.HOMEWORK, "2023-2024", null);
    }
    
    @Test
    void testGetCourseResources_WithStatistics() {
        // Test getting course resources with proper statistics
        List<ResourceDTO> resources = resourceService.getCourseResources(javaCourse.getId(), null);
        assertEquals(7, resources.size());
        
        // All should be approved
        assertTrue(resources.stream().allMatch(r -> r.status() == ResourceStatus.APPROVED));
        assertTrue(resources.stream().allMatch(r -> r.courseId().equals(javaCourse.getId())));
        
        // Test course resource statistics
        var stats = resourceService.getCourseResourceStats(javaCourse.getId());
        assertEquals(javaCourse.getId(), stats.courseId());
        assertEquals(7, stats.totalResources());
        assertEquals(3, stats.examCount());
        assertEquals(2, stats.homeworkCount());
        assertEquals(1, stats.summaryCount());
        assertEquals(1, stats.linkCount());
    }
    
    @Test
    void testCourseResourceFiltering_ByType() {
        // Filter by EXAM type
        ResourceFilterDTO examFilter = new ResourceFilterDTO(
            javaCourse.getId(), ResourceType.EXAM, null, null, null, null, null,
            null, null, "createdAt", "desc"
        );
        List<ResourceDTO> examResources = resourceService.getCourseResources(javaCourse.getId(), examFilter);
        assertEquals(3, examResources.size());
        assertTrue(examResources.stream().allMatch(r -> r.type() == ResourceType.EXAM));
        
        // Filter by HOMEWORK type
        ResourceFilterDTO homeworkFilter = new ResourceFilterDTO(
            javaCourse.getId(), ResourceType.HOMEWORK, null, null, null, null, null,
            null, null, "createdAt", "desc"
        );
        List<ResourceDTO> homeworkResources = resourceService.getCourseResources(javaCourse.getId(), homeworkFilter);
        assertEquals(2, homeworkResources.size());
        assertTrue(homeworkResources.stream().allMatch(r -> r.type() == ResourceType.HOMEWORK));
    }
    
    @Test
    void testCourseResourceFiltering_ByAcademicYear() {
        // Filter by academic year 2023-2024
        ResourceFilterDTO yearFilter = new ResourceFilterDTO(
            javaCourse.getId(), null, null, "2023-2024", null, null, null,
            null, null, "createdAt", "desc"
        );
        List<ResourceDTO> yearResources = resourceService.getCourseResources(javaCourse.getId(), yearFilter);
        assertEquals(4, yearResources.size());
        assertTrue(yearResources.stream().allMatch(r -> "2023-2024".equals(r.academicYear())));
    }
    
    @Test
    void testMultipleCourses_IndependentResources() {
        // Verify Java course has 7 resources
        var javaStats = resourceService.getCourseResourceStats(javaCourse.getId());
        assertEquals(7, javaStats.totalResources());
        
        // Verify Math course has 2 resources
        var mathStats = resourceService.getCourseResourceStats(mathCourse.getId());
        assertEquals(2, mathStats.totalResources());
        assertEquals(1, mathStats.examCount());
        assertEquals(1, mathStats.homeworkCount());
        
        // Verify resources are course-specific
        List<ResourceDTO> mathResources = resourceService.getCourseResources(mathCourse.getId(), null);
        assertEquals(2, mathResources.size());
        assertTrue(mathResources.stream().allMatch(r -> r.courseId().equals(mathCourse.getId())));
    }
    
    @Test
    void testCourseResourceUpload_CourseSpecific() {
        // Upload resource to Java course
        CreateResourceRequestDTO javaRequest = new CreateResourceRequestDTO(
            javaCourse.getId(),
            "New Java Assignment",
            ResourceType.HOMEWORK,
            "https://example.com/java-assignment.pdf",
            "2024-2025",
            null
        );
        
        ResourceDTO javaResource = resourceService.createResource(javaRequest, studentUser.getId());
        assertEquals(javaCourse.getId(), javaResource.courseId());
        assertEquals("New Java Assignment", javaResource.title());
        
        // Upload resource to Math course
        CreateResourceRequestDTO mathRequest = new CreateResourceRequestDTO(
            mathCourse.getId(),
            "New Math Problem Set",
            ResourceType.HOMEWORK,
            "https://example.com/math-problems.pdf",
            "2024-2025",
            null
        );
        
        ResourceDTO mathResource = resourceService.createResource(mathRequest, studentUser.getId());
        assertEquals(mathCourse.getId(), mathResource.courseId());
        assertEquals("New Math Problem Set", mathResource.title());
    }
    
    @Test
    void testEmptyCourse_NoResources() {
        Course emptyCourse = createCourse("Empty Course", CourseCategory.CS_ELECTIVE, 2, StudyYear.Y3);
        
        // Test empty course resources
        List<ResourceDTO> resources = resourceService.getCourseResources(emptyCourse.getId(), null);
        assertEquals(0, resources.size());
        
        // Test empty course statistics
        var stats = resourceService.getCourseResourceStats(emptyCourse.getId());
        assertEquals(emptyCourse.getId(), stats.courseId());
        assertEquals(0, stats.totalResources());
        assertEquals(0, stats.examCount());
        assertEquals(0, stats.homeworkCount());
        assertEquals(0, stats.summaryCount());
        assertEquals(0, stats.linkCount());
    }
    
    // Helper methods
    
    private Course createCourse(String name, CourseCategory category, int credits, StudyYear year) {
        Course course = new Course();
        course.setName(name);
        course.setCategory(category);
        course.setCredits(credits);
        course.setRecommendedYear(year);
        return courseRepository.save(course);
    }
    
    private User createUser(String email, String displayName, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash("hashedpassword");
        user.setRole(role);
        return userRepository.save(user);
    }
    
    private void createApprovedResource(Course course, String title, ResourceType type, 
                                      String academicYear, String examTerm) {
        Resource resource = new Resource(course, studentUser, title, type, 
            "https://example.com/" + title.toLowerCase().replace(" ", "-") + ".pdf");
        resource.setAcademicYear(academicYear);
        resource.setExamTerm(examTerm);
        resource.approve(adminUser);
        resourceRepository.save(resource);
    }
}