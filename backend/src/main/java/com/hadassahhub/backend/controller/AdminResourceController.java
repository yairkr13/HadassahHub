package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.service.JwtService;
import com.hadassahhub.backend.service.ResourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for admin resource moderation.
 * Handles approval, rejection, and listing of pending resources.
 * 
 * Security: All endpoints require ADMIN or MODERATOR role.
 */
@RestController
@RequestMapping("/api/admin/resources")
public class AdminResourceController {
    
    private final ResourceService resourceService;
    private final JwtService jwtService;
    
    public AdminResourceController(ResourceService resourceService, JwtService jwtService) {
        this.resourceService = resourceService;
        this.jwtService = jwtService;
    }
    
    /**
     * Gets all pending resources for moderation.
     * Returns resources awaiting approval/rejection.
     * 
     * Query Parameters:
     * - courseId: Filter by course (optional)
     * - type: Filter by resource type (optional)
     * - uploaderId: Filter by uploader (optional)
     * - sortBy: Sort field (default: createdAt)
     * - sortDirection: Sort direction (default: asc - oldest first)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<ResourceDTO>> getPendingResources(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long uploaderId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Authentication authentication) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            courseId, parseResourceType(type), null, null, null, null, uploaderId,
            null, null, sortBy, sortDirection
        );
        
        List<ResourceDTO> pendingResources = resourceService.getPendingResources(filter);
        return ResponseEntity.ok(pendingResources);
    }
    
    /**
     * Gets all pending resources with pagination.
     */
    @GetMapping("/pending/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Page<ResourceDTO>> getPendingResourcesPaginated(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long uploaderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Authentication authentication) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            courseId, parseResourceType(type), null, null, null, null, uploaderId,
            page, size, sortBy, sortDirection
        );
        
        Page<ResourceDTO> pendingResources = resourceService.getPendingResourcesPaginated(filter);
        return ResponseEntity.ok(pendingResources);
    }
    
    /**
     * Approves a pending resource.
     * Once approved, the resource becomes visible to all users on course pages.
     * 
     * @param id Resource ID to approve
     * @return The approved resource with updated status
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> approveResource(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            Long adminId = getUserIdFromAuth(authentication);
            ResourceDTO approvedResource = resourceService.approveResource(id, adminId);
            return ResponseEntity.ok(approvedResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Bad Request", e.getMessage(), LocalDateTime.now()));
        }
    }
    
    /**
     * Rejects a pending resource with a reason.
     * Rejected resources remain visible only to the uploader and admins.
     * 
     * @param id Resource ID to reject
     * @param request Rejection request containing the reason
     * @return The rejected resource with updated status and rejection reason
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> rejectResource(
            @PathVariable Long id,
            @Valid @RequestBody RejectResourceRequestDTO request,
            Authentication authentication) {
        
        try {
            Long adminId = getUserIdFromAuth(authentication);
            ResourceDTO rejectedResource = resourceService.rejectResource(id, adminId, request);
            return ResponseEntity.ok(rejectedResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Bad Request", e.getMessage(), LocalDateTime.now()));
        }
    }
    
    /**
     * Resets a resource status back to pending.
     * Useful for re-reviewing rejected resources or correcting mistakes.
     * 
     * @param id Resource ID to reset
     * @return The resource with status reset to PENDING
     */
    @PutMapping("/{id}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetResourceToPending(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            Long adminId = getUserIdFromAuth(authentication);
            ResourceDTO resetResource = resourceService.resetResourceToPending(id, adminId);
            return ResponseEntity.ok(resetResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Bad Request", e.getMessage(), LocalDateTime.now()));
        }
    }
    
    /**
     * Gets moderation statistics for the admin dashboard.
     * Returns counts of pending, approved, and rejected resources.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ModerationStatsDTO> getModerationStats() {
        ModerationStatsDTO stats = resourceService.getModerationStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Gets all resources (pending, approved, rejected) with filtering.
     * Admin-only endpoint for comprehensive resource management.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResourceDTO>> getAllResources(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long uploaderId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            courseId, parseResourceType(type), parseResourceStatus(status), 
            null, null, null, uploaderId,
            null, null, sortBy, sortDirection
        );
        
        // Use admin view to get all resources
        Long adminId = getUserIdFromAuth(authentication);
        List<ResourceDTO> resources = resourceService.getUserResources(uploaderId != null ? uploaderId : adminId, filter);
        return ResponseEntity.ok(resources);
    }
    
    /**
     * Deletes a resource permanently.
     * Also deletes the physical file if it's a file upload.
     * 
     * @param id Resource ID to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteResource(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            Long adminId = getUserIdFromAuth(authentication);
            boolean deleted = resourceService.deleteResource(id, adminId, true);
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Forbidden", e.getMessage(), LocalDateTime.now()));
        }
    }
    
    // Helper methods
    
    private Long getUserIdFromAuth(Authentication authentication) {
        // Get the JWT token from the request to extract userId claim
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            return jwtService.extractUserId(jwt);
        }
        
        throw new IllegalStateException("Unable to extract user ID from authentication");
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
    
    private com.hadassahhub.backend.enums.ResourceStatus parseResourceStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return com.hadassahhub.backend.enums.ResourceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
