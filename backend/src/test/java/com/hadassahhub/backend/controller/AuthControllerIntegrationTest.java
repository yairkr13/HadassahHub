package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.AuthResponseDTO;
import com.hadassahhub.backend.dto.LoginRequestDTO;
import com.hadassahhub.backend.dto.RegisterRequestDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.repository.UserRepository;
import com.hadassahhub.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_WithValidStudentData_ShouldReturnSuccess() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "student@edu.jmc.ac.il",
                "password123",
                "Test Student"
        );

        AuthResponseDTO response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("student@edu.jmc.ac.il", response.user().email());
        assertEquals("Test Student", response.user().displayName());
        assertEquals(UserRole.STUDENT, response.user().role());
        assertEquals(0, response.user().pointsBalance());
    }   
 @Test
    void register_WithValidHacData_ShouldReturnSuccess() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "student@edu.hac.ac.il",
                "password123",
                "HAC Student"
        );

        AuthResponseDTO response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("student@edu.hac.ac.il", response.user().email());
        assertEquals("HAC Student", response.user().displayName());
        assertEquals(UserRole.STUDENT, response.user().role());
    }

    @Test
    void register_WithInvalidEmailDomain_ShouldThrowException() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "student@gmail.com",
                "password123",
                "Invalid Student"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowException() {
        // Create existing user
        User existingUser = new User();
        existingUser.setEmail("existing@edu.jmc.ac.il");
        existingUser.setPasswordHash(passwordEncoder.encode("password123"));
        existingUser.setDisplayName("Existing User");
        existingUser.setRole(UserRole.STUDENT);
        userRepository.save(existingUser);

        RegisterRequestDTO request = new RegisterRequestDTO(
                "existing@edu.jmc.ac.il",
                "newpassword",
                "New User"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccess() {
        // Create test user
        User testUser = new User();
        testUser.setEmail("test@edu.jmc.ac.il");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setDisplayName("Test User");
        testUser.setRole(UserRole.STUDENT);
        userRepository.save(testUser);

        LoginRequestDTO request = new LoginRequestDTO(
                "test@edu.jmc.ac.il",
                "password123"
        );

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("test@edu.jmc.ac.il", response.user().email());
        assertEquals("Test User", response.user().displayName());
        assertEquals(UserRole.STUDENT, response.user().role());
    }    @
Test
    void login_WithInvalidEmail_ShouldThrowException() {
        LoginRequestDTO request = new LoginRequestDTO(
                "nonexistent@edu.jmc.ac.il",
                "password123"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        // Create test user
        User testUser = new User();
        testUser.setEmail("test@edu.jmc.ac.il");
        testUser.setPasswordHash(passwordEncoder.encode("correctpassword"));
        testUser.setDisplayName("Test User");
        testUser.setRole(UserRole.STUDENT);
        userRepository.save(testUser);

        LoginRequestDTO request = new LoginRequestDTO(
                "test@edu.jmc.ac.il",
                "wrongpassword"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_WithAdminUser_ShouldReturnSuccessWithAdminRole() {
        // Create admin user
        User adminUser = new User();
        adminUser.setEmail("admin@edu.jmc.ac.il");
        adminUser.setPasswordHash(passwordEncoder.encode("adminpass"));
        adminUser.setDisplayName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        userRepository.save(adminUser);

        LoginRequestDTO request = new LoginRequestDTO(
                "admin@edu.jmc.ac.il",
                "adminpass"
        );

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("admin@edu.jmc.ac.il", response.user().email());
        assertEquals("Admin User", response.user().displayName());
        assertEquals(UserRole.ADMIN, response.user().role());
    }

    @Test
    void authenticationFlow_RegisterThenLogin_ShouldWork() {
        // Register a new user
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "newuser@edu.jmc.ac.il",
                "password123",
                "New User"
        );

        AuthResponseDTO registerResponse = authService.register(registerRequest);
        assertNotNull(registerResponse);
        assertNotNull(registerResponse.token());

        // Login with the same credentials
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "newuser@edu.jmc.ac.il",
                "password123"
        );

        AuthResponseDTO loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.token());
        assertEquals("newuser@edu.jmc.ac.il", loginResponse.user().email());
        assertEquals("New User", loginResponse.user().displayName());
    }
}