package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.AuthResponseDTO;
import com.hadassahhub.backend.dto.LoginRequestDTO;
import com.hadassahhub.backend.dto.RegisterRequestDTO;
import com.hadassahhub.backend.dto.UserDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDTO testUserDTO;
    private RegisterRequestDTO validRegisterRequest;
    private LoginRequestDTO validLoginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "student@edu.jmc.ac.il",
                "hashedPassword123",
                "Test Student"
        );
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testUser, "createdAt", LocalDateTime.now());

        testUserDTO = new UserDTO(
                1L,
                "student@edu.jmc.ac.il",
                "Test Student",
                UserRole.STUDENT,
                0,
                LocalDateTime.now()
        );

        validRegisterRequest = new RegisterRequestDTO(
                "student@edu.jmc.ac.il",
                "password123",
                "Test Student"
        );

        validLoginRequest = new LoginRequestDTO(
                "student@edu.jmc.ac.il",
                "password123"
        );
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_ValidRequest_ReturnsAuthResponse() {
        // Given
        String expectedToken = "jwt.token.here";
        when(userService.createUser(validRegisterRequest)).thenReturn(testUserDTO);
        when(userService.findUserEntityByEmail(testUserDTO.email())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn(expectedToken);

        // When
        AuthResponseDTO result = authService.register(validRegisterRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(expectedToken);
        assertThat(result.user()).isEqualTo(testUserDTO);

        verify(userService).createUser(validRegisterRequest);
        verify(userService).findUserEntityByEmail(testUserDTO.email());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Should reject registration with null email")
    void register_NullEmail_ThrowsException() {
        // Given
        RegisterRequestDTO nullEmailRequest = new RegisterRequestDTO(
                null,
                "password123",
                "Test Student"
        );

        // When & Then
        assertThatThrownBy(() -> authService.register(nullEmailRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should reject registration with empty email")
    void register_EmptyEmail_ThrowsException() {
        // Given
        RegisterRequestDTO emptyEmailRequest = new RegisterRequestDTO(
                "",
                "password123",
                "Test Student"
        );

        // When & Then
        assertThatThrownBy(() -> authService.register(emptyEmailRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should reject registration with null password")
    void register_NullPassword_ThrowsException() {
        // Given
        RegisterRequestDTO nullPasswordRequest = new RegisterRequestDTO(
                "student@edu.jmc.ac.il",
                null,
                "Test Student"
        );

        // When & Then
        assertThatThrownBy(() -> authService.register(nullPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should reject registration with null display name")
    void register_NullDisplayName_ThrowsException() {
        // Given
        RegisterRequestDTO nullDisplayNameRequest = new RegisterRequestDTO(
                "student@edu.jmc.ac.il",
                "password123",
                null
        );

        // When & Then
        assertThatThrownBy(() -> authService.register(nullDisplayNameRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Display name is required");

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should login user successfully")
    void login_ValidCredentials_ReturnsAuthResponse() {
        // Given
        String expectedToken = "jwt.token.here";
        when(userService.findUserEntityByEmail(validLoginRequest.email())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(validLoginRequest.password(), testUser.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn(expectedToken);
        when(userService.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUserDTO));

        // When
        AuthResponseDTO result = authService.login(validLoginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(expectedToken);
        assertThat(result.user()).isEqualTo(testUserDTO);

        verify(userService).findUserEntityByEmail(validLoginRequest.email());
        verify(passwordEncoder).matches(validLoginRequest.password(), testUser.getPasswordHash());
        verify(jwtService).generateToken(testUser);
        verify(userService).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should reject login with non-existent email")
    void login_NonExistentEmail_ThrowsException() {
        // Given
        when(userService.findUserEntityByEmail(validLoginRequest.email())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(validLoginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userService).findUserEntityByEmail(validLoginRequest.email());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should reject login with wrong password")
    void login_WrongPassword_ThrowsException() {
        // Given
        when(userService.findUserEntityByEmail(validLoginRequest.email())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(validLoginRequest.password(), testUser.getPasswordHash())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(validLoginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userService).findUserEntityByEmail(validLoginRequest.email());
        verify(passwordEncoder).matches(validLoginRequest.password(), testUser.getPasswordHash());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should reject login with null email")
    void login_NullEmail_ThrowsException() {
        // Given
        LoginRequestDTO nullEmailRequest = new LoginRequestDTO(null, "password123");

        // When & Then
        assertThatThrownBy(() -> authService.login(nullEmailRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");

        verify(userService, never()).findUserEntityByEmail(anyString());
    }

    @Test
    @DisplayName("Should reject login with empty email")
    void login_EmptyEmail_ThrowsException() {
        // Given
        LoginRequestDTO emptyEmailRequest = new LoginRequestDTO("", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.login(emptyEmailRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");

        verify(userService, never()).findUserEntityByEmail(anyString());
    }

    @Test
    @DisplayName("Should reject login with null password")
    void login_NullPassword_ThrowsException() {
        // Given
        LoginRequestDTO nullPasswordRequest = new LoginRequestDTO("student@edu.jmc.ac.il", null);

        // When & Then
        assertThatThrownBy(() -> authService.login(nullPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");

        verify(userService, never()).findUserEntityByEmail(anyString());
    }

    @Test
    @DisplayName("Should reject login with empty password")
    void login_EmptyPassword_ThrowsException() {
        // Given
        LoginRequestDTO emptyPasswordRequest = new LoginRequestDTO("student@edu.jmc.ac.il", "");

        // When & Then
        assertThatThrownBy(() -> authService.login(emptyPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");

        verify(userService, never()).findUserEntityByEmail(anyString());
    }

    @Test
    @DisplayName("Should handle admin user login")
    void login_AdminUser_ReturnsAuthResponseWithAdminRole() {
        // Given
        User adminUser = new User(
                "admin@edu.jmc.ac.il",
                "hashedPassword123",
                "Admin User",
                UserRole.ADMIN
        );
        ReflectionTestUtils.setField(adminUser, "id", 2L);

        UserDTO adminUserDTO = new UserDTO(
                2L,
                "admin@edu.jmc.ac.il",
                "Admin User",
                UserRole.ADMIN,
                0,
                LocalDateTime.now()
        );

        LoginRequestDTO adminLoginRequest = new LoginRequestDTO(
                "admin@edu.jmc.ac.il",
                "adminPassword"
        );

        String expectedToken = "admin.jwt.token";
        when(userService.findUserEntityByEmail(adminLoginRequest.email())).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches(adminLoginRequest.password(), adminUser.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(adminUser)).thenReturn(expectedToken);
        when(userService.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUserDTO));

        // When
        AuthResponseDTO result = authService.login(adminLoginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(expectedToken);
        assertThat(result.user().role()).isEqualTo(UserRole.ADMIN);
        assertThat(result.user().email()).isEqualTo("admin@edu.jmc.ac.il");
    }
}