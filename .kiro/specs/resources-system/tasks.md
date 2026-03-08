# Implementation Plan

- [x] 1. Create core resource domain models and enums


  - Create ResourceType enum with values (EXAM, HOMEWORK, SUMMARY, LINK)
  - Create ResourceStatus enum with values (PENDING, APPROVED, REJECTED)
  - Create Resource entity with proper JPA annotations and relationships
  - Write unit tests for Resource entity validation and relationships
  - _Requirements: 1.3, 1.4, 1.5, 3.3, 3.4_

- [x] 2. Implement resource repository layer with specifications





  - Create ResourceRepository interface extending JpaRepository and JpaSpecificationExecutor
  - Implement custom query methods for finding resources by course, status, and type
  - Create ResourceSpecifications utility class for dynamic filtering
  - Write unit tests for repository methods and specifications
  - _Requirements: 2.1, 2.2, 3.1, 3.2_

- [x] 3. Create resource DTOs and request/response models



  - Create ResourceDTO record for API responses
  - Create CreateResourceRequestDTO with validation annotations
  - Create ResourceStatsDTO for course resource statistics
  - Create RejectResourceRequestDTO for admin rejection workflow
  - Write unit tests for DTO validation and mapping
  - _Requirements: 1.2, 2.3, 3.5, 5.3_

- [x] 4. Implement core ResourceService business logic




  - Create ResourceService class with resource creation methods
  - Implement resource retrieval methods with proper filtering
  - Add resource ownership validation and security checks
  - Implement entity to DTO mapping methods
  - Write comprehensive unit tests for ResourceService methods
  - _Requirements: 1.1, 1.6, 2.4, 5.1, 5.2_

- [x] 5. Add resource moderation functionality to ResourceService



  - Implement approveResource method with admin validation
  - Implement rejectResource method with reason tracking
  - Add findPendingResources method for admin interface
  - Implement resource status change validation and history tracking
  - Write unit tests for moderation workflow and edge cases
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

- [x] 6. Create ResourceController with CRUD endpoints


  - Implement POST /api/resources endpoint for resource creation (URL-based)
  - Implement GET /api/resources/my endpoint for user's resources
  - Implement GET /api/resources/{id} endpoint with access control
  - Implement DELETE /api/resources/{id} endpoint with ownership validation
  - Implement GET /api/resources/{id}/access endpoint for URL redirection
  - Add proper security annotations and request validation
  - Write integration tests for ResourceController endpoints
  - _Requirements: 1.1, 1.2, 5.1, 5.4, 5.5_

- [x] 7. Create AdminResourceController for moderation


  - Implement GET /api/admin/resources/pending endpoint
  - Implement POST /api/admin/resources/{id}/approve endpoint
  - Implement POST /api/admin/resources/{id}/reject endpoint
  - Add proper admin role validation and security
  - Write integration tests for admin moderation endpoints
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [x] 8. Enhance CourseController with resource integration


  - Add GET /api/courses/{id}/resources endpoint with filtering
  - Add GET /api/courses/{id}/resources/stats endpoint
  - Add POST /api/courses/{id}/resources endpoint for course-specific uploads
  - Implement resource type filtering and pagination
  - Write integration tests for enhanced course endpoints
  - _Requirements: 2.1, 2.2, 2.5, 4.2, 4.3, 4.5_

- [x] 9. Enhance CourseService with resource statistics


  - Add getCourseResourceStats method to CourseService
  - Implement getCourseWithResources method for enhanced course details
  - Add resource count aggregation by type
  - Implement recent resources retrieval for course pages
  - Write unit tests for enhanced CourseService methods
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 10. Implement URL validation and basic resource access


  - Create URL validation service for external links
  - Implement basic URL format and accessibility checking
  - Add URL sanitization and safety validation
  - Implement resource access endpoint that redirects to URLs
  - Add error handling for invalid or inaccessible URLs
  - Write unit tests for URL validation and access methods
  - _Requirements: 6.1, 6.5_

- [x] 11. Add resource-specific exception handling


  - Create ResourceNotFoundException for missing resources
  - Create UnauthorizedResourceAccessException for access violations
  - Create InvalidUrlException for URL validation errors
  - Enhance GlobalExceptionHandler with resource exception mappings
  - Write unit tests for exception handling scenarios
  - _Requirements: 1.4, 5.4, 6.1, 6.5_

- [x] 12. Create enhanced course detail DTOs


  - Create CourseDetailDTO with integrated resource information
  - Enhance existing CourseDTO with resource statistics
  - Add ResourceStatsDTO integration to course responses
  - Implement proper DTO mapping in CourseService
  - Write unit tests for enhanced DTO mapping and validation
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 13. Implement resource access control and security


  - Add method-level security to ResourceService operations
  - Implement resource ownership validation for modifications
  - Add admin role validation for moderation operations
  - Implement resource visibility rules (only approved resources for students)
  - Write security integration tests for access control scenarios
  - _Requirements: 1.6, 3.1, 5.4, 5.5, 6.5_

- [x] 14. Add comprehensive resource repository tests


  - Write integration tests for ResourceRepository custom queries
  - Test resource specifications with complex filtering scenarios
  - Test resource-course-user relationship integrity
  - Test repository performance with large datasets
  - Verify proper indexing and query optimization
  - _Requirements: 2.1, 2.2, 2.6, 3.2_

- [x] 15. Create resource data seeding for development


  - Enhance DataSeeder with sample resource data
  - Create resources for existing courses with various types and statuses
  - Add realistic academic year and exam term data
  - Implement proper user-resource associations
  - Test seeding with different user roles and scenarios
  - _Requirements: 1.5, 2.3, 3.4, 4.4_

- [x] 16. Implement end-to-end resource workflow tests





  - Create integration tests for complete resource upload-to-approval workflow
  - Test resource creation, moderation, and visibility changes
  - Test enhanced course pages with resource integration
  - Verify proper error handling and edge cases
  - Test security and access control across the entire workflow
  - _Requirements: 1.1, 1.6, 2.4, 3.3, 4.5, 5.1_