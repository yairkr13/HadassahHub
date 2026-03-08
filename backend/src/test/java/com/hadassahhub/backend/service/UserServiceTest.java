package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.RegisterRequestDTO;
import com.hadassahhub.backend.dto.UserDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequestDTO validRegisterRequest;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "student@edu.jmc.ac.il",
                "hashedPassword123",
                "Test Student"
        );
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testUser, "createdAt", LocalDateTime.now());

        validRegisterRequest = new RegisterRequestDTO(
                "newstudent@edu.hac.ac.il",
                "password123",
                "New Student"
        );
    }

    @Test
    @DisplayName("Should create user with valid college email")
    void createUser_ValidCollegeEmail_CreatesUser() {
        // Given
        when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(validRegisterRequest.password())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.createUser(validRegisterRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(testUser.getEmail());
        assertThat(result.displayName()).isEqualTo(testUser.getDisplayName());
        assertThat(result.role()).isEqualTo(UserRole.STUDENT);
        assertThat(result.pointsBalance()).isEqualTo(0);

        verify(userRepository).existsByEmail(validRegisterRequest.email());
        verify(passwordEncoder).encode(validRegisterRequest.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject invalid email domain")
    void createUser_InvalidEmailDomain_ThrowsException() {
        // Given
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO(
                "student@gmail.com",
                "password123",
                "Test Student"
        );

        // When & Then
        assertThatThrownBy(() -> userService.createUser(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email must be from @edu.jmc.ac.il or @edu.hac.ac.il domain");

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should accept valid JMC email domain")
    void createUser_ValidJMCEmail_CreatesUser() {
        // Given
        RegisterRequestDTO jmcRequest = new RegisterRequestDTO(
                "student@edu.jmc.ac.il",
                "password123",
                "JMC Student"
        );
        when(userRepository.existsByEmail(jmcRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(jmcRequest.password())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.createUser(jmcRequest);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should accept valid HAC email domain")
    void createUser_ValidHACEmail_CreatesUser() {
        // Given
        RegisterRequestDTO hacRequest = new RegisterRequestDTO(
                "student@edu.hac.ac.il",
                "password123",
                "HAC Student"
        );
        when(userRepository.existsByEmail(hacRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(hacRequest.password())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.createUser(hacRequest);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject duplicate email")
    void createUser_DuplicateEmail_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).existsByEmail(validRegisterRequest.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject null email")
    void createUser_NullEmail_ThrowsException() {
        // Given
        RegisterRequestDTO nullEmailRequest = new RegisterRequestDTO(
                null,
                "password123",
                "Test Student"
        );

        // When & Then
        assertThatThrownBy(() -> userService.createUser(nullEmailRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    @DisplayName("Should reject empty email")
    void createUser_EmptyEmail_ThrowsException() {
        // Given
        RegisterRequestDTO emptyEmailRequest = new RegisterRequestDTO(
                "",
                "password123",
                "Test Student"
        );

        // When & Then
        assertThatThrownBy(() -> userService.createUser(emptyEmailRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_ExistingUser_ReturnsUserDTO() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findByEmail(testUser.getEmail());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo(testUser.getEmail());
        assertThat(result.get().displayName()).isEqualTo(testUser.getDisplayName());
        assertThat(result.get().role()).isEqualTo(testUser.getRole());

        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void findByEmail_NonExistentUser_ReturnsEmpty() {
        // Given
        String nonExistentEmail = "nonexistent@edu.jmc.ac.il";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.findByEmail(nonExistentEmail);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Should find user entity by email")
    void findUserEntityByEmail_ExistingUser_ReturnsUser() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findUserEntityByEmail(testUser.getEmail());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should correctly map User entity to UserDTO")
    void toDTO_ValidUser_ReturnsMappedDTO() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findByEmail(testUser.getEmail());

        // Then
        assertThat(result).isPresent();
        UserDTO dto = result.get();
        assertThat(dto.id()).isEqualTo(testUser.getId());
        assertThat(dto.email()).isEqualTo(testUser.getEmail());
        assertThat(dto.displayName()).isEqualTo(testUser.getDisplayName());
        assertThat(dto.role()).isEqualTo(testUser.getRole());
        assertThat(dto.pointsBalance()).isEqualTo(testUser.getPointsBalance());
        assertThat(dto.createdAt()).isEqualTo(testUser.getCreatedAt());
    }
}