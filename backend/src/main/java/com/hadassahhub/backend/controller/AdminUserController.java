package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;
import com.hadassahhub.backend.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for admin user management operations.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * List all users with filtering and pagination.
     * 
     * @param search Search by name or email (optional)
     * @param role Filter by role (optional)
     * @param status Filter by status (optional)
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param sortBy Sort field (default: createdAt)
     * @param sortDirection Sort direction (default: desc)
     * @return Page of AdminUserDTO
     */
    @GetMapping
    public ResponseEntity<Page<AdminUserDTO>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        // Create filter DTO
        UserFilterDTO filter = new UserFilterDTO(search, role, status);
        
        // Create pageable with sorting
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Call service
        Page<AdminUserDTO> users = adminUserService.listUsers(filter, pageable);
        
        return ResponseEntity.ok(users);
    }

    /**
     * Get detailed information about a specific user.
     * 
     * @param id User ID
     * @return AdminUserDetailDTO with full user information
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserDetailDTO> getUserDetails(@PathVariable Long id) {
        AdminUserDetailDTO userDetails = adminUserService.getUserDetails(id);
        return ResponseEntity.ok(userDetails);
    }

    /**
     * Block a user permanently.
     * 
     * @param id User ID to block
     * @param request Block request with reason
     * @param authentication Current admin authentication
     * @return 200 OK if successful
     */
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(
            @PathVariable Long id,
            @Valid @RequestBody BlockUserRequestDTO request,
            Authentication authentication
    ) {
        // Extract admin ID from authentication
        Long adminId = extractUserId(authentication);
        
        // Call service
        adminUserService.blockUser(id, request.reason(), adminId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Suspend a user temporarily with expiration date.
     * 
     * @param id User ID to suspend
     * @param request Suspend request with reason and expiration
     * @param authentication Current admin authentication
     * @return 200 OK if successful
     */
    @PutMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendUser(
            @PathVariable Long id,
            @Valid @RequestBody SuspendUserRequestDTO request,
            Authentication authentication
    ) {
        // Extract admin ID from authentication
        Long adminId = extractUserId(authentication);
        
        // Parse expiration date from string
        LocalDateTime expiresAt = LocalDateTime.parse(request.expiresAt());
        
        // Call service
        adminUserService.suspendUser(id, request.reason(), expiresAt, adminId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Activate a blocked or suspended user.
     * 
     * @param id User ID to activate
     * @param authentication Current admin authentication
     * @return 200 OK if successful
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(
            @PathVariable Long id,
            Authentication authentication
    ) {
        // Extract admin ID from authentication
        Long adminId = extractUserId(authentication);
        
        // Call service
        adminUserService.activateUser(id, adminId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Change a user's role.
     * 
     * @param id User ID
     * @param request Role change request with new role and optional reason
     * @param authentication Current admin authentication
     * @return 200 OK if successful
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<Void> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequestDTO request,
            Authentication authentication
    ) {
        // Extract admin ID from authentication
        Long adminId = extractUserId(authentication);
        
        // Call service
        adminUserService.changeUserRole(id, request.newRole(), request.reason(), adminId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Get user activity history (uploads, downloads).
     * 
     * @param id User ID
     * @return UserActivityDTO with activity information
     */
    @GetMapping("/{id}/activity")
    public ResponseEntity<UserActivityDTO> getUserActivity(@PathVariable Long id) {
        UserActivityDTO activity = adminUserService.getUserActivity(id);
        return ResponseEntity.ok(activity);
    }

    /**
     * Extract user ID from authentication object.
     * 
     * @param authentication Spring Security authentication
     * @return User ID
     */
    private Long extractUserId(Authentication authentication) {
        String email = authentication.getName(); // Principal is the email
        return adminUserService.getUserIdByEmail(email);
    }
}
