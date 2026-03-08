# Requirements Document

## Introduction

This specification covers the implementation of the Course Catalog MVP backend foundations for HadassahHub. The system will provide REST APIs for courses and course offerings, enabling students to browse, search, and filter the academic course catalog. This builds upon the existing CourseOffering API to complete the core course catalog functionality needed for the MVP.

## Requirements

### Requirement 1

**User Story:** As a guest user, I want to browse all available courses, so that I can explore the academic catalog without needing to authenticate.

#### Acceptance Criteria

1. WHEN a guest makes a GET request to /api/courses THEN the system SHALL return a list of all courses with basic information
2. WHEN a guest requests courses THEN the system SHALL return course data including id, name, description, category, credits, and recommended year
3. WHEN the courses endpoint is called THEN the system SHALL return data in DTO format and not expose database entities directly
4. WHEN no courses exist THEN the system SHALL return an empty array with HTTP 200 status

### Requirement 2

**User Story:** As a guest user, I want to search for courses by name, so that I can quickly find specific courses I'm interested in.

#### Acceptance Criteria

1. WHEN a guest makes a GET request to /api/courses?search={query} THEN the system SHALL return courses matching the search query in the course name
2. WHEN the search query is provided THEN the system SHALL perform case-insensitive matching on course names
3. WHEN the search query is empty or whitespace THEN the system SHALL return all courses
4. WHEN no courses match the search query THEN the system SHALL return an empty array with HTTP 200 status

### Requirement 3

**User Story:** As a guest user, I want to filter courses by category and study year, so that I can find courses relevant to my academic level and interests.

#### Acceptance Criteria

1. WHEN a guest makes a GET request to /api/courses?category={category} THEN the system SHALL return only courses matching the specified category
2. WHEN a guest makes a GET request to /api/courses?year={year} THEN the system SHALL return only courses whose recommended year exactly equals the specified year
3. WHEN multiple filters are provided (search, category, year) THEN the system SHALL apply all filters using AND logic across all criteria
4. WHEN an invalid category is provided THEN the system SHALL return HTTP 400 with an appropriate error message
5. WHEN an invalid year is provided THEN the system SHALL return HTTP 400 with an appropriate error message

### Requirement 4

**User Story:** As a guest user, I want to view detailed information about a specific course, so that I can understand the course content and requirements.

#### Acceptance Criteria

1. WHEN a guest makes a GET request to /api/courses/{id} THEN the system SHALL return detailed information for the specified course
2. WHEN a valid course ID is provided THEN the system SHALL return course data including all fields (id, name, description, category, credits, recommended year)
3. WHEN an invalid course ID is provided THEN the system SHALL return HTTP 404 with an appropriate error message
4. WHEN the course exists THEN the system SHALL return the data in DTO format

### Requirement 5

**User Story:** As a system administrator, I want the course data model to support the academic structure, so that the system can properly categorize and organize courses.

#### Acceptance Criteria

1. WHEN a course is created THEN the system SHALL support category values of CS_CORE, CS_ELECTIVE, and GENERAL_ELECTIVE
2. WHEN a course has category CS_CORE THEN the system SHALL require a recommended year value of Y1, Y2, or Y3
3. WHEN a course has category CS_ELECTIVE or GENERAL_ELECTIVE THEN the system SHALL allow null recommended year values
4. WHEN course data is stored THEN the system SHALL maintain referential integrity with existing CourseOffering entities
5. WHEN course data is retrieved THEN the system SHALL ensure consistent enum values for categories and years across the API

### Requirement 6

**User Story:** As a developer, I want the system to follow the established architecture patterns, so that the codebase remains maintainable and consistent.

#### Acceptance Criteria

1. WHEN implementing course APIs THEN the system SHALL follow the Controller → Service → Repository layered architecture
2. WHEN returning course data THEN the system SHALL use DTO objects and never expose JPA entities directly
3. WHEN implementing business logic THEN the system SHALL place it in service classes, not controllers
4. WHEN accessing data THEN the system SHALL use Spring Data JPA repositories with appropriate query methods
5. WHEN handling errors THEN the system SHALL return appropriate HTTP status codes and error messages

### Requirement 7

**User Story:** As a developer, I want to clarify the scope of course offerings integration, so that the implementation boundaries are clear for this milestone.

#### Acceptance Criteria

1. WHEN implementing the Course API THEN the system SHALL focus only on /api/courses and /api/courses/{id} endpoints
2. WHEN course offerings are needed THEN clients SHALL use the existing /api/offerings endpoint which is already implemented
3. WHEN this milestone is complete THEN the system SHALL NOT include /api/courses/{id}/offerings as this is out of scope
4. WHEN course and offering data is needed together THEN clients SHALL make separate API calls to both endpoints
5. WHEN future integration is needed THEN course offerings can be added to course details in a later milestone