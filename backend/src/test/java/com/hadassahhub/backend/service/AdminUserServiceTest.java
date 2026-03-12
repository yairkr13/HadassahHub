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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserService Unit Tests")
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSuspensionRepository suspensionRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    private User testStudent;
    private User testAdmin;
    private User testModerator;

    @BeforeEach
    void setUp() {
        testStudent = new User("student@edu.hac.ac.il", "hashedPassword", "Test Student");
        ReflectionTestUtils.setField(testStudent, "id", 1L);
        ReflectionTestUtils.setField(testStudent, "createdAt", LocalDateTime.now().minusDays(30));
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.ACTIVE);

        testAdmin = new User("admin@edu.hac.ac.il", "hashedPassword", "Test Admin", UserRole.ADMIN);
        ReflectionTestUtils.setField(testAdmin, "id", 2L);
        ReflectionTestUtils.setField(testAdmin, "createdAt", LocalDateTime.now().minusDays(60));
        ReflectionTestUtils.setField(testAdmin, "status", UserStatus.ACTIVE);

        testModerator = new User("moderator@edu.hac.ac.il", "hashedPassword", "Test Moderator", UserRole.MODERATOR);
        ReflectionTestUtils.setField(testModerator, "id", 3L);
        ReflectionTestUtils.setField(testModerator, "createdAt", LocalDateTime.now().minusDays(45));
        ReflectionTestUtils.setField(testModerator, "status", UserStatus.ACTIVE);
    }

    // ========== listUsers Tests ==========

    @Test
    @DisplayName("Should list all users without filters")
    void listUsers_NoFilters_ReturnsAllUsers() {
        // Given
        UserFilterDTO filter = new UserFilterDTO(null, null, null);
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = List.of(testStudent, testAdmin, testModerator);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findWithFilters(null, null, null, pageable)).thenReturn(userPage);
        when(resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(anyLong())).thenReturn(new ArrayList<>());

        // When
        Page<AdminUserDTO> result = adminUserService.listUsers(filter, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        verify(userRepository).findWithFilters(null, null, null, pageable);
    }

    @Test
    @DisplayName("Should filter users by search term")
    void listUsers_WithSearchFilter_ReturnsFilteredUsers() {
        // Given
        UserFilterDTO filter = new UserFilterDTO("student", null, null);
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = List.of(testStudent);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findWithFilters("student", null, null, pageable)).thenReturn(userPage);
        when(resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

        // When
        Page<AdminUserDTO> result = adminUserService.listUsers(filter, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).fullName()).isEqualTo("Test Student");
        verify(userRepository).findWithFilters("student", null, null, pageable);
    }

    @Test
    @DisplayName("Should filter users by role")
    void listUsers_WithRoleFilter_ReturnsFilteredUsers() {
        // Given
        UserFilterDTO filter = new UserFilterDTO(null, UserRole.ADMIN, null);
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = List.of(testAdmin);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findWithFilters(null, UserRole.ADMIN, null, pageable)).thenReturn(userPage);
        when(resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(2L)).thenReturn(new ArrayList<>());

        // When
        Page<AdminUserDTO> result = adminUserService.listUsers(filter, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).role()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).findWithFilters(null, UserRole.ADMIN, null, pageable);
    }

    @Test
    @DisplayName("Should filter users by status")
    void listUsers_WithStatusFilter_ReturnsFilteredUsers() {
        // Given
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.BLOCKED);
        UserFilterDTO filter = new UserFilterDTO(null, null, UserStatus.BLOCKED);
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = List.of(testStudent);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findWithFilters(null, null, UserStatus.BLOCKED, pageable)).thenReturn(userPage);
        when(resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

        // When
        Page<AdminUserDTO> result = adminUserService.listUsers(filter, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).status()).isEqualTo(UserStatus.BLOCKED);
        verify(userRepository).findWithFilters(null, null, UserStatus.BLOCKED, pageable);
    }

    // ========== getUserDetails Tests ==========

    @Test
    @DisplayName("Should get user details with resource statistics")
    void getUserDetails_ExistingUser_ReturnsDetails() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(userId)).thenReturn(new ArrayList<>());

        // When
        AdminUserDetailDTO result = adminUserService.getUserDetails(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.fullName()).isEqualTo("Test Student");
        assertThat(result.email()).isEqualTo("student@edu.hac.ac.il");
        assertThat(result.role()).isEqualTo(UserRole.STUDENT);
        assertThat(result.status()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).findById(userId);
        verify(resourceRepository).findByUploadedByIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserDetails_NonExistentUser_ThrowsException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminUserService.getUserDetails(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
        verify(resourceRepository, never()).findByUploadedByIdOrderByCreatedAtDesc(anyLong());
    }

    // ========== blockUser Tests ==========

    @Test
    @DisplayName("Should block active user successfully")
    void blockUser_ActiveUser_BlocksSuccessfully() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        String reason = "Policy violation";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // When
        adminUserService.blockUser(userId, reason, adminId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(testStudent);
        assertThat(testStudent.getStatus()).isEqualTo(UserStatus.BLOCKED);
        assertThat(testStudent.getBlockReason()).isEqualTo(reason);
        assertThat(testStudent.getBlockedBy()).isEqualTo(adminId);
        assertThat(testStudent.getBlockedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should not allow admin to block themselves")
    void blockUser_SelfBlock_ThrowsException() {
        // Given
        Long userId = 2L;
        Long adminId = 2L;
        String reason = "Test";

        // When & Then
        assertThatThrownBy(() -> adminUserService.blockUser(userId, reason, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot block yourself");

        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should not block already blocked user")
    void blockUser_AlreadyBlocked_ThrowsException() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        String reason = "Test";
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.BLOCKED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> adminUserService.blockUser(userId, reason, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already blocked");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when blocking non-existent user")
    void blockUser_NonExistentUser_ThrowsException() {
        // Given
        Long userId = 999L;
        Long adminId = 2L;
        String reason = "Test";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminUserService.blockUser(userId, reason, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== suspendUser Tests ==========

    @Test
    @DisplayName("Should suspend active user successfully")
    void suspendUser_ActiveUser_SuspendsSuccessfully() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        String reason = "Temporary suspension";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenReturn(testStudent);
        when(suspensionRepository.save(any(UserSuspension.class))).thenReturn(null);

        // When
        adminUserService.suspendUser(userId, reason, expiresAt, adminId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(testStudent);
        verify(suspensionRepository).save(any(UserSuspension.class));
        assertThat(testStudent.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    @DisplayName("Should not allow admin to suspend themselves")
    void suspendUser_SelfSuspend_ThrowsException() {
        // Given
        Long userId = 2L;
        Long adminId = 2L;
        String reason = "Test";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // When & Then
        assertThatThrownBy(() -> adminUserService.suspendUser(userId, reason, expiresAt, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot suspend yourself");

        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should not suspend already suspended user")
    void suspendUser_AlreadySuspended_ThrowsException() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        String reason = "Test";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.SUSPENDED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> adminUserService.suspendUser(userId, reason, expiresAt, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already suspended");

        verify(userRepository).findById(userId);
        verify(suspensionRepository, never()).save(any(UserSuspension.class));
    }

    @Test
    @DisplayName("Should not suspend blocked user")
    void suspendUser_BlockedUser_ThrowsException() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        String reason = "Test";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.BLOCKED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> adminUserService.suspendUser(userId, reason, expiresAt, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot suspend a blocked user");

        verify(userRepository).findById(userId);
        verify(suspensionRepository, never()).save(any(UserSuspension.class));
    }

    @Test
    @DisplayName("Should reject past expiration date")
    void suspendUser_PastExpirationDate_ThrowsException() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        String reason = "Test";
        LocalDateTime expiresAt = LocalDateTime.now().minusDays(1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> adminUserService.suspendUser(userId, reason, expiresAt, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be in the future");

        verify(userRepository).findById(userId);
        verify(suspensionRepository, never()).save(any(UserSuspension.class));
    }

    // ========== activateUser Tests ==========

    @Test
    @DisplayName("Should activate blocked user successfully")
    void activateUser_BlockedUser_ActivatesSuccessfully() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.BLOCKED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // When
        adminUserService.activateUser(userId, adminId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(testStudent);
        assertThat(testStudent.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should activate suspended user and lift suspension")
    void activateUser_SuspendedUser_ActivatesAndLiftsSuspension() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.SUSPENDED);

        UserSuspension suspension = mock(UserSuspension.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(suspensionRepository.findByUserAndIsActiveTrue(testStudent)).thenReturn(Optional.of(suspension));
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // When
        adminUserService.activateUser(userId, adminId);

        // Then
        verify(userRepository).findById(userId);
        verify(suspensionRepository).findByUserAndIsActiveTrue(testStudent);
        verify(suspension).lift(adminId);
        verify(suspensionRepository).save(suspension);
        verify(userRepository).save(testStudent);
        assertThat(testStudent.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should not activate already active user")
    void activateUser_AlreadyActive_ThrowsException() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> adminUserService.activateUser(userId, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already active");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== changeUserRole Tests ==========

    @Test
    @DisplayName("Should change user role successfully")
    void changeUserRole_ValidRequest_ChangesRole() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        UserRole newRole = UserRole.MODERATOR;
        String reason = "Promotion";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // When
        adminUserService.changeUserRole(userId, newRole, reason, adminId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(testStudent);
        assertThat(testStudent.getRole()).isEqualTo(UserRole.MODERATOR);
    }

    @Test
    @DisplayName("Should not allow admin to change their own role")
    void changeUserRole_SelfChange_ThrowsException() {
        // Given
        Long userId = 2L;
        Long adminId = 2L;
        UserRole newRole = UserRole.STUDENT;
        String reason = "Test";

        // When & Then
        assertThatThrownBy(() -> adminUserService.changeUserRole(userId, newRole, reason, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot change your own role");

        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should not change to same role")
    void changeUserRole_SameRole_ThrowsException() {
        // Given
        Long userId = 1L;
        Long adminId = 2L;
        UserRole newRole = UserRole.STUDENT;
        String reason = "Test";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThatThrownBy(() -> adminUserService.changeUserRole(userId, newRole, reason, adminId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already has role");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== getUserActivity Tests ==========

    @Test
    @DisplayName("Should get user activity with resources")
    void getUserActivity_ExistingUser_ReturnsActivity() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testStudent));
        when(resourceRepository.findByUploadedByIdOrderByCreatedAtDesc(userId)).thenReturn(new ArrayList<>());

        // When
        UserActivityDTO result = adminUserService.getUserActivity(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.resourcesUploaded()).isEmpty();
        assertThat(result.totalUploads()).isEqualTo(0);
        verify(userRepository).findById(userId);
        verify(resourceRepository).findByUploadedByIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("Should throw exception when getting activity for non-existent user")
    void getUserActivity_NonExistentUser_ThrowsException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminUserService.getUserActivity(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
        verify(resourceRepository, never()).findByUploadedByIdOrderByCreatedAtDesc(anyLong());
    }

    // ========== autoActivateExpiredSuspensions Tests ==========

    @Test
    @DisplayName("Should auto-activate expired suspensions")
    void autoActivateExpiredSuspensions_ExpiredSuspensions_ActivatesUsers() {
        // Given
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.SUSPENDED);
        UserSuspension expiredSuspension = mock(UserSuspension.class);
        when(expiredSuspension.getUser()).thenReturn(testStudent);

        List<UserSuspension> expiredSuspensions = List.of(expiredSuspension);
        when(suspensionRepository.findExpiredActiveSuspensions(any(LocalDateTime.class)))
                .thenReturn(expiredSuspensions);

        // When
        adminUserService.autoActivateExpiredSuspensions();

        // Then
        verify(suspensionRepository).findExpiredActiveSuspensions(any(LocalDateTime.class));
        verify(userRepository).save(testStudent);
        verify(expiredSuspension).lift(null);
        verify(suspensionRepository).save(expiredSuspension);
        assertThat(testStudent.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should not activate user if status changed")
    void autoActivateExpiredSuspensions_StatusChanged_DoesNotActivate() {
        // Given
        ReflectionTestUtils.setField(testStudent, "status", UserStatus.ACTIVE);
        UserSuspension expiredSuspension = mock(UserSuspension.class);
        when(expiredSuspension.getUser()).thenReturn(testStudent);

        List<UserSuspension> expiredSuspensions = List.of(expiredSuspension);
        when(suspensionRepository.findExpiredActiveSuspensions(any(LocalDateTime.class)))
                .thenReturn(expiredSuspensions);

        // When
        adminUserService.autoActivateExpiredSuspensions();

        // Then
        verify(suspensionRepository).findExpiredActiveSuspensions(any(LocalDateTime.class));
        verify(userRepository, never()).save(any(User.class));
        verify(expiredSuspension, never()).lift(any());
    }
}
