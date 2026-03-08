# Implementation Plan

- [x] 1. Create CourseCategory enum for course categorization


  - Create CourseCategory enum with CS_CORE, CS_ELECTIVE, GENERAL_ELECTIVE values
  - Place in enums package following existing pattern
  - _Requirements: 5.1, 5.2, 5.3_

- [x] 2. Update Course entity to align with database schema


  - Add credits field as Integer with NOT NULL constraint
  - Change category field from String to CourseCategory enum
  - Change year field from String to StudyYear enum (rename to recommendedYear)
  - Preserve existing column names using @Column annotations
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 6.4_

- [x] 3. Create CourseDTO for API responses


  - Create CourseDTO record with id, name, description, category, credits, recommendedYear fields
  - Follow existing CourseOfferingDTO pattern
  - _Requirements: 1.3, 4.4, 6.2_

- [x] 4. Create CourseSpecifications utility for flexible filtering


  - Implement CourseSpecifications class with static methods for filtering
  - Add hasNameContaining method for case-insensitive search
  - Add hasCategory method for category filtering
  - Add hasRecommendedYear method for exact year matching
  - _Requirements: 2.1, 2.2, 3.1, 3.2_

- [x] 5. Update CourseRepository to use JpaSpecificationExecutor


  - Extend CourseRepository interface with JpaSpecificationExecutor<Course>
  - Remove any existing derived query methods to avoid method explosion
  - _Requirements: 6.4_

- [x] 6. Create CourseService for business logic


  - Implement CourseService with findCourses method accepting search, category, year parameters
  - Implement findCourseById method returning Optional<CourseDTO>
  - Add private toDTO method for entity to DTO mapping
  - Use Specifications to build dynamic queries
  - _Requirements: 1.1, 1.2, 2.1, 3.3, 4.1, 4.2, 6.1, 6.3_

- [x] 7. Create ErrorResponse class for consistent error handling


  - Create ErrorResponse record with error, message, timestamp fields
  - Follow the documented JSON error format
  - _Requirements: 3.4, 3.5, 4.3_

- [x] 8. Create GlobalExceptionHandler for centralized error handling


  - Implement @RestControllerAdvice class
  - Handle MethodArgumentTypeMismatchException for invalid enum values (400)
  - Handle EntityNotFoundException for missing course IDs (404)
  - Return ErrorResponse objects with appropriate HTTP status codes
  - _Requirements: 3.4, 3.5, 4.3_


- [x] 9. Refactor CourseController to use service layer and DTOs

  - Replace existing CourseController implementation
  - Add getCourses method with search, category, year parameters
  - Update getCourseById method to return CourseDTO and handle 404
  - Remove createCourse method (out of scope for MVP)
  - _Requirements: 1.1, 1.3, 2.1, 3.1, 4.1, 4.4, 6.1, 6.2_

- [x] 10. Update DataSeeder to populate Course entities with new fields



  - Add credits values to existing course seed data
  - Convert category strings to CourseCategory enum values
  - Convert year strings to StudyYear enum values (set to null for electives)
  - Ensure diverse test data for filtering scenarios
  - _Requirements: 5.1, 5.2, 5.3_

- [x] 11. Write unit tests for CourseService


  - Test findCourses method with various filter combinations
  - Test toDTO mapping accuracy
  - Test empty result scenarios
  - _Requirements: 1.4, 2.4, 3.3_


- [x] 12. Write integration tests for Course API endpoints


  - Test GET /api/courses with no parameters
  - Test GET /api/courses with search parameter
  - Test GET /api/courses with category and year filters
  - Test GET /api/courses/{id} with valid and invalid IDs
  - Test error scenarios (invalid enum values)
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 4.3_