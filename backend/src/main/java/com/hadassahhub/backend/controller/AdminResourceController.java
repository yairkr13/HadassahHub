package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.service.ResourceService;
import com.hadassahhub.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * REST controller for admin resource moderation.
 * Handles approval, rejection, and moderation queue management.
 */
@RestController
@RequestMapping("/api/admin/resources")
@PreAuthorize("hasRole('ADMIN')")
public class AdminResourceController {
    
    private final ResourceService resourceService;
    private final JwtService jwtService;
    
    public AdminResourceController(ResourceService resourceService, JwtService jwtService) {
        this.resourceService = resourceService;
        this.jwtService = jwtService;
    }
    
    /**
     * Gets all pending resources for moderation.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ResourceDTO>> getPendingResources(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long uploaderId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            courseId, parseResourceType(type), null, null, null, null, uploaderId,
            null, null, sortBy, sortDirection
        );
        
        List<ResourceDTO> pendingResources = resourceService.getPendingResources(filter);
        return ResponseEntity.ok(pendingResources);
    }
    
    /**
     * Gets pending resources with pagination.
     */
    @GetMapping("/pending/paginated")
    public ResponseEntity<Page<ResourceDTO>> getPendingResourcesPaginated(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long uploaderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            courseId, parseResourceType(type), null, null, null, null, uploaderId,
            page, size, sortBy, sortDirection
        );
        
        Page<ResourceDTO> pendingResources = resourceService.getPendingResourcesPaginated(filter);
        return ResponseEntity.ok(pendingResources);
    }
    
    /**
     * Approves a pending resource.
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ResourceDTO> approveResource(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        
        try {
            ResourceDTO approvedResource = resourceService.approveResource(id, adminId);
            return ResponseEntity.ok(approvedResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Rejects a pending resource with a reason.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ResourceDTO> rejectResource(
            @PathVariable Long id,
            @Valid @RequestBody RejectResourceRequestDTO rejectRequest,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        
        try {
            ResourceDTO rejectedResource = resourceService.rejectResource(id, adminId, rejectRequest);
            return ResponseEntity.ok(rejectedResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Resets a resource status back to pending.
     */
    @PostMapping("/{id}/reset")
    public ResponseEntity<ResourceDTO> resetResourceToPending(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        
        try {
            ResourceDTO resetResource = resourceService.resetResourceToPending(id, adminId);
            return ResponseEntity.ok(resetResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Gets moderation statistics for admin dashboard.
     */
    @GetMapping("/stats")
    public ResponseEntity<ModerationStatsDTO> getModerationStats() {
        ModerationStatsDTO stats = resourceService.getModerationStats();
        return ResponseEntity.ok(stats);
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
}