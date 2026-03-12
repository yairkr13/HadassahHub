# Requirements Document

## Introduction

The Admin User Management System enables administrators to control user access, roles, and permissions within the Hadassah Hub system. This includes blocking malicious users, suspending users temporarily, activating suspended accounts, and managing role assignments to maintain system security and integrity.

## Requirements

### Requirement 1

**User Story:** As an admin, I want to view all users with filtering capabilities so that I can efficiently manage the user base.

#### Acceptance Criteria

1. WHEN an admin is authenticated THEN the system SHALL display a list of all users
2. WHEN viewing users THEN the system SHALL show user ID, name, email, role, status, and registration date
3. WHEN viewing users THEN the system SHALL provide search by name or email
4. WHEN viewing users THEN the system SHALL allow filtering by role (STUDENT, MODERATOR, ADMIN)
5. WHEN viewing users THEN the system SHALL allow filtering by status (ACTIVE, BLOCKED, SUSPENDED)
6. WHEN viewing users THEN the system SHALL support pagination for large user lists
7. WHEN viewing users THEN the system SHALL allow sorting by name, email, registration date, or last login

### Requirement 2

**User Story:** As an admin, I want to block users permanently so that I can prevent malicious users from accessing the system.

#### Acceptance Criteria

1. WHEN viewing an active user THEN the system SHALL provide a block action
2. WHEN blocking a user THEN the system SHALL require a reason
3. WHEN blocking a user THEN the system SHALL change status to BLOCKED
4. WHEN blocking a user THEN the system SHALL record admin ID, timestamp, and reason
5. WHEN blocking a user THEN the system SHALL invalidate all active sessions
6. WHEN blocking a user THEN the system SHALL prevent login until unblocked
7. WHEN blocking a user THEN the system SHALL create an audit log entry

### Requirement 3

**User Story:** As an admin, I want to suspend users temporarily so that I can restrict access for a specific period.

#### Acceptance Criteria

1. WHEN viewing an active user THEN the system SHALL provide a suspend action
2. WHEN suspending a user THEN the system SHALL require a reason and expiration date
3. WHEN suspending a user THEN the system SHALL change status to SUSPENDED
4. WHEN suspending a user THEN the system SHALL record admin ID, timestamp, reason, and expiration
5. WHEN suspending a user THEN the system SHALL invalidate all active sessions
6. WHEN suspending a user THEN the system SHALL prevent login until expiration or manual activation
7. WHEN suspension expires THEN the system SHALL automatically activate the user
8. WHEN suspending a user THEN the system SHALL create an audit log entry

### Requirement 4

**User Story:** As an admin, I want to activate blocked or suspended users so that I can restore their access.

#### Acceptance Criteria

1. WHEN viewing a blocked or suspended user THEN the system SHALL provide an activate action
2. WHEN activating a user THEN the system SHALL change status to ACTIVE
3. WHEN activating a user THEN the system SHALL record admin ID and timestamp
4. WHEN activating a user THEN the system SHALL allow the user to login
5. WHEN activating a user THEN the system SHALL create an audit log entry

### Requirement 5

**User Story:** As an admin, I want to change user roles so that I can promote or demote users based on their responsibilities.

#### Acceptance Criteria

1. WHEN viewing a user THEN the system SHALL provide a change role action
2. WHEN changing role THEN the system SHALL allow selection of STUDENT, MODERATOR, or ADMIN
3. WHEN changing role THEN the system SHALL require confirmation for ADMIN promotion
4. WHEN changing role THEN the system SHALL update the user's role
5. WHEN changing role THEN the system SHALL invalidate active sessions to refresh permissions
6. WHEN changing role THEN the system SHALL prevent admins from changing their own role
7. WHEN changing role THEN the system SHALL create an audit log entry

### Requirement 6

**User Story:** As an admin, I want to view detailed user information so that I can make informed decisions about user management.

#### Acceptance Criteria

1. WHEN viewing a user THEN the system SHALL provide a view details action
2. WHEN viewing details THEN the system SHALL display basic information (name, email, role, status)
3. WHEN viewing details THEN the system SHALL display account information (registration date, last login, email verified)
4. WHEN viewing details THEN the system SHALL display activity summary (resources uploaded, downloads, last activity)
5. WHEN viewing details THEN the system SHALL display status history (previous blocks/suspensions)
6. WHEN viewing details THEN the system SHALL provide action buttons based on current state

### Requirement 7

**User Story:** As an admin, I want to view user activity history so that I can understand user behavior and contributions.

#### Acceptance Criteria

1. WHEN viewing user details THEN the system SHALL provide an activity history section
2. WHEN viewing activity THEN the system SHALL display resources uploaded with status
3. WHEN viewing activity THEN the system SHALL display recent downloads
4. WHEN viewing activity THEN the system SHALL display total upload and download counts
5. WHEN viewing activity THEN the system SHALL display last activity timestamp
