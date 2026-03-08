# Authentication System Implementation Plan

- [x] 1. Create UserRole enum for role-based access control


  - Create UserRole enum with STUDENT and ADMIN values
  - Place in enums package following existing pattern
  - _Requirements: 3.1, 3.2, 3.3_

- [x] 2. Create User entity with proper security fields


  - Implement User entity with id, email, passwordHash, displayName, role, pointsBalance, createdAt
  - Add unique constraint on email field
  - Use BCrypt-compatible passwordHash field
  - Set default values for pointsBalance (0) and role (STUDENT)
  - _Requirements: 1.1, 3.1, 4.1, 5.4_

- [x] 3. Create authentication DTOs for API requests and responses


  - Create UserDTO for API responses (exclude password)
  - Create RegisterRequestDTO for registration requests
  - Create LoginRequestDTO for login requests  
  - Create AuthResponseDTO combining token and user data
  - _Requirements: 1.5, 2.5, 5.2_

- [x] 4. Create UserRepository with email-based queries


  - Extend JpaRepository with User entity
  - Add findByEmail method for authentication
  - Add existsByEmail method for registration validation
  - _Requirements: 1.4, 2.1, 5.4_

- [x] 5. Implement UserService for user management operations


  - Create UserService with user creation and retrieval methods
  - Implement college email domain validation (@edu.jmc.ac.il, @edu.hac.ac.il)
  - Add User to UserDTO mapping method
  - Handle user creation with proper validation
  - _Requirements: 1.1, 1.2, 1.3, 5.1, 5.3_

- [x] 6. Implement JwtService for token operations


  - Create JwtService for JWT generation, validation, and extraction
  - Implement token generation with user ID, email, and role claims
  - Add token validation and expiration handling (24 hours)
  - Implement claim extraction methods (email, role, userId)
  - _Requirements: 2.3, 2.4, 3.3, 4.4_

- [x] 7. Implement AuthService for authentication business logic


  - Create AuthService with register and login methods
  - Implement password hashing using BCrypt
  - Add password validation for login
  - Integrate with UserService and JwtService
  - _Requirements: 1.1, 2.1, 2.2, 4.1, 4.2, 5.3_

- [x] 8. Create AuthController for authentication endpoints


  - Implement POST /api/auth/register endpoint
  - Implement POST /api/auth/login endpoint
  - Add proper request validation and error handling
  - Return AuthResponseDTO with token and user data
  - _Requirements: 1.1, 1.3, 1.4, 2.1, 2.2, 5.1, 5.2_

- [x] 9. Extend GlobalExceptionHandler for authentication errors



  - Add handling for duplicate email registration (409 Conflict)
  - Add handling for invalid credentials (401 Unauthorized)
  - Add handling for invalid email domain (400 Bad Request)
  - Add handling for JWT validation errors (401 Unauthorized)
  - _Requirements: 1.3, 1.4, 2.2, 4.5, 5.5_

- [x] 10. Configure Spring Security with JWT authentication


  - Create SecurityConfig with JWT-based authentication
  - Configure public endpoints (courses, auth) and protected endpoints
  - Set up password encoder bean (BCrypt)
  - Configure CORS for frontend integration
  - _Requirements: 6.1, 6.2, 6.3, 6.5_


- [ ] 11. Implement JwtAuthenticationFilter for request processing
  - Create JWT filter to extract and validate tokens from requests
  - Implement token extraction from Authorization header
  - Set up Spring Security authentication context
  - Handle JWT validation errors appropriately
  - _Requirements: 3.4, 6.4_




- [ ] 12. Create UserController for user profile management
  - Implement GET /api/users/me endpoint for current user info
  - Add JWT-based authentication requirement




  - Return UserDTO with current user details
  - Test protected endpoint functionality
  - _Requirements: 2.5, 3.4, 5.2_




- [ ] 13. Update DataSeeder with admin user for testing
  - Add admin user creation in DataSeeder for development/testing
  - Create test student user for authentication testing

  - Use proper password hashing for seeded users
  - Ensure seeded users have valid college email domains
  - _Requirements: 3.2, 4.1_

- [ ] 14. Write unit tests for authentication services
  - Test UserService email validation and user creation
  - Test AuthService registration and login flows
  - Test JwtService token generation and validation
  - Test password hashing and validation
  - _Requirements: 1.2, 2.1, 4.1, 4.2_

- [x] 15. Write integration tests for authentication API


  - Test POST /api/auth/register with valid and invalid data
  - Test POST /api/auth/login with valid and invalid credentials
  - Test GET /api/users/me with and without JWT tokens
  - Test error scenarios (duplicate email, invalid domain, etc.)
  - _Requirements: 1.1, 1.3, 2.1, 2.2, 3.4_