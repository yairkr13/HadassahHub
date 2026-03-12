package com.hadassahhub.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadassahhub.backend.dto.BlockUserRequestDTO;
import com.hadassahhub.backend.dto.ChangeRoleRequestDTO;
import com.hadassahhub.backend.dto.SuspendUserRequestDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.entity.UserSuspension;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;
import com.hadassahhub.backend.repository.UserRepository;
import com.hadassahhub.backend.repository.UserSuspensionRepository;
import com.hadassahhub.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Admin User Management endpoints.
 * Tests the full flow from HTTP request to database operations.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Admin User Management Integration Tests")
class AdminUserManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSuspensionRepository suspensionRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User studentUser1;
    private User studentUser2;
    private User moderatorUser;
    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        // Create admin user
        adminUser = new User(
                "admin@edu.hac.ac.il",
                passwordEncoder.encode("password123"),
                "Admin User",
                UserRole.ADMIN
        );
        adminUser = userRepository.save(adminUser);
        adminToken = jwtService.generateToken(adminUser.getEmail());

        // Create student users
        studentUser1 = new User(
                "student1@edu.hac.ac.il",
                passwordEncoder.encode("password123"),
                "Student One"
        );
        studentUser1 = userRepository.save(studentUser1);

        studentUser2 = new User(
                "student2@edu.hac.ac.il",
                passwordEncoder.encode("password123"),
                "Student Two"
        );
        studentUser2 = userRepository.save(studentUser2);
        studentToken = jwtService.generateToken(studentUser2.getEmail());

        // Create moderator user
        moderatorUser = new User(
                "moderator@edu.hac.ac.il",
                passwordEncoder.encode("password123"),
                "Moderator User",
                UserRole.MODERATOR
        );
        moderatorUser = userRepository.save(moderatorUser);
    }

    // ========== GET /api/admin/users Tests ==========

    @Test
    @DisplayName("Should list all users with admin authentication")
    void listUsers_WithAdminAuth_ReturnsUserList() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$.content[*].email", hasItem("admin@edu.hac.ac.il")))
                .andExpect(jsonPath("$.content[*].email", hasItem("student1@edu.hac.ac.il")))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(4)));
    }

    @Test
    @DisplayName("Should filter users by search term")
    void listUsers_WithSearchFilter_ReturnsFilteredUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("search", "Student One")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].fullName", containsString("Student One")));
    }

    @Test
    @DisplayName("Should filter users by role")
    void listUsers_WithRoleFilter_ReturnsFilteredUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("role", "ADMIN")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].role", everyItem(is("ADMIN"))));
    }

    @Test
    @DisplayName("Should filter users by status")
    void listUsers_WithStatusFilter_ReturnsFilteredUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("status", "ACTIVE")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$.content[*].status", everyItem(is("ACTIVE"))));
    }

    @Test
    @DisplayName("Should reject list users request without authentication")
    void listUsers_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject list users request with non-admin role")
    void listUsers_WithStudentAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should support pagination")
    void listUsers_WithPagination_ReturnsPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.pageSize", is(2)));
    }

    // ========== GET /api/admin/users/{id} Tests ==========

    @Test
    @DisplayName("Should get user details by ID")
    void getUserDetails_ExistingUser_ReturnsDetails() throws Exception {
        mockMvc.perform(get("/api/admin/users/" + studentUser1.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentUser1.getId().intValue())))
                .andExpect(jsonPath("$.fullName", is("Student One")))
                .andExpect(jsonPath("$.email", is("student1@edu.hac.ac.il")))
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.resourcesUploaded", notNullValue()))
                .andExpect(jsonPath("$.resourcesApproved", notNullValue()))
                .andExpect(jsonPath("$.resourcesPending", notNullValue()))
                .andExpect(jsonPath("$.resourcesRejected", notNullValue()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent user")
    void getUserDetails_NonExistentUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/users/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject get user details without authentication")
    void getUserDetails_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users/" + studentUser1.getId()))
                .andExpect(status().isUnauthorized());
    }

    // ========== PUT /api/admin/users/{id}/block Tests ==========

    @Test
    @DisplayName("Should block user successfully")
    void blockUser_ValidRequest_BlocksUser() throws Exception {
        BlockUserRequestDTO request = new BlockUserRequestDTO("Policy violation");

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify user is blocked in database
        User blockedUser = userRepository.findById(studentUser1.getId()).orElseThrow();
        assert blockedUser.getStatus() == UserStatus.BLOCKED;
        assert blockedUser.getBlockReason().equals("Policy violation");
        assert blockedUser.getBlockedBy().equals(adminUser.getId());
    }

    @Test
    @DisplayName("Should reject blocking yourself")
    void blockUser_SelfBlock_ReturnsBadRequest() throws Exception {
        BlockUserRequestDTO request = new BlockUserRequestDTO("Test");

        mockMvc.perform(put("/api/admin/users/" + adminUser.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot block yourself")));
    }

    @Test
    @DisplayName("Should reject blocking already blocked user")
    void blockUser_AlreadyBlocked_ReturnsBadRequest() throws Exception {
        // First block the user
        studentUser1.block(adminUser.getId(), "First block");
        userRepository.save(studentUser1);

        BlockUserRequestDTO request = new BlockUserRequestDTO("Second block");

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already blocked")));
    }

    @Test
    @DisplayName("Should reject block request without reason")
    void blockUser_WithoutReason_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ========== PUT /api/admin/users/{id}/suspend Tests ==========

    @Test
    @DisplayName("Should suspend user successfully")
    void suspendUser_ValidRequest_SuspendsUser() throws Exception {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        SuspendUserRequestDTO request = new SuspendUserRequestDTO(
                "Temporary suspension",
                expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/suspend")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify user is suspended in database
        User suspendedUser = userRepository.findById(studentUser1.getId()).orElseThrow();
        assert suspendedUser.getStatus() == UserStatus.SUSPENDED;

        // Verify suspension record created
        UserSuspension suspension = suspensionRepository.findByUserAndIsActiveTrue(suspendedUser).orElseThrow();
        assert suspension.getReason().equals("Temporary suspension");
        assert suspension.getSuspendedBy().equals(adminUser.getId());
    }

    @Test
    @DisplayName("Should reject suspending yourself")
    void suspendUser_SelfSuspend_ReturnsBadRequest() throws Exception {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        SuspendUserRequestDTO request = new SuspendUserRequestDTO(
                "Test",
                expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        mockMvc.perform(put("/api/admin/users/" + adminUser.getId() + "/suspend")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot suspend yourself")));
    }

    @Test
    @DisplayName("Should reject suspending already suspended user")
    void suspendUser_AlreadySuspended_ReturnsBadRequest() throws Exception {
        // First suspend the user
        studentUser1.suspend();
        userRepository.save(studentUser1);
        UserSuspension suspension = new UserSuspension(
                studentUser1,
                adminUser.getId(),
                LocalDateTime.now().plusDays(7),
                "First suspension"
        );
        suspensionRepository.save(suspension);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        SuspendUserRequestDTO request = new SuspendUserRequestDTO(
                "Second suspension",
                expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/suspend")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already suspended")));
    }

    @Test
    @DisplayName("Should reject suspending blocked user")
    void suspendUser_BlockedUser_ReturnsBadRequest() throws Exception {
        // First block the user
        studentUser1.block(adminUser.getId(), "Blocked");
        userRepository.save(studentUser1);

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        SuspendUserRequestDTO request = new SuspendUserRequestDTO(
                "Suspension attempt",
                expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/suspend")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot suspend a blocked user")));
    }

    @Test
    @DisplayName("Should reject past expiration date")
    void suspendUser_PastExpirationDate_ReturnsBadRequest() throws Exception {
        LocalDateTime expiresAt = LocalDateTime.now().minusDays(1);
        SuspendUserRequestDTO request = new SuspendUserRequestDTO(
                "Test",
                expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/suspend")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must be in the future")));
    }

    // ========== PUT /api/admin/users/{id}/activate Tests ==========

    @Test
    @DisplayName("Should activate blocked user successfully")
    void activateUser_BlockedUser_ActivatesUser() throws Exception {
        // First block the user
        studentUser1.block(adminUser.getId(), "Blocked");
        userRepository.save(studentUser1);

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/activate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify user is activated in database
        User activatedUser = userRepository.findById(studentUser1.getId()).orElseThrow();
        assert activatedUser.getStatus() == UserStatus.ACTIVE;
    }

    @Test
    @DisplayName("Should activate suspended user successfully")
    void activateUser_SuspendedUser_ActivatesUser() throws Exception {
        // First suspend the user
        studentUser1.suspend();
        userRepository.save(studentUser1);
        UserSuspension suspension = new UserSuspension(
                studentUser1,
                adminUser.getId(),
                LocalDateTime.now().plusDays(7),
                "Suspended"
        );
        suspensionRepository.save(suspension);

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/activate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verify user is activated in database
        User activatedUser = userRepository.findById(studentUser1.getId()).orElseThrow();
        assert activatedUser.getStatus() == UserStatus.ACTIVE;

        // Verify suspension is lifted
        UserSuspension liftedSuspension = suspensionRepository.findById(suspension.getId()).orElseThrow();
        assert !liftedSuspension.getIsActive();
    }

    @Test
    @DisplayName("Should reject activating already active user")
    void activateUser_AlreadyActive_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/activate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already active")));
    }

    // ========== PUT /api/admin/users/{id}/role Tests ==========

    @Test
    @DisplayName("Should change user role successfully")
    void changeUserRole_ValidRequest_ChangesRole() throws Exception {
        ChangeRoleRequestDTO request = new ChangeRoleRequestDTO(
                UserRole.MODERATOR,
                "Promotion for good contributions"
        );

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify role changed in database
        User updatedUser = userRepository.findById(studentUser1.getId()).orElseThrow();
        assert updatedUser.getRole() == UserRole.MODERATOR;
    }

    @Test
    @DisplayName("Should reject changing your own role")
    void changeUserRole_SelfChange_ReturnsBadRequest() throws Exception {
        ChangeRoleRequestDTO request = new ChangeRoleRequestDTO(
                UserRole.STUDENT,
                "Test"
        );

        mockMvc.perform(put("/api/admin/users/" + adminUser.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot change your own role")));
    }

    @Test
    @DisplayName("Should reject changing to same role")
    void changeUserRole_SameRole_ReturnsBadRequest() throws Exception {
        ChangeRoleRequestDTO request = new ChangeRoleRequestDTO(
                UserRole.STUDENT,
                "Test"
        );

        mockMvc.perform(put("/api/admin/users/" + studentUser1.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already has role")));
    }

    // ========== GET /api/admin/users/{id}/activity Tests ==========

    @Test
    @DisplayName("Should get user activity")
    void getUserActivity_ExistingUser_ReturnsActivity() throws Exception {
        mockMvc.perform(get("/api/admin/users/" + studentUser1.getId() + "/activity")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(studentUser1.getId().intValue())))
                .andExpect(jsonPath("$.resourcesUploaded", notNullValue()))
                .andExpect(jsonPath("$.recentDownloads", notNullValue()))
                .andExpect(jsonPath("$.totalUploads", notNullValue()))
                .andExpect(jsonPath("$.totalDownloads", notNullValue()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent user activity")
    void getUserActivity_NonExistentUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/users/99999/activity")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
