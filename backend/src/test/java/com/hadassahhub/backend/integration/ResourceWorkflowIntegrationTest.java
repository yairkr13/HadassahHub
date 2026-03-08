package com.hadassahhub.backend.integration;

import com.hadassahhub.backend.dto.CreateResourceRequestDTO;
import com.hadassahhub.backend.dto.RejectResourceRequestDTO;
import com.hadassahhub.backend.dto.ResourceDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests for the complete resource workflow.
 * Tests the entire flow from resource creation to approval/rejection and visibility.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ResourceWorkflowIntegrationTest {
    
    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private Course testCourse;
    private User studentUser;
    private User adminUser;
    private User otherStudentUser;
    
    @BeforeEach
    void setUp() {
        // Create test course
        testCourse = new Course();
        testCourse.setName("Advanced Java Programming");
        testCourse.setCategory(CourseCategory.CS_CORE);
        testCourse.setCredits(4);
        testCourse.setRecommendedYear(StudyYear.Y2);
        testCourse = courseRepository.save(testCourse);
        
        // Create test users
        studentUser = createUser("student@test.com", "Test Student", UserRole.STUDENT);
        adminUser = createUser("admin@test.com", "Test Admin", UserRole.ADMIN);
        otherStudentUser = createUser("other@test.com", "Other Student", UserRole.STUDENT);
    }
    
    @Test
    void testCompleteResourceWorkflow_UploadToApproval() {
        // Step 1: Student uploads a resource
        CreateResourceRequestDTO uploadRequest = new CreateResourceRequestDTO(
            testCourse.getId(),
            "Final Exam 2024 - Moed A",
            ResourceType.EXAM,
            "https://example.com/exam-2024-a.pdf",
            "2023-2024",
            "Moed A"
        );
        
        ResourceDTO createdResource = resourceService.createResource(uploadRequest, studentUser.getId());
        
        assertNotNull(createdResource);
        assertEquals("Final Exam 2024 - Moed A", createdResource.title());
        assertEquals(ResourceStatus.PENDING, createdResource.status());
        assertEquals("Test Student", createdResource.uploaderName());
        
        Long resourceId = createdResource.id();
        
        // Step 2: Verify resource is not visible in public course resources (only approved)
        List<ResourceDTO> publicResources = resourceService.getCourseResources(testCourse.getId(), null);
        assertEquals(0, publicResources.size()); // No approved resources yet
        
        // Step 3: Verify resource appears in student's own resources
        List<ResourceDTO> userResources = resourceService.getUserResources(studentUser.getId(), null);
        assertEquals(1, userResources.size());
        assertEquals("Final Exam 2024 - Moed A", userResources.get(0).title());
        assertEquals(ResourceStatus.PENDING, userResources.get(0).status());
        
        // Step 4: Verify resource appears in admin pending queue
        List<ResourceDTO> pendingResources = resourceService.getPendingResources(null);
        assertEquals(1, pendingResources.size());
        assertEquals("Final Exam 2024 - Moed A", pendingResources.get(0).title());
        
        // Step 5: Admin approves the resource
        ResourceDTO approvedResource = resourceService.approveResource(resourceId, adminUser.getId());
        assertEquals(ResourceStatus.APPROVED, approvedResource.status());
        assertEquals("Test Admin", approvedResource.approverName());
        
        // Step 6: Verify resource now appears in public course resources
        publicResources = resourceService.getCourseResources(testCourse.getId(), null);
        assertEquals(1, publicResources.size());
        assertEquals("Final Exam 2024 - Moed A", publicResources.get(0).title());
        assertEquals(ResourceStatus.APPROVED, publicResources.get(0).status());
        
        // Step 7: Verify course statistics are updated
        var stats = resourceService.getCourseResourceStats(testCourse.getId());
        assertEquals(1, stats.totalResources());
        assertEquals(1, stats.examCount());
        assertEquals(0, stats.homeworkCount());
        
        // Step 8: Verify resource is no longer in pending queue
        pendingResources = resourceService.getPendingResources(null);
        assertEquals(0, pendingResources.size());
    }
    
    @Test
    void testCompleteResourceWorkflow_UploadToRejection() {
        // Step 1: Student uploads a resource
        CreateResourceRequestDTO uploadRequest = new CreateResourceRequestDTO(
            testCourse.getId(),
            "Inappropriate Content",
            ResourceType.SUMMARY,
            "https://example.com/inappropriate.pdf",
            null,
            null
        );
        
        ResourceDTO createdResource = resourceService.createResource(uploadRequest, studentUser.getId());
        Long resourceId = createdResource.id();
        
        // Step 2: Admin rejects the resource
        RejectResourceRequestDTO rejectRequest = new RejectResourceRequestDTO(
            "Content does not meet academic standards"
        );
        
        ResourceDTO rejectedResource = resourceService.rejectResource(resourceId, adminUser.getId(), rejectRequest);
        assertEquals(ResourceStatus.REJECTED, rejectedResource.status());
        assertEquals("Content does not meet academic standards", rejectedResource.rejectionReason());
        
        // Step 3: Verify resource does not appear in public course resources
        List<ResourceDTO> publicResources = resourceService.getCourseResources(testCourse.getId(), null);
        assertEquals(0, publicResources.size());
        
        // Step 4: Verify resource still appears in student's resources with rejected status
        List<ResourceDTO> userResources = resourceService.getUserResources(studentUser.getId(), null);
        assertEquals(1, userResources.size());
        assertEquals(ResourceStatus.REJECTED, userResources.get(0).status());
        assertEquals("Content does not meet academic standards", userResources.get(0).rejectionReason());
        
        // Step 5: Admin can reset resource to pending if needed
        ResourceDTO resetResource = resourceService.resetResourceToPending(resourceId, adminUser.getId());
        assertEquals(ResourceStatus.PENDING, resetResource.status());
    }
    
    @Test
    void testResourceAccessControl_OwnershipAndVisibility() {
        // Create a resource as student 1
        CreateResourceRequestDTO uploadRequest = new CreateResourceRequestDTO(
            testCourse.getId(),
            "Student 1 Resource",
            ResourceType.HOMEWORK,
            "https://example.com/homework.pdf",
            null,
            null
        );
        
        ResourceDTO createdResource = resourceService.createResource(uploadRequest, studentUser.getId());
        Long resourceId = createdResource.id();
        
        // Test 1: Owner can access their pending resource
        Optional<ResourceDTO> ownerAccess = resourceService.getResourceById(resourceId, studentUser.getId(), false);
        assertTrue(ownerAccess.isPresent());
        assertEquals("Student 1 Resource", ownerAccess.get().title());
        
        // Test 2: Other student cannot access pending resource
        Optional<ResourceDTO> otherAccess = resourceService.getResourceById(resourceId, otherStudentUser.getId(), false);
        assertFalse(otherAccess.isPresent());
        
        // Test 3: Admin can access any resource
        Optional<ResourceDTO> adminAccess = resourceService.getResourceById(resourceId, adminUser.getId(), true);
        assertTrue(adminAccess.isPresent());
        assertEquals("Student 1 Resource", adminAccess.get().title());
        
        // Test 4: Owner can delete their own resource
        boolean deleted = resourceService.deleteResource(resourceId, studentUser.getId(), false);
        assertTrue(deleted);
        
        // Verify resource is deleted
        assertFalse(resourceRepository.existsById(resourceId));
    }
    
    @Test
    void testModerationStatistics_MultipleResources() {
        // Create resources with different statuses
        CreateResourceRequestDTO request1 = new CreateResourceRequestDTO(
            testCourse.getId(), "Pending 1", ResourceType.EXAM, "https://example.com/1.pdf", null, null);
        CreateResourceRequestDTO request2 = new CreateResourceRequestDTO(
            testCourse.getId(), "Pending 2", ResourceType.HOMEWORK, "https://example.com/2.pdf", null, null);
        CreateResourceRequestDTO request3 = new CreateResourceRequestDTO(
            testCourse.getId(), "To Approve", ResourceType.SUMMARY, "https://example.com/3.pdf", null, null);
        CreateResourceRequestDTO request4 = new CreateResourceRequestDTO(
            testCourse.getId(), "To Reject", ResourceType.LINK, "https://example.com/4.pdf", null, null);
        
        resourceService.createResource(request1, studentUser.getId()); // Stays pending
        resourceService.createResource(request2, studentUser.getId()); // Stays pending
        
        ResourceDTO approved = resourceService.createResource(request3, studentUser.getId());
        resourceService.approveResource(approved.id(), adminUser.getId());
        
        ResourceDTO rejected = resourceService.createResource(request4, studentUser.getId());
        resourceService.rejectResource(rejected.id(), adminUser.getId(), 
            new RejectResourceRequestDTO("Invalid link"));
        
        // Test moderation statistics
        var stats = resourceService.getModerationStats();
        assertEquals(2, stats.pendingCount());
        assertEquals(1, stats.approvedCount());
        assertEquals(1, stats.rejectedCount());
        assertEquals(4, stats.getTotalResources());
    }
    
    // Helper methods
    
    private User createUser(String email, String displayName, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash("hashedpassword");
        user.setRole(role);
        return userRepository.save(user);
    }
}