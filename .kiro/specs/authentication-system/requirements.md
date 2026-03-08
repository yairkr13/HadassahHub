# Authentication System Requirements

## Introduction

This specification covers the implementation of JWT-based authentication system for HadassahHub. The system will enable user registration with college email validation, secure login/logout, and role-based access control. This builds upon the existing Course Catalog MVP to provide the authentication foundation needed for protected resources and admin functionality.

## Requirements

### Requirement 1

**User Story:** As a student, I want to register with my college email address, so that I can access protected features of the platform.

#### Acceptance Criteria

1. WHEN a user submits registration with a valid college email THEN the system SHALL create a new user account
2. WHEN a user submits registration THEN the system SHALL only accept emails from domains @edu.jmc.ac.il and @edu.hac.ac.il
3. WHEN a user submits registration with invalid email domain THEN the system SHALL return HTTP 400 with appropriate error message
4. WHEN a user submits registration with existing email THEN the system SHALL return HTTP 409 with appropriate error message
5. WHEN registration is successful THEN the system SHALL return user details without password in DTO format

### Requirement 2

**User Story:** As a registered user, I want to login with my credentials, so that I can access protected features and maintain my session.

#### Acceptance Criteria

1. WHEN a user submits valid login credentials THEN the system SHALL return a JWT token and user details
2. WHEN a user submits invalid credentials THEN the system SHALL return HTTP 401 with appropriate error message
3. WHEN login is successful THEN the JWT token SHALL contain user ID, email, and role information
4. WHEN login is successful THEN the JWT token SHALL have appropriate expiration time (24 hours)
5. WHEN user data is returned THEN the system SHALL use DTO format and never expose password

### Requirement 3

**User Story:** As a system administrator, I want role-based access control, so that admin functions are restricted to authorized users.

#### Acceptance Criteria

1. WHEN a user registers THEN the system SHALL assign STUDENT role by default
2. WHEN the system needs admin access THEN it SHALL support ADMIN role assignment
3. WHEN JWT tokens are generated THEN they SHALL include role information for authorization
4. WHEN protected endpoints are accessed THEN the system SHALL validate JWT tokens and extract user information
5. WHEN role-based restrictions apply THEN the system SHALL enforce proper access control

### Requirement 4

**User Story:** As a developer, I want secure password handling, so that user credentials are properly protected.

#### Acceptance Criteria

1. WHEN passwords are stored THEN the system SHALL use BCrypt hashing with appropriate salt rounds
2. WHEN passwords are validated THEN the system SHALL use secure comparison methods
3. WHEN user data is returned THEN passwords SHALL never be included in API responses
4. WHEN JWT tokens are generated THEN they SHALL be signed with a secure secret key
5. WHEN authentication fails THEN the system SHALL not reveal whether email or password was incorrect

### Requirement 5

**User Story:** As a developer, I want the authentication system to follow established architecture patterns, so that the codebase remains maintainable and consistent.

#### Acceptance Criteria

1. WHEN implementing authentication THEN the system SHALL follow Controller → Service → Repository layered architecture
2. WHEN returning user data THEN the system SHALL use DTO objects and never expose JPA entities directly
3. WHEN handling authentication logic THEN the system SHALL place it in service classes, not controllers
4. WHEN accessing user data THEN the system SHALL use Spring Data JPA repositories
5. WHEN handling authentication errors THEN the system SHALL use the existing GlobalExceptionHandler pattern

### Requirement 6

**User Story:** As a system integrator, I want Spring Security integration, so that authentication is properly integrated with the existing application security.

#### Acceptance Criteria

1. WHEN configuring security THEN the system SHALL use Spring Security with JWT authentication
2. WHEN public endpoints are accessed THEN they SHALL remain accessible without authentication
3. WHEN protected endpoints are accessed THEN they SHALL require valid JWT tokens
4. WHEN JWT tokens are validated THEN Spring Security SHALL handle the authentication process
5. WHEN security is configured THEN it SHALL not interfere with existing public Course API endpoints