package com.hadassahhub.backend.service;

import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize JwtService with test configuration
        jwtService = new JwtService("test-secret-key-for-jwt-signing-must-be-long-enough", 3600000L); // 1 hour

        // Create test user
        testUser = new User(
                "student@edu.jmc.ac.il",
                "hashedPassword123",
                "Test Student"
        );
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testUser, "createdAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_ValidUser_ReturnsToken() {
        // When
        String token = jwtService.generateToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts separated by dots
    }

    @Test
    @DisplayName("Should validate valid token")
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        boolean isValid = jwtService.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid token")
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtService.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject malformed token")
    void validateToken_MalformedToken_ReturnsFalse() {
        // Given
        String malformedToken = "not-a-jwt-token";

        // When
        boolean isValid = jwtService.validateToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract email from token")
    void extractEmail_ValidToken_ReturnsEmail() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertThat(extractedEmail).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void extractUserId_ValidToken_ReturnsUserId() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        Long extractedUserId = jwtService.extractUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should extract role from token")
    void extractRole_ValidToken_ReturnsRole() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        UserRole extractedRole = jwtService.extractRole(token);

        // Then
        assertThat(extractedRole).isEqualTo(testUser.getRole());
    }

    @Test
    @DisplayName("Should extract admin role from token")
    void extractRole_AdminUser_ReturnsAdminRole() {
        // Given
        User adminUser = new User(
                "admin@edu.jmc.ac.il",
                "hashedPassword123",
                "Admin User",
                UserRole.ADMIN
        );
        ReflectionTestUtils.setField(adminUser, "id", 2L);
        String token = jwtService.generateToken(adminUser);

        // When
        UserRole extractedRole = jwtService.extractRole(token);

        // Then
        assertThat(extractedRole).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should detect non-expired token")
    void isTokenExpired_FreshToken_ReturnsFalse() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should detect expired token")
    void isTokenExpired_ExpiredToken_ReturnsTrue() {
        // Given - Create JwtService with very short expiration (1ms)
        JwtService shortExpirationJwtService = new JwtService(
                "test-secret-key-for-jwt-signing-must-be-long-enough", 
                1L
        );
        String token = shortExpirationJwtService.generateToken(testUser);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isExpired = shortExpirationJwtService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void generateToken_DifferentUsers_ReturnsDifferentTokens() {
        // Given
        User user1 = new User("user1@edu.jmc.ac.il", "hash1", "User 1");
        User user2 = new User("user2@edu.hac.ac.il", "hash2", "User 2");
        ReflectionTestUtils.setField(user1, "id", 1L);
        ReflectionTestUtils.setField(user2, "id", 2L);

        // When
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        // Then
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.extractEmail(token1)).isEqualTo(user1.getEmail());
        assertThat(jwtService.extractEmail(token2)).isEqualTo(user2.getEmail());
    }

    @Test
    @DisplayName("Should generate different tokens for same user at different times")
    void generateToken_SameUserDifferentTimes_ReturnsDifferentTokens() {
        // When
        String token1 = jwtService.generateToken(testUser);
        
        // Small delay to ensure different issued-at time
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = jwtService.generateToken(testUser);

        // Then
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.extractEmail(token1)).isEqualTo(jwtService.extractEmail(token2));
        assertThat(jwtService.extractUserId(token1)).isEqualTo(jwtService.extractUserId(token2));
    }
}