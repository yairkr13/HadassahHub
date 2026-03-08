# Authentication System Design

## Overview

This design document outlines the implementation of JWT-based authentication system for HadassahHub. The system will provide secure user registration, login, and role-based access control while maintaining consistency with the existing layered architecture and DTO patterns established in the Course Catalog MVP.

## Architecture

### System Context

The Authentication system will integrate with the existing HadassahHub backend architecture:

```
Client (Browser/Frontend)
    ↓ HTTP REST (JWT in Authorization header)
AuthController
    ↓ Business Logic
AuthService + UserService
    ↓ Data Access  
UserRepository
    ↓ JPA/Hibernate
PostgreSQL Database
```

### Security Flow

```
Registration: Email validation → Password hashing → User creation → DTO response
Login: Credential validation → JWT generation → User DTO + Token response
Protected Access: JWT validation → User extraction → Endpoint access
```

## Components and Interfaces

### 1. User Entity

Following the database schema and existing entity patterns:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String displayName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(nullable = false)
    private Integer pointsBalance = 0;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

### 2. UserRole Enum

```java
public enum UserRole {
    STUDENT,    // Default role for registered users
    ADMIN       // Administrative privileges
}
```

### 3. DTOs

**UserDTO** (for API responses):
```java
public record UserDTO(
    Long id,
    String email,
    String displayName,
    UserRole role,
    Integer pointsBalance,
    LocalDateTime createdAt
) {}
```

**RegisterRequestDTO**:
```java
public record RegisterRequestDTO(
    String email,
    String password,
    String displayName
) {}
```

**LoginRequestDTO**:
```java
public record LoginRequestDTO(
    String email,
    String password
) {}
```

**AuthResponseDTO**:
```java
public record AuthResponseDTO(
    String token,
    UserDTO user
) {}
```

### 4. UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### 5. Services

**UserService** (User management):
```java
@Service
public class UserService {
    public UserDTO createUser(RegisterRequestDTO request);
    public Optional<UserDTO> findByEmail(String email);
    public Optional<User> findUserEntityByEmail(String email);
    private UserDTO toDTO(User user);
    private void validateCollegeEmail(String email);
}
```

**AuthService** (Authentication logic):
```java
@Service
public class AuthService {
    public AuthResponseDTO register(RegisterRequestDTO request);
    public AuthResponseDTO login(LoginRequestDTO request);
    private String generateJwtToken(User user);
    private boolean validatePassword(String rawPassword, String hashedPassword);
}
```

**JwtService** (JWT operations):
```java
@Service
public class JwtService {
    public String generateToken(User user);
    public boolean validateToken(String token);
    public String extractEmail(String token);
    public UserRole extractRole(String token);
    public Long extractUserId(String token);
}
```

### 6. Controllers

**AuthController**:
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request);
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request);
}
```

**UserController** (for user profile management):
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication);
}
```

### 7. Spring Security Configuration

**SecurityConfig**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http);
    
    @Bean
    public PasswordEncoder passwordEncoder();
    
    @Bean
    public AuthenticationManager authenticationManager();
}
```

**JwtAuthenticationFilter**:
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain);
}
```

## Data Models

### User Entity Schema

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key, Auto-increment | Unique identifier |
| email | String | NOT NULL, UNIQUE | College email address |
| passwordHash | String | NOT NULL | BCrypt hashed password |
| displayName | String | NOT NULL | User's display name |
| role | UserRole | NOT NULL | STUDENT/ADMIN |
| pointsBalance | Integer | NOT NULL, Default 0 | Reputation points |
| createdAt | LocalDateTime | NOT NULL | Account creation timestamp |

### Business Rules

1. **Email Validation**: Only @edu.jmc.ac.il and @edu.hac.ac.il domains allowed
2. **Password Security**: BCrypt with 12 salt rounds minimum
3. **Default Role**: New users get STUDENT role
4. **JWT Expiration**: 24 hours for user tokens
5. **Points System**: Initialize with 0 points for future reputation system

## Security Configuration

### Public Endpoints (No Authentication Required)
- `GET /api/health`
- `GET /api/courses/**`
- `GET /api/offerings/**`
- `POST /api/auth/register`
- `POST /api/auth/login`

### Protected Endpoints (JWT Required)
- `GET /api/users/me`
- Future resource and review endpoints

### JWT Token Structure
```json
{
  "sub": "user@edu.jmc.ac.il",
  "userId": 123,
  "role": "STUDENT",
  "iat": 1640995200,
  "exp": 1641081600
}
```

## Error Handling

### HTTP Status Codes
- **200 OK**: Successful authentication operations
- **400 Bad Request**: Invalid email domain, validation errors
- **401 Unauthorized**: Invalid credentials, expired/invalid JWT
- **409 Conflict**: Email already exists during registration
- **500 Internal Server Error**: Unexpected server errors

### Error Response Format
Using existing ErrorResponse DTO:
```json
{
    "error": "Invalid email domain",
    "message": "Email must be from @edu.jmc.ac.il or @edu.hac.ac.il domain",
    "timestamp": "2026-03-06T12:30:00Z"
}
```

### Authentication Error Handling
Extend existing GlobalExceptionHandler:
- Handle authentication exceptions
- Handle JWT validation errors
- Handle email domain validation errors
- Handle duplicate email registration

## Integration with Existing System

### Course API Compatibility
- All existing Course API endpoints remain public
- No breaking changes to existing functionality
- Course filtering and search continue to work without authentication

### Database Integration
- User entity integrates with existing schema
- Foreign key relationships ready for future Resource/Review entities
- Points system prepared for future reputation features

### Architecture Consistency
- Follows established Controller → Service → Repository pattern
- Uses DTO pattern for all API responses
- Integrates with existing GlobalExceptionHandler
- Maintains existing code quality standards

## Testing Strategy

### Unit Tests
1. **UserService Tests**: User creation, email validation, DTO mapping
2. **AuthService Tests**: Registration, login, password validation
3. **JwtService Tests**: Token generation, validation, extraction
4. **Repository Tests**: User queries, email uniqueness

### Integration Tests
1. **Auth API Tests**: Registration and login endpoints
2. **Security Tests**: Protected endpoint access with/without JWT
3. **Error Handling Tests**: Invalid credentials, duplicate emails
4. **JWT Integration Tests**: Token validation in request flow

### Security Tests
1. **Password Security**: BCrypt hashing validation
2. **JWT Security**: Token tampering detection
3. **Email Validation**: Domain restriction enforcement
4. **Access Control**: Role-based endpoint protection