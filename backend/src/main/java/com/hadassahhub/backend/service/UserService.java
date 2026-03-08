package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.RegisterRequestDTO;
import com.hadassahhub.backend.dto.UserDTO;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final Set<String> ALLOWED_EMAIL_DOMAINS = Set.of(
            "@edu.jmc.ac.il",
            "@edu.hac.ac.il"
    );

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(RegisterRequestDTO request) {
        validateCollegeEmail(request.email());
        
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        
        User user = new User(
                request.email(),
                hashedPassword,
                request.displayName()
        );

        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDTO);
    }

    public Optional<User> findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole(),
                user.getPointsBalance(),
                user.getCreatedAt()
        );
    }

    private void validateCollegeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        boolean isValidDomain = ALLOWED_EMAIL_DOMAINS.stream()
                .anyMatch(domain -> email.toLowerCase().endsWith(domain));

        if (!isValidDomain) {
            throw new IllegalArgumentException("Email must be from @edu.jmc.ac.il or @edu.hac.ac.il domain");
        }
    }
}