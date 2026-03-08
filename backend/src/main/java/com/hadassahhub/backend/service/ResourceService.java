package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.repository.CourseRepository;
import com.hadassahhub.backend.repository.ResourceRepository;
import com.hadassahhub.backend.repository.UserRepository;
import com.hadassahhub.backend.specification.ResourceSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing resources.
 * Handles resource creation, retrieval, statistics, and business logic for the MVP scope.
 */
@Service
@Transactional
public class ResourceService {
    
    private final ResourceRepository resourceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    public ResourceService(ResourceRepository resourceRepository, 
                          CourseRepository courseRepository, 
                          UserRepository userRepository) {
        this.resourceRepository = resourceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Creates a new resource from a request DTO.
     * Resource starts in PENDING status and requires admin approval.
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResourceDTO createResource(CreateResourceRequestDTO request, Long uploaderId) {
        // Validate course exists
        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + request.courseId()));
        
        // Validate user exists
        User uploader = userRepository.findById(uploaderId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + uploaderId));
        
        // Check for duplicate resource by same user for same course with same title
        if (resourceRepository.existsByCourseIdAndUploadedByIdAndTitle(
                request.courseId(), uploaderId, request.title())) {
            throw new IllegalArgumentException("You have already uploaded a resource with this title for this course");
        }
        
        // Validate exam resource requirements
        if (!request.isValidExamResource()) {
            throw new IllegalArgumentException("Exam resources must include academic year");
        }
        
        // Create resource entity
        Resource resource = new Resource(course, uploader, request.title(), request.type(), request.url());
        resource.setAcademicYear(request.academicYear());
        resource.setExamTerm(request.examTerm());
        
        // Save resource
        Resource savedResource = resourceRepository.save(resource);
        
        // Return DTO for owner view
        return mapToOwnerViewDTO(savedResource);
    }
    
    /**
     * Gets approved resources for a course with optional filtering.
     * Used for course details pages.
     */
    @Transactional(readOnly = true)
    public List<ResourceDTO> getCourseResources(Long courseId, ResourceFilterDTO filter) {
        // Validate course exists
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
        
        // Build specification for approved resources
        Specification<Resource> spec = ResourceSpecifications.forCoursePageFiltering(
            courseId, filter.type(), filter.academicYear(), filter.titleSearch()
        );
        
        // Create sort order
        Sort sort = createSort(filter.sortBy(), filter.sortDirection());
        
        // Get resources
        List<Resource> resources = resourceRepository.findAll(spec, sort);
        
        // Map to public view DTOs
        return resources.stream()
            .map(this::mapToPublicViewDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets approved resources for a course with pagination.
     */
    @Transactional(readOnly = true)
    public Page<ResourceDTO> getCourseResourcesPaginated(Long courseId, ResourceFilterDTO filter) {
        // Validate course exists
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
        
        // Build specification for approved resources
        Specification<Resource> spec = ResourceSpecifications.forCoursePageFiltering(
            courseId, filter.type(), filter.academicYear(), filter.titleSearch()
        );
        
        // Create pageable
        Pageable pageable = PageRequest.of(
            filter.getEffectivePage(), 
            filter.getEffectiveSize(),
            createSort(filter.sortBy(), filter.sortDirection())
        );
        
        // Get resources page
        Page<Resource> resourcePage = resourceRepository.findAll(spec, pageable);
        
        // Map to public view DTOs
        return resourcePage.map(this::mapToPublicViewDTO);
    }
    
    /**
     * Gets all resources uploaded by a specific user.
     */
    @Transactional(readOnly = true)
    public List<ResourceDTO> getUserResources(Long userId, ResourceFilterDTO filter) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        
        // Build specification for user resources
        Specification<Resource> spec = ResourceSpecifications.forUserResources(
            userId, filter.status(), filter.type()
        );
        
        // Create sort order (most recent first by default)
        Sort sort = createSort(filter.sortBy(), filter.sortDirection());
        
        // Get resources
        List<Resource> resources = resourceRepository.findAll(spec, sort);
        
        // Map to owner view DTOs
        return resources.stream()
            .map(this::mapToOwnerViewDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets a specific resource by ID with access control.
     */
    @Transactional(readOnly = true)
    public Optional<ResourceDTO> getResourceById(Long resourceId, Long requestingUserId, boolean isAdmin) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (resourceOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Resource resource = resourceOpt.get();
        
        // Access control: only approved resources are visible to non-owners/non-admins
        if (!isAdmin && !resource.isOwnedBy(userRepository.findById(requestingUserId).orElse(null))) {
            if (!resource.isApproved()) {
                return Optional.empty();
            }
            return Optional.of(mapToPublicViewDTO(resource));
        }
        
        // Admin or owner gets full view
        if (isAdmin) {
            return Optional.of(mapToAdminViewDTO(resource));
        } else {
            return Optional.of(mapToOwnerViewDTO(resource));
        }
    }
    
    /**
     * Gets resource statistics for a course.
     */
    @Transactional(readOnly = true)
    public ResourceStatsDTO getCourseResourceStats(Long courseId) {
        // Validate course exists
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        
        // Get approved resources for the course
        List<Resource> approvedResources = resourceRepository.findByCourseIdAndStatus(
            courseId, ResourceStatus.APPROVED
        );
        
        if (approvedResources.isEmpty()) {
            return ResourceStatsDTO.empty(courseId, course.getName());
        }
        
        // Calculate statistics
        long totalResources = approvedResources.size();
        long examCount = approvedResources.stream().filter(r -> r.getType() == ResourceType.EXAM).count();
        long homeworkCount = approvedResources.stream().filter(r -> r.getType() == ResourceType.HOMEWORK).count();
        long summaryCount = approvedResources.stream().filter(r -> r.getType() == ResourceType.SUMMARY).count();
        long linkCount = approvedResources.stream().filter(r -> r.getType() == ResourceType.LINK).count();
        
        // Get pending count for admin interface
        long pendingCount = resourceRepository.countByCourseIdAndStatus(courseId, ResourceStatus.PENDING);
        
        // Group by academic year
        Map<String, Long> resourcesByYear = approvedResources.stream()
            .filter(r -> r.getAcademicYear() != null)
            .collect(Collectors.groupingBy(
                Resource::getAcademicYear,
                Collectors.counting()
            ));
        
        // Group by type
        Map<ResourceType, Long> resourcesByType = approvedResources.stream()
            .collect(Collectors.groupingBy(
                Resource::getType,
                Collectors.counting()
            ));
        
        return new ResourceStatsDTO(
            courseId, course.getName(), totalResources,
            examCount, homeworkCount, summaryCount, linkCount, pendingCount,
            resourcesByYear, resourcesByType
        );
    }
    
    /**
     * Deletes a resource if the user owns it or is an admin.
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public boolean deleteResource(Long resourceId, Long requestingUserId, boolean isAdmin) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (resourceOpt.isEmpty()) {
            return false;
        }
        
        Resource resource = resourceOpt.get();
        User requestingUser = userRepository.findById(requestingUserId).orElse(null);
        
        // Check ownership or admin privileges
        if (!isAdmin && !resource.isOwnedBy(requestingUser)) {
            throw new IllegalArgumentException("You can only delete your own resources");
        }
        
        resourceRepository.delete(resource);
        return true;
    }
    
    /**
     * Checks if a resource exists and is accessible to the user.
     */
    @Transactional(readOnly = true)
    public boolean canAccessResource(Long resourceId, Long requestingUserId, boolean isAdmin) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (resourceOpt.isEmpty()) {
            return false;
        }
        
        Resource resource = resourceOpt.get();
        User requestingUser = userRepository.findById(requestingUserId).orElse(null);
        
        // Admin can access all resources
        if (isAdmin) {
            return true;
        }
        
        // Owner can access their own resources
        if (resource.isOwnedBy(requestingUser)) {
            return true;
        }
        
        // Others can only access approved resources
        return resource.isApproved();
    }
    
    // ===== MODERATION METHODS =====
    
    /**
     * Approves a resource. Only admins can approve resources.
     * Once approved, the resource becomes visible on course pages.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDTO approveResource(Long resourceId, Long adminId) {
        // Validate admin user
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + adminId));
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only admins can approve resources");
        }
        
        // Get resource
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + resourceId));
        
        // Check if resource is in pending state
        if (!resource.isPending()) {
            throw new IllegalArgumentException("Only pending resources can be approved. Current status: " + resource.getStatus());
        }
        
        // Approve the resource
        resource.approve(admin);
        Resource savedResource = resourceRepository.save(resource);
        
        // Return admin view DTO
        return mapToAdminViewDTO(savedResource);
    }
    
    /**
     * Rejects a resource with a reason. Only admins can reject resources.
     * Rejected resources are not visible on course pages but remain accessible to the uploader.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDTO rejectResource(Long resourceId, Long adminId, RejectResourceRequestDTO rejectRequest) {
        // Validate admin user
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + adminId));
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only admins can reject resources");
        }
        
        // Get resource
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + resourceId));
        
        // Check if resource is in pending state
        if (!resource.isPending()) {
            throw new IllegalArgumentException("Only pending resources can be rejected. Current status: " + resource.getStatus());
        }
        
        // Reject the resource
        resource.reject(admin, rejectRequest.reason());
        Resource savedResource = resourceRepository.save(resource);
        
        // Return admin view DTO
        return mapToAdminViewDTO(savedResource);
    }
    
    /**
     * Gets all pending resources for admin moderation interface.
     * Returns resources awaiting approval/rejection.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ResourceDTO> getPendingResources(ResourceFilterDTO filter) {
        // Build specification for pending resources with optional filters
        Specification<Resource> spec = ResourceSpecifications.forAdminModeration(
            filter.courseId(), filter.type(), filter.uploaderId()
        );
        
        // Create sort order (oldest first for moderation queue)
        Sort sort = createSort(filter.sortBy(), filter.sortDirection());
        
        // Get pending resources
        List<Resource> resources = resourceRepository.findAll(spec, sort);
        
        // Map to admin view DTOs
        return resources.stream()
            .map(this::mapToAdminViewDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets pending resources with pagination for admin interface.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<ResourceDTO> getPendingResourcesPaginated(ResourceFilterDTO filter) {
        // Build specification for pending resources
        Specification<Resource> spec = ResourceSpecifications.forAdminModeration(
            filter.courseId(), filter.type(), filter.uploaderId()
        );
        
        // Create pageable (oldest first for moderation queue)
        Pageable pageable = PageRequest.of(
            filter.getEffectivePage(), 
            filter.getEffectiveSize(),
            createSort(filter.sortBy(), filter.sortDirection())
        );
        
        // Get resources page
        Page<Resource> resourcePage = resourceRepository.findAll(spec, pageable);
        
        // Map to admin view DTOs
        return resourcePage.map(this::mapToAdminViewDTO);
    }
    
    /**
     * Gets moderation statistics for admin dashboard.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ModerationStatsDTO getModerationStats() {
        long pendingCount = resourceRepository.countByStatus(ResourceStatus.PENDING);
        long approvedCount = resourceRepository.countByStatus(ResourceStatus.APPROVED);
        long rejectedCount = resourceRepository.countByStatus(ResourceStatus.REJECTED);
        
        return new ModerationStatsDTO(pendingCount, approvedCount, rejectedCount);
    }
    
    /**
     * Resets a resource status back to pending (admin only).
     * Useful for re-reviewing rejected resources or correcting mistakes.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDTO resetResourceToPending(Long resourceId, Long adminId) {
        // Validate admin user
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with id: " + adminId));
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only admins can reset resource status");
        }
        
        // Get resource
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + resourceId));
        
        // Check if resource is not already pending
        if (resource.isPending()) {
            throw new IllegalArgumentException("Resource is already pending");
        }
        
        // Reset to pending
        resource.setStatus(ResourceStatus.PENDING);
        resource.setApprovedBy(null);
        resource.setApprovedAt(null);
        resource.setRejectionReason(null);
        
        Resource savedResource = resourceRepository.save(resource);
        
        // Return admin view DTO
        return mapToAdminViewDTO(savedResource);
    }
    
    // Private mapping methods
    
    private ResourceDTO mapToPublicViewDTO(Resource resource) {
        return ResourceDTO.forPublicView(
            resource.getId(),
            resource.getTitle(),
            resource.getType(),
            resource.getUrl(),
            resource.getAcademicYear(),
            resource.getExamTerm(),
            resource.getUploadedBy().getDisplayName(),
            resource.getCreatedAt()
        );
    }
    
    private ResourceDTO mapToOwnerViewDTO(Resource resource) {
        return ResourceDTO.forOwnerView(
            resource.getId(),
            resource.getTitle(),
            resource.getType(),
            resource.getUrl(),
            resource.getAcademicYear(),
            resource.getExamTerm(),
            resource.getStatus(),
            resource.getRejectionReason(),
            resource.getCourse().getName(),
            resource.getCreatedAt(),
            resource.getUpdatedAt()
        );
    }
    
    private ResourceDTO mapToAdminViewDTO(Resource resource) {
        return ResourceDTO.forAdminView(
            resource.getId(),
            resource.getTitle(),
            resource.getType(),
            resource.getUrl(),
            resource.getAcademicYear(),
            resource.getExamTerm(),
            resource.getStatus(),
            resource.getRejectionReason(),
            resource.getCourse().getId(),
            resource.getCourse().getName(),
            resource.getUploadedBy().getId(),
            resource.getUploadedBy().getDisplayName(),
            resource.getApprovedBy() != null ? resource.getApprovedBy().getId() : null,
            resource.getApprovedBy() != null ? resource.getApprovedBy().getDisplayName() : null,
            resource.getApprovedAt(),
            resource.getCreatedAt(),
            resource.getUpdatedAt()
        );
    }
    
    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        // Validate sort field
        String validSortBy = switch (sortBy) {
            case "title", "type", "academicYear", "examTerm", "createdAt", "updatedAt" -> sortBy;
            default -> "createdAt";
        };
        
        return Sort.by(direction, validSortBy);
    }
}