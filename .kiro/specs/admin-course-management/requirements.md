# Requirements Document

## Introduction

The Admin Course Management System enables administrators to create, edit, and manage courses in the system. This provides flexibility to maintain an up-to-date course catalog and respond to curriculum changes while ensuring data integrity and preventing accidental deletion of courses with associated resources.

## Requirements

### Requirement 1

**User Story:** As an admin, I want to view all courses with filtering capabilities so that I can efficiently manage the course catalog.

#### Acceptance Criteria

1. WHEN an admin is authenticated THEN the system SHALL display a list of all courses
2. WHEN viewing courses THEN the system SHALL show course code, name, category, credits, and resources count
3. WHEN viewing courses THEN the system SHALL provide search by name or code
4. WHEN viewing courses THEN the system SHALL allow filtering by category
5. WHEN viewing courses THEN the system SHALL allow filtering by recommended year
6. WHEN viewing courses THEN the system SHALL allow filtering by status (ACTIVE, ARCHIVED)
7. WHEN viewing courses THEN the system SHALL support pagination and sorting

### Requirement 2

**User Story:** As an admin, I want to create new courses so that I can add courses to the catalog.

#### Acceptance Criteria

1. WHEN creating a course THEN the system SHALL require course code, name, description, category, and credits
2. WHEN creating a course THEN the system SHALL validate course code uniqueness
3. WHEN creating a course THEN the system SHALL validate course code format (letters + numbers)
4. WHEN creating a course THEN the system SHALL validate credits range (1-10)
5. WHEN creating a course THEN the system SHALL set status to ACTIVE by default
6. WHEN creating a course THEN the system SHALL record admin ID and timestamp
7. WHEN creating a course THEN the system SHALL create an audit log entry

### Requirement 3

**User Story:** As an admin, I want to edit course details so that I can keep course information up to date.

#### Acceptance Criteria

1. WHEN editing a course THEN the system SHALL allow updating name, description, credits, and instructor
2. WHEN editing a course THEN the system SHALL prevent changing course code
3. WHEN editing a course THEN the system SHALL validate all updated fields
4. WHEN editing a course THEN the system SHALL record admin ID and update timestamp
5. WHEN editing a course THEN the system SHALL create an audit log entry

### Requirement 4

**User Story:** As an admin, I want to delete courses so that I can remove obsolete courses from the catalog.

#### Acceptance Criteria

1. WHEN deleting a course THEN the system SHALL check if course has resources
2. WHEN a course has resources THEN the system SHALL prevent deletion and show warning
3. WHEN a course has no resources THEN the system SHALL allow deletion with confirmation
4. WHEN deleting a course THEN the system SHALL remove it from the database
5. WHEN deleting a course THEN the system SHALL create an audit log entry

### Requirement 5

**User Story:** As an admin, I want to archive courses so that I can hide obsolete courses without deleting them.

#### Acceptance Criteria

1. WHEN archiving a course THEN the system SHALL change status to ARCHIVED
2. WHEN archiving a course THEN the system SHALL hide course from student view
3. WHEN archiving a course THEN the system SHALL keep resources accessible
4. WHEN archiving a course THEN the system SHALL record admin ID and timestamp
5. WHEN archiving a course THEN the system SHALL create an audit log entry
6. WHEN viewing archived courses THEN the system SHALL allow reactivation

### Requirement 6

**User Story:** As an admin, I want to view detailed course information so that I can understand course usage and statistics.

#### Acceptance Criteria

1. WHEN viewing course details THEN the system SHALL display basic information (code, name, description, category, credits)
2. WHEN viewing course details THEN the system SHALL display resources count by status
3. WHEN viewing course details THEN the system SHALL display prerequisites
4. WHEN viewing course details THEN the system SHALL display creation and update timestamps
5. WHEN viewing course details THEN the system SHALL provide action buttons based on status

### Requirement 7

**User Story:** As an admin, I want to manage course prerequisites so that I can define course dependencies.

#### Acceptance Criteria

1. WHEN creating or editing a course THEN the system SHALL allow selecting prerequisites from existing courses
2. WHEN selecting prerequisites THEN the system SHALL support multiple selections
3. WHEN selecting prerequisites THEN the system SHALL prevent circular dependencies
4. WHEN viewing course details THEN the system SHALL display all prerequisites
5. WHEN deleting a course THEN the system SHALL check if it's a prerequisite for other courses
