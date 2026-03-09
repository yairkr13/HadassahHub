package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;
import com.hadassahhub.backend.service.CourseService;
import com.hadassahhub.backend.service.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final ResourceService resourceService;

    public CourseController(CourseService courseService, ResourceService resourceService) {
        this.courseService = courseService;
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<CourseDTO> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) StudyYear year
    ) {
        return courseService.findCourses(search, category, year);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return courseService.findCourseById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Course with id " + id + " not found"));
    }
    
    /**
     * Gets approved resources for a course with filtering.
     */
    @GetMapping("/{id}/resources")
    public ResponseEntity<List<ResourceDTO>> getCourseResources(
            @PathVariable Long id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            id, parseResourceType(type), null, academicYear, null, search, null,
            null, null, sortBy, sortDirection
        );
        
        try {
            List<ResourceDTO> resources = resourceService.getCourseResources(id, filter);
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Gets course resources with pagination.
     */
    @GetMapping("/{id}/resources/paginated")
    public ResponseEntity<Page<ResourceDTO>> getCourseResourcesPaginated(
            @PathVariable Long id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            id, parseResourceType(type), null, academicYear, null, search, null,
            page, size, sortBy, sortDirection
        );
        
        try {
            Page<ResourceDTO> resources = resourceService.getCourseResourcesPaginated(id, filter);
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Gets resource statistics for a course.
     */
    @GetMapping("/{id}/resources/stats")
    public ResponseEntity<ResourceStatsDTO> getCourseResourceStats(@PathVariable Long id) {
        try {
            ResourceStatsDTO stats = resourceService.getCourseResourceStats(id);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Creates a resource for a specific course.
     */
    @PostMapping("/{id}/resources")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ResourceDTO> createCourseResource(
            @PathVariable Long id,
            @Valid @RequestBody CreateResourceRequestDTO request,
            Authentication authentication) {
        
        // Ensure the course ID matches the path parameter
        CreateResourceRequestDTO courseSpecificRequest = new CreateResourceRequestDTO(
            id, request.title(), request.type(), request.url(), 
            request.academicYear(), request.examTerm()
        );
        
        Long userId = getUserIdFromAuth(authentication);
        
        try {
            ResourceDTO createdResource = resourceService.createResource(courseSpecificRequest, null, userId);
            return ResponseEntity
                .created(URI.create("/api/resources/" + createdResource.id()))
                .body(createdResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Helper methods
    
    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
    
    private com.hadassahhub.backend.enums.ResourceType parseResourceType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        try {
            return com.hadassahhub.backend.enums.ResourceType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
