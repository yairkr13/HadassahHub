package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.AuthResponseDTO;
import com.hadassahhub.backend.dto.LoginRequestDTO;
import com.hadassahhub.backend.dto.RegisterRequestDTO;
import com.hadassahhub.backend.dto.UserDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Validate input
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.displayName() == null || request.displayName().trim().isEmpty()) {
            throw new IllegalArgumentException("Display name is required");
        }

        // Create user through UserService (handles email validation and duplicate check)
        UserDTO userDTO = userService.createUser(request);
        
        // Get the created user entity to generate JWT
        User user = userService.findUserEntityByEmail(userDTO.email())
                .orElseThrow(() -> new RuntimeException("User creation failed"));

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(token, userDTO);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        // Validate input
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Find user by email
        User user = userService.findUserEntityByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        // Validate password
        if (!validatePassword(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Check user status before allowing login
        if (!user.isActive()) {
            if (user.isBlocked()) {
                throw new IllegalArgumentException("Your account has been blocked. Please contact administrator.");
            } else if (user.isSuspended()) {
                throw new IllegalArgumentException("Your account has been suspended. Please contact administrator.");
            } else {
                throw new IllegalArgumentException("Your account is not active. Please contact administrator.");
            }
        }

        // Update last login timestamp
        user.updateLastLogin();
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Convert to DTO
        UserDTO userDTO = userService.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User conversion failed"));

        return new AuthResponseDTO(token, userDTO);
    }

    private boolean validatePassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}