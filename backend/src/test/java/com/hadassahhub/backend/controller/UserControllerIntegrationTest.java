package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.UserDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.repository.UserRepository;
import com.hadassahhub.backend.service.JwtService;
import com.hadassahhub.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        // Create test user
        testUser = new User();
        testUser.setEmail("test@edu.jmc.ac.il");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setDisplayName("Test User");
        testUser.setRole(UserRole.STUDENT);
        testUser.setPointsBalance(100);
        testUser = userRepository.save(testUser);
        
        // Generate valid token
        validToken = jwtService.generateToken(testUser);
    }   
 @Test
    void getCurrentUser_WithValidUserId_ShouldReturnUserInfo() {
        Long userId = jwtService.extractUserId(validToken);
        
        Optional<UserDTO> userDTOOpt = userService.findById(userId);
        assertTrue(userDTOOpt.isPresent());
        
        UserDTO userDTO = userDTOOpt.get();
        assertEquals("test@edu.jmc.ac.il", userDTO.email());
        assertEquals("Test User", userDTO.displayName());
        assertEquals(UserRole.STUDENT, userDTO.role());
        assertEquals(100, userDTO.pointsBalance());
    }

    @Test
    void getCurrentUser_WithAdminUser_ShouldReturnAdminInfo() {
        // Create admin user
        User adminUser = new User();
        adminUser.setEmail("admin@edu.jmc.ac.il");
        adminUser.setPasswordHash(passwordEncoder.encode("adminpass"));
        adminUser.setDisplayName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setPointsBalance(500);
        adminUser = userRepository.save(adminUser);
        
        String adminToken = jwtService.generateToken(adminUser);
        Long adminUserId = jwtService.extractUserId(adminToken);

        Optional<UserDTO> userDTOOpt = userService.findById(adminUserId);
        assertTrue(userDTOOpt.isPresent());
        
        UserDTO userDTO = userDTOOpt.get();
        assertEquals("admin@edu.jmc.ac.il", userDTO.email());
        assertEquals("Admin User", userDTO.displayName());
        assertEquals(UserRole.ADMIN, userDTO.role());
        assertEquals(500, userDTO.pointsBalance());
    }

    @Test
    void jwtTokenValidation_WithValidToken_ShouldExtractCorrectClaims() {
        assertTrue(jwtService.validateToken(validToken));
        assertEquals("test@edu.jmc.ac.il", jwtService.extractEmail(validToken));
        assertEquals(UserRole.STUDENT, jwtService.extractRole(validToken));
        assertEquals(testUser.getId(), jwtService.extractUserId(validToken));
        assertFalse(jwtService.isTokenExpired(validToken));
    }

    @Test
    void jwtTokenValidation_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtService.validateToken("invalid-token"));
    }

    @Test
    void jwtTokenValidation_WithMalformedToken_ShouldReturnFalse() {
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";
        assertFalse(jwtService.validateToken(malformedToken));
    }

    @Test
    void userService_GetNonExistentUser_ShouldReturnEmpty() {
        Optional<UserDTO> result = userService.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void userService_FindByEmail_ShouldWork() {
        Optional<UserDTO> userDTOOpt = userService.findByEmail("test@edu.jmc.ac.il");
        assertTrue(userDTOOpt.isPresent());
        
        UserDTO userDTO = userDTOOpt.get();
        assertEquals("test@edu.jmc.ac.il", userDTO.email());
        assertEquals("Test User", userDTO.displayName());
    }
}