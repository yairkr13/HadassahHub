package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.*;
import com.hadassahhub.backend.repository.CourseRepository;
import com.hadassahhub.backend.repository.ResourceRepository;
import com.hadassahhub.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    
    @Mock
    private ResourceRepository resourceRepository;
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private ResourceService resourceService;
    
    private Course testCourse;
    private User testUser;
    private User adminUser;
    private Resource testResource;
    private CreateResourceRequestDTO createRequest;
    
    @BeforeEach
    void setUp() {
        // Create test course
        testCourse = new Course();
        testCourse.setName("Computer Science");
        testCourse.setCategory(CourseCategory.CS_CORE);
        testCourse.setCredits(4);
        testCourse.setRecommendedYear(StudyYear.Y1);
        
        // Create test user
        testUser = new User();
        testUser.setEmail("student@edu.hac.ac.il");
        testUser.setDisplayName("John Doe");
        testUser.setRole(UserRole.STUDENT);
        
        // Create admin user
        adminUser = new User();
        adminUser.setEmail("admin@edu.hac.ac.il");
        adminUser.setDisplayName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        
        // Create test resource
        testResource = new Resource(testCourse, testUser, "Final Exam 2024", ResourceType.EXAM, "https://example.com/exam.pdf");
        testResource.setAcademicYear("2024-2025");
        testResource.setExamTerm("Moed A");
        testResource.approve(adminUser);
        
        // Create test request
        createRequest = new CreateResourceRequestDTO(
            1L, "Test Resource", ResourceType.EXAM, "https://example.com/test.pdf", "2024-2025", "Moed A"
        );
    }
    
    @Test
    void testCreateResource_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(resourceRepository.existsByCourseIdAndUploadedByIdAndTitle(1L, 1L, "Test Resource")).thenReturn(false);
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);
        
        // Act
        ResourceDTO result = resourceService.createResource(createRequest, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Final Exam 2024", result.title());
        assertEquals(ResourceType.EXAM, result.type());
        assertEquals("https://example.com/exam.pdf", result.url());
        assertEquals("2024-2025", result.academicYear());
        assertEquals("Moed A", result.examTerm());
        
        verify(resourceRepository).save(any(Resource.class));
    }
    
    @Test
    void testCreateResource_CourseNotFound() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.createResource(createRequest, 1L));
        assertEquals("Course not found with id: 1", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testCreateResource_UserNotFound() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.createResource(createRequest, 1L));
        assertEquals("User not found with id: 1", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testCreateResource_DuplicateTitle() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(resourceRepository.existsByCourseIdAndUploadedByIdAndTitle(1L, 1L, "Test Resource")).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.createResource(createRequest, 1L));
        assertEquals("You have already uploaded a resource with this title for this course", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testCreateResource_InvalidExamResource() {
        // Arrange
        CreateResourceRequestDTO invalidExamRequest = new CreateResourceRequestDTO(
            1L, "Test Exam", ResourceType.EXAM, "https://example.com/exam.pdf", null, "Moed A"
        );
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(resourceRepository.existsByCourseIdAndUploadedByIdAndTitle(1L, 1L, "Test Exam")).thenReturn(false);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.createResource(invalidExamRequest, 1L));
        assertEquals("Exam resources must include academic year", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testGetCourseResources_Success() {
        // Arrange
        ResourceFilterDTO filter = ResourceFilterDTO.forCourseResources(1L, null, null);
        List<Resource> resources = List.of(testResource);
        
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(resourceRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(resources);
        
        // Act
        List<ResourceDTO> result = resourceService.getCourseResources(1L, filter);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ResourceDTO dto = result.get(0);
        assertEquals("Final Exam 2024", dto.title());
        assertEquals(ResourceType.EXAM, dto.type());
        assertTrue(dto.isApproved());
        
        verify(resourceRepository).findAll(any(Specification.class), any(Sort.class));
    }
    
    @Test
    void testGetCourseResources_CourseNotFound() {
        // Arrange
        ResourceFilterDTO filter = ResourceFilterDTO.forCourseResources(1L, null, null);
        when(courseRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.getCourseResources(1L, filter));
        assertEquals("Course not found with id: 1", exception.getMessage());
        
        verify(resourceRepository, never()).findAll(any(Specification.class), any(Sort.class));
    }
    
    @Test
    void testGetCourseResourcesPaginated_Success() {
        // Arrange
        ResourceFilterDTO filter = ResourceFilterDTO.withPagination(1L, 0, 10);
        List<Resource> resources = List.of(testResource);
        Page<Resource> resourcePage = new PageImpl<>(resources);
        
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(resourceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(resourcePage);
        
        // Act
        Page<ResourceDTO> result = resourceService.getCourseResourcesPaginated(1L, filter);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        ResourceDTO dto = result.getContent().get(0);
        assertEquals("Final Exam 2024", dto.title());
        
        verify(resourceRepository).findAll(any(Specification.class), any(Pageable.class));
    }
    
    @Test
    void testGetUserResources_Success() {
        // Arrange
        ResourceFilterDTO filter = ResourceFilterDTO.forUserResources(1L, null);
        List<Resource> resources = List.of(testResource);
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(resourceRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(resources);
        
        // Act
        List<ResourceDTO> result = resourceService.getUserResources(1L, filter);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ResourceDTO dto = result.get(0);
        assertEquals("Final Exam 2024", dto.title());
        assertEquals("Computer Science", dto.courseName());
        
        verify(resourceRepository).findAll(any(Specification.class), any(Sort.class));
    }
    
    @Test
    void testGetUserResources_UserNotFound() {
        // Arrange
        ResourceFilterDTO filter = ResourceFilterDTO.forUserResources(1L, null);
        when(userRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.getUserResources(1L, filter));
        assertEquals("User not found with id: 1", exception.getMessage());
        
        verify(resourceRepository, never()).findAll(any(Specification.class), any(Sort.class));
    }
    
    @Test
    void testGetResourceById_PublicAccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser)); // Different user
        
        // Act
        Optional<ResourceDTO> result = resourceService.getResourceById(1L, 2L, false);
        
        // Assert
        assertTrue(result.isPresent());
        ResourceDTO dto = result.get();
        assertEquals("Final Exam 2024", dto.title());
        assertNull(dto.courseName()); // Public view doesn't include course name
        assertTrue(dto.isApproved());
    }
    
    @Test
    void testGetResourceById_OwnerAccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser)); // Same user as owner
        
        // Act
        Optional<ResourceDTO> result = resourceService.getResourceById(1L, 1L, false);
        
        // Assert
        assertTrue(result.isPresent());
        ResourceDTO dto = result.get();
        assertEquals("Final Exam 2024", dto.title());
        assertEquals("Computer Science", dto.courseName()); // Owner view includes course name
    }
    
    @Test
    void testGetResourceById_AdminAccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        
        // Act
        Optional<ResourceDTO> result = resourceService.getResourceById(1L, 2L, true);
        
        // Assert
        assertTrue(result.isPresent());
        ResourceDTO dto = result.get();
        assertEquals("Final Exam 2024", dto.title());
        assertEquals("Computer Science", dto.courseName());
        assertEquals(testCourse.getId(), dto.courseId()); // Admin view includes course ID
    }
    
    @Test
    void testGetResourceById_NotFound() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<ResourceDTO> result = resourceService.getResourceById(1L, 1L, false);
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void testGetCourseResourceStats_Success() {
        // Arrange
        List<Resource> approvedResources = List.of(
            createResourceWithType(ResourceType.EXAM, "2024-2025"),
            createResourceWithType(ResourceType.EXAM, "2024-2025"),
            createResourceWithType(ResourceType.HOMEWORK, "2023-2024"),
            createResourceWithType(ResourceType.SUMMARY, null)
        );
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(resourceRepository.findByCourseIdAndStatus(1L, ResourceStatus.APPROVED)).thenReturn(approvedResources);
        when(resourceRepository.countByCourseIdAndStatus(1L, ResourceStatus.PENDING)).thenReturn(2L);
        
        // Act
        ResourceStatsDTO result = resourceService.getCourseResourceStats(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.courseId());
        assertEquals("Computer Science", result.courseName());
        assertEquals(4L, result.totalResources());
        assertEquals(2L, result.examCount());
        assertEquals(1L, result.homeworkCount());
        assertEquals(1L, result.summaryCount());
        assertEquals(0L, result.linkCount());
        assertEquals(2L, result.pendingCount());
        
        assertTrue(result.hasResources());
        assertTrue(result.hasResourcesOfType(ResourceType.EXAM));
        assertTrue(result.hasPendingResources());
        
        // Check academic year breakdown
        assertEquals(2L, result.resourcesByAcademicYear().get("2024-2025"));
        assertEquals(1L, result.resourcesByAcademicYear().get("2023-2024"));
    }
    
    @Test
    void testGetCourseResourceStats_EmptyStats() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(resourceRepository.findByCourseIdAndStatus(1L, ResourceStatus.APPROVED)).thenReturn(List.of());
        
        // Act
        ResourceStatsDTO result = resourceService.getCourseResourceStats(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.courseId());
        assertEquals("Computer Science", result.courseName());
        assertEquals(0L, result.totalResources());
        assertFalse(result.hasResources());
    }
    
    @Test
    void testGetCourseResourceStats_CourseNotFound() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.getCourseResourceStats(1L));
        assertEquals("Course not found with id: 1", exception.getMessage());
    }
    
    @Test
    void testDeleteResource_OwnerSuccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        boolean result = resourceService.deleteResource(1L, 1L, false);
        
        // Assert
        assertTrue(result);
        verify(resourceRepository).delete(testResource);
    }
    
    @Test
    void testDeleteResource_AdminSuccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        
        // Act
        boolean result = resourceService.deleteResource(1L, 2L, true);
        
        // Assert
        assertTrue(result);
        verify(resourceRepository).delete(testResource);
    }
    
    @Test
    void testDeleteResource_NotOwner() {
        // Arrange
        User otherUser = new User();
        otherUser.setEmail("other@edu.hac.ac.il");
        otherUser.setDisplayName("Other User");
        
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.deleteResource(1L, 2L, false));
        assertEquals("You can only delete your own resources", exception.getMessage());
        
        verify(resourceRepository, never()).delete(any(Resource.class));
    }
    
    @Test
    void testDeleteResource_NotFound() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        boolean result = resourceService.deleteResource(1L, 1L, false);
        
        // Assert
        assertFalse(result);
        verify(resourceRepository, never()).delete(any(Resource.class));
    }
    
    @Test
    void testCanAccessResource_AdminAccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        
        // Act
        boolean result = resourceService.canAccessResource(1L, 2L, true);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void testCanAccessResource_OwnerAccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        boolean result = resourceService.canAccessResource(1L, 1L, false);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void testCanAccessResource_ApprovedResourceAccess() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        
        // Act
        boolean result = resourceService.canAccessResource(1L, 2L, false);
        
        // Assert
        assertTrue(result); // Resource is approved, so accessible
    }
    
    @Test
    void testCanAccessResource_PendingResourceNoAccess() {
        // Arrange
        Resource pendingResource = new Resource(testCourse, testUser, "Pending Resource", ResourceType.HOMEWORK, "https://example.com");
        // Resource is pending by default
        
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(pendingResource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        
        // Act
        boolean result = resourceService.canAccessResource(1L, 2L, false);
        
        // Assert
        assertFalse(result); // Pending resource not accessible to non-owners
    }
    
    @Test
    void testCanAccessResource_NotFound() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        boolean result = resourceService.canAccessResource(1L, 1L, false);
        
        // Assert
        assertFalse(result);
    }
    
    // Helper methods
    
    private Resource createResourceWithType(ResourceType type, String academicYear) {
        Resource resource = new Resource(testCourse, testUser, "Test " + type, type, "https://example.com");
        resource.setAcademicYear(academicYear);
        resource.approve(adminUser);
        return resource;
    }
    
    // ===== MODERATION TESTS =====
    
    @Test
    void testApproveResource_Success() {
        // Arrange
        Resource pendingResource = new Resource(testCourse, testUser, "Pending Resource", ResourceType.EXAM, "https://example.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(pendingResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(pendingResource);
        
        // Act
        ResourceDTO result = resourceService.approveResource(1L, 2L);
        
        // Assert
        assertNotNull(result);
        assertTrue(pendingResource.isApproved());
        assertEquals(adminUser, pendingResource.getApprovedBy());
        assertNotNull(pendingResource.getApprovedAt());
        
        verify(resourceRepository).save(pendingResource);
    }
    
    @Test
    void testApproveResource_AdminNotFound() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.approveResource(1L, 2L));
        assertEquals("Admin user not found with id: 2", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testApproveResource_NotAdmin() {
        // Arrange
        User regularUser = new User();
        regularUser.setEmail("user@edu.hac.ac.il");
        regularUser.setDisplayName("Regular User");
        regularUser.setRole(UserRole.STUDENT);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.approveResource(1L, 2L));
        assertEquals("Only admins can approve resources", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testApproveResource_ResourceNotFound() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.approveResource(1L, 2L));
        assertEquals("Resource not found with id: 1", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testApproveResource_AlreadyApproved() {
        // Arrange
        Resource approvedResource = new Resource(testCourse, testUser, "Already Approved", ResourceType.EXAM, "https://example.com");
        approvedResource.approve(adminUser);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(approvedResource));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.approveResource(1L, 2L));
        assertEquals("Only pending resources can be approved. Current status: APPROVED", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testRejectResource_Success() {
        // Arrange
        Resource pendingResource = new Resource(testCourse, testUser, "Pending Resource", ResourceType.EXAM, "https://example.com");
        RejectResourceRequestDTO rejectRequest = new RejectResourceRequestDTO("Content not appropriate");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(pendingResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(pendingResource);
        
        // Act
        ResourceDTO result = resourceService.rejectResource(1L, 2L, rejectRequest);
        
        // Assert
        assertNotNull(result);
        assertTrue(pendingResource.isRejected());
        assertEquals(adminUser, pendingResource.getApprovedBy());
        assertEquals("Content not appropriate", pendingResource.getRejectionReason());
        assertNotNull(pendingResource.getApprovedAt());
        
        verify(resourceRepository).save(pendingResource);
    }
    
    @Test
    void testRejectResource_NotAdmin() {
        // Arrange
        User regularUser = new User();
        regularUser.setEmail("user@edu.hac.ac.il");
        regularUser.setDisplayName("Regular User");
        regularUser.setRole(UserRole.STUDENT);
        
        RejectResourceRequestDTO rejectRequest = new RejectResourceRequestDTO("Test reason");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.rejectResource(1L, 2L, rejectRequest));
        assertEquals("Only admins can reject resources", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testRejectResource_AlreadyRejected() {
        // Arrange
        Resource rejectedResource = new Resource(testCourse, testUser, "Already Rejected", ResourceType.EXAM, "https://example.com");
        rejectedResource.reject(adminUser, "Previous reason");
        
        RejectResourceRequestDTO rejectRequest = new RejectResourceRequestDTO("New reason");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(rejectedResource));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.rejectResource(1L, 2L, rejectRequest));
        assertEquals("Only pending resources can be rejected. Current status: REJECTED", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testGetPendingResources_Success() {
        // Arrange
        Resource pendingResource1 = new Resource(testCourse, testUser, "Pending 1", ResourceType.EXAM, "https://example.com");
        Resource pendingResource2 = new Resource(testCourse, testUser, "Pending 2", ResourceType.HOMEWORK, "https://example.com");
        List<Resource> pendingResources = List.of(pendingResource1, pendingResource2);
        
        ResourceFilterDTO filter = ResourceFilterDTO.forPendingModeration(null, null);
        
        when(resourceRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(pendingResources);
        
        // Act
        List<ResourceDTO> result = resourceService.getPendingResources(filter);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pending 1", result.get(0).title());
        assertEquals("Pending 2", result.get(1).title());
        
        verify(resourceRepository).findAll(any(Specification.class), any(Sort.class));
    }
    
    @Test
    void testGetPendingResourcesPaginated_Success() {
        // Arrange
        Resource pendingResource = new Resource(testCourse, testUser, "Pending Resource", ResourceType.EXAM, "https://example.com");
        List<Resource> pendingResources = List.of(pendingResource);
        Page<Resource> resourcePage = new PageImpl<>(pendingResources);
        
        ResourceFilterDTO filter = ResourceFilterDTO.withPagination(null, 0, 10);
        
        when(resourceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(resourcePage);
        
        // Act
        Page<ResourceDTO> result = resourceService.getPendingResourcesPaginated(filter);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Pending Resource", result.getContent().get(0).title());
        
        verify(resourceRepository).findAll(any(Specification.class), any(Pageable.class));
    }
    
    @Test
    void testGetModerationStats_Success() {
        // Arrange
        when(resourceRepository.countByStatus(ResourceStatus.PENDING)).thenReturn(5L);
        when(resourceRepository.countByStatus(ResourceStatus.APPROVED)).thenReturn(15L);
        when(resourceRepository.countByStatus(ResourceStatus.REJECTED)).thenReturn(3L);
        
        // Act
        ModerationStatsDTO result = resourceService.getModerationStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(5L, result.pendingCount());
        assertEquals(15L, result.approvedCount());
        assertEquals(3L, result.rejectedCount());
        assertEquals(23L, result.getTotalResources());
        assertTrue(result.hasPendingResources());
        
        verify(resourceRepository).countByStatus(ResourceStatus.PENDING);
        verify(resourceRepository).countByStatus(ResourceStatus.APPROVED);
        verify(resourceRepository).countByStatus(ResourceStatus.REJECTED);
    }
    
    @Test
    void testResetResourceToPending_Success() {
        // Arrange
        Resource rejectedResource = new Resource(testCourse, testUser, "Rejected Resource", ResourceType.EXAM, "https://example.com");
        rejectedResource.reject(adminUser, "Previous reason");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(rejectedResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(rejectedResource);
        
        // Act
        ResourceDTO result = resourceService.resetResourceToPending(1L, 2L);
        
        // Assert
        assertNotNull(result);
        assertTrue(rejectedResource.isPending());
        assertNull(rejectedResource.getApprovedBy());
        assertNull(rejectedResource.getApprovedAt());
        assertNull(rejectedResource.getRejectionReason());
        
        verify(resourceRepository).save(rejectedResource);
    }
    
    @Test
    void testResetResourceToPending_NotAdmin() {
        // Arrange
        User regularUser = new User();
        regularUser.setEmail("user@edu.hac.ac.il");
        regularUser.setDisplayName("Regular User");
        regularUser.setRole(UserRole.STUDENT);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.resetResourceToPending(1L, 2L));
        assertEquals("Only admins can reset resource status", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
    
    @Test
    void testResetResourceToPending_AlreadyPending() {
        // Arrange
        Resource pendingResource = new Resource(testCourse, testUser, "Pending Resource", ResourceType.EXAM, "https://example.com");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(pendingResource));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> resourceService.resetResourceToPending(1L, 2L));
        assertEquals("Resource is already pending", exception.getMessage());
        
        verify(resourceRepository, never()).save(any());
    }
}