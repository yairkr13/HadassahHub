package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.service.ResourceService;
import com.hadassahhub.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for resource management.
 * Handles CRUD operations for resources with proper security and validation.
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    
    private final ResourceService resourceService;
    private final JwtService jwtService;
    
    public ResourceController(ResourceService resourceService, JwtService jwtService) {
        this.resourceService = resourceService;
        this.jwtService = jwtService;
    }
    
    /**
     * Creates a new resource (supports both URL and file uploads).
     * Accepts multipart/form-data for file uploads or JSON for URL resources.
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ResourceDTO> createResource(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "data") @Valid CreateResourceRequestDTO request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        
        // Validate mutual exclusivity: exactly one of url or file must be provided
        boolean hasFile = file != null && !file.isEmpty();
        if (!request.hasUrlOrFile(hasFile)) {
            if (hasFile && request.url() != null && !request.url().isEmpty()) {
                return ResponseEntity.badRequest().build(); // Both provided
            } else {
                return ResponseEntity.badRequest().build(); // Neither provided
            }
        }
        
        ResourceDTO createdResource = resourceService.createResource(request, file, userId);
        
        return ResponseEntity
            .created(URI.create("/api/resources/" + createdResource.id()))
            .body(createdResource);
    }
    
    /**
     * Gets user's own resources with optional filtering.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<List<ResourceDTO>> getMyResources(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        
        ResourceFilterDTO filter = new ResourceFilterDTO(
            null, parseResourceType(type), parseResourceStatus(status), 
            null, null, null, userId,
            null, null, sortBy, sortDirection
        );
        
        List<ResourceDTO> resources = resourceService.getUserResources(userId, filter);
        return ResponseEntity.ok(resources);
    }
    
    /**
     * Gets a specific resource by ID with access control.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ResourceDTO> getResource(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        boolean isAdmin = hasAdminRole(authentication);
        
        Optional<ResourceDTO> resource = resourceService.getResourceById(id, userId, isAdmin);
        
        return resource.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Deletes a resource (owner or admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        boolean isAdmin = hasAdminRole(authentication);
        
        try {
            boolean deleted = resourceService.deleteResource(id, userId, isAdmin);
            return deleted ? ResponseEntity.noContent().build() 
                          : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    /**
     * Gets resource access URL (redirects to external URL for MVP).
     */
    @GetMapping("/{id}/access")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> accessResource(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        boolean isAdmin = hasAdminRole(authentication);
        
        if (!resourceService.canAccessResource(id, userId, isAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<ResourceDTO> resource = resourceService.getResourceById(id, userId, isAdmin);
        
        if (resource.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                               .location(URI.create(resource.get().url()))
                               .build();
        }
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Downloads a file resource.
     * Returns the file with appropriate headers for download.
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        boolean isAdmin = hasAdminRole(authentication);
        
        try {
            // Get resource metadata
            Optional<ResourceDTO> resourceDTO = resourceService.getResourceById(id, userId, isAdmin);
            if (resourceDTO.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Verify it's a file resource
            if (!resourceDTO.get().isFileResource()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Get file
            Resource file = resourceService.getFileForDownload(id, userId, isAdmin);
            
            // Set headers
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceDTO.get().mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + resourceDTO.get().fileName() + "\"")
                .body(file);
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
    
    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
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