package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.*;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.entity.UserSuspension;
import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;
import com.hadassahhub.backend.repository.ResourceRepository;
import com.hadassahhub.backend.repository.UserRepository;
import com.hadassahhub.backend.repository.UserSuspensionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for admin user management operations.
 * Handles user blocking, suspending, activating, and role changes.
 */
@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserSuspensionRepository suspensionRepository;
    private final ResourceRepository resourceRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    // Note: AuditLogService will be added when audit log feature is implemented

    public AdminUserService(
            UserRepository userRepository,
            UserSuspensionRepository suspensionRepository,
            ResourceRepository resourceRepository
    ) {
        this.userRepository = userRepository;
        this.suspensionRepository = suspensionRepository;
        this.resourceRepository = resourceRepository;
    }

    /**
     * List all users with filtering and pagination.
     * 
     * @param filter Filter criteria (search, role, status)
     * @param pageable Pagination parameters
     * @return Page of AdminUserDTO
     */
    public Page<AdminUserDTO> listUsers(UserFilterDTO filter, Pageable pageable) {
        // Query users with filters
        Page<User> users = userRepository.findWithFilters(
            filter.search(),
            filter.role(),
            filter.status(),
            pageable
        );
        
        // Map to DTOs
        return users.map(user -> {
            // Count resources uploaded by user
            int resourcesUploaded = (int) resourceRepository.countByUploadedById(user.getId());
            
            return new AdminUserDTO(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getLastLogin(),
                resourcesUploaded,
                true // Email verification not implemented yet, default to true
            );
        });
    }

    /**
     * Get detailed information about a specific user.
     * 
     * @param userId The user ID
     * @return AdminUserDetailDTO with full user information
     */
    public AdminUserDetailDTO getUserDetails(Long userId) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Get all resources uploaded by user
        List<Resource> allResources = resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(userId);
        
        // Count resources by status
        int totalUploaded = allResources.size();
        int approved = (int) allResources.stream().filter(r -> r.getStatus() == ResourceStatus.APPROVED).count();
        int pending = (int) allResources.stream().filter(r -> r.getStatus() == ResourceStatus.PENDING).count();
        int rejected = (int) allResources.stream().filter(r -> r.getStatus() == ResourceStatus.REJECTED).count();
        
        // Get last activity (most recent resource upload)
        LocalDateTime lastActivity = allResources.isEmpty() ? null : allResources.get(0).getCreatedAt();
        
        return new AdminUserDetailDTO(
            user.getId(),
            user.getDisplayName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getLastLogin(),
            true, // Email verification not implemented yet
            totalUploaded,
            approved,
            pending,
            rejected,
            0, // Download tracking not implemented yet
            lastActivity
        );
    }

    /**
     * Block a user permanently.
     * 
     * @param userId The user ID to block
     * @param reason The reason for blocking
     * @param adminId The admin performing the action
     */
    public void blockUser(Long userId, String reason, Long adminId) {
        // Validation: Cannot block yourself
        if (userId.equals(adminId)) {
            throw new IllegalArgumentException("Cannot block yourself");
        }
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Validation: User must be active
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new IllegalArgumentException("User is already blocked");
        }
        
        // Block user
        user.block(adminId, reason);
        userRepository.save(user);
        
        // TODO: Create audit log entry when audit log feature is implemented
        // TODO: Invalidate user sessions (future enhancement)
    }

    /**
     * Suspend a user temporarily with expiration date.
     * 
     * @param userId The user ID to suspend
     * @param reason The reason for suspension
     * @param expiresAt When the suspension expires
     * @param adminId The admin performing the action
     */
    public void suspendUser(Long userId, String reason, LocalDateTime expiresAt, Long adminId) {
        // Validation: Cannot suspend yourself
        if (userId.equals(adminId)) {
            throw new IllegalArgumentException("Cannot suspend yourself");
        }
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Validation: User must be active
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("User is already suspended");
        }
        
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new IllegalArgumentException("Cannot suspend a blocked user. Activate first.");
        }
        
        // Validation: Expiration must be in the future
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
        
        // Suspend user
        user.suspend();
        userRepository.save(user);
        
        // Create suspension record
        UserSuspension suspension = new UserSuspension(user, adminId, expiresAt, reason);
        suspensionRepository.save(suspension);
        
        // TODO: Create audit log entry when audit log feature is implemented
        // TODO: Invalidate user sessions (future enhancement)
    }

    /**
     * Activate a blocked or suspended user.
     * 
     * @param userId The user ID to activate
     * @param adminId The admin performing the action
     */
    public void activateUser(Long userId, Long adminId) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Validation: User must be blocked or suspended
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("User is already active");
        }
        
        // If suspended, lift the active suspension
        if (user.getStatus() == UserStatus.SUSPENDED) {
            suspensionRepository.findByUserAndIsActiveTrue(user)
                    .ifPresent(suspension -> {
                        suspension.lift(adminId);
                        suspensionRepository.save(suspension);
                    });
        }
        
        // Activate user
        user.activate();
        userRepository.save(user);
        
        // TODO: Create audit log entry when audit log feature is implemented
    }

    /**
     * Change a user's role.
     * 
     * @param userId The user ID
     * @param newRole The new role to assign
     * @param reason Optional reason for role change
     * @param adminId The admin performing the action
     */
    public void changeUserRole(Long userId, UserRole newRole, String reason, Long adminId) {
        // Validation: Cannot change your own role
        if (userId.equals(adminId)) {
            throw new IllegalArgumentException("Cannot change your own role");
        }
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Validation: Role must be different
        if (user.getRole() == newRole) {
            throw new IllegalArgumentException("User already has role: " + newRole);
        }
        
        // Change role
        user.setRole(newRole);
        userRepository.save(user);
        
        // TODO: Create audit log entry when audit log feature is implemented
        // TODO: Invalidate user sessions to refresh permissions (future enhancement)
    }

    /**
     * Get user activity history (uploads, downloads).
     * 
     * @param userId The user ID
     * @return UserActivityDTO with activity information
     */
    public UserActivityDTO getUserActivity(Long userId) {
        // Find user to ensure they exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Get all resources uploaded by user
        List<Resource> resources = resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(userId);
        
        // Map resources to DTOs
        List<UserActivityDTO.UserResourceDTO> resourceDTOs = resources.stream()
                .map(r -> new UserActivityDTO.UserResourceDTO(
                    r.getId(),
                    r.getTitle(),
                    r.getType().name(),
                    r.getStatus().name(),
                    r.getCreatedAt().format(DATE_FORMATTER)
                ))
                .collect(Collectors.toList());
        
        // Download tracking not implemented yet, return empty list
        List<UserActivityDTO.UserDownloadDTO> downloadDTOs = List.of();
        
        return new UserActivityDTO(
            userId,
            resourceDTOs,
            downloadDTOs,
            resources.size(),
            0 // Download tracking not implemented yet
        );
    }

    /**
     * Get user ID by email.
     * 
     * @param email The user email
     * @return User ID
     */
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return user.getId();
    }

    /**
     * Scheduled task to automatically activate users with expired suspensions.
     * Runs every 5 minutes.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void autoActivateExpiredSuspensions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find all active suspensions that have expired
        List<UserSuspension> expiredSuspensions = suspensionRepository.findExpiredActiveSuspensions(now);
        
        for (UserSuspension suspension : expiredSuspensions) {
            User user = suspension.getUser();
            
            // Only activate if user is still suspended
            if (user.getStatus() == UserStatus.SUSPENDED) {
                // Activate user
                user.activate();
                userRepository.save(user);
                
                // Lift suspension
                suspension.lift(null); // System action, no admin ID
                suspensionRepository.save(suspension);
                
                // TODO: Create audit log entry when audit log feature is implemented
            }
        }
    }
}
