# Design Document

## Overview

This design document outlines the implementation of the Course Catalog MVP backend foundations for HadassahHub. The system will provide REST APIs for courses with search and filtering capabilities, building upon the existing CourseOffering infrastructure while maintaining architectural consistency.

The implementation will address current architectural violations in the existing CourseController (which returns entities directly) and establish proper layered architecture patterns for the Course domain.

## Architecture

### System Context

The Course API will integrate with the existing HadassahHub backend architecture:

```
Client (Browser/Frontend)
    ↓ HTTP REST
CourseController
    ↓ Business Logic
CourseService  
    ↓ Data Access
CourseRepository
    ↓ JPA/Hibernate
PostgreSQL Database
```

### Layered Architecture Compliance

Following the established patterns from CourseOfferingController:

- **Controller Layer**: Handle HTTP requests, parameter validation, response formatting
- **Service Layer**: Business logic, filtering, DTO mapping, transaction management  
- **Repository Layer**: Data access using Spring Data JPA query methods
- **DTO Layer**: API response objects that never expose JPA entities

## Components and Interfaces

### 1. Course Entity Updates

The existing Course entity requires updates to align with the database schema:

**Current Issues:**
- Missing `credits` field (required for academic credit tracking)
- Using String for `year` instead of StudyYear enum
- Using String for `category` instead of CourseCategory enum
- Field naming inconsistency with schema

**Required Changes:**
```java
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseCategory category;
    
    @Column(nullable = false)
    private Integer credits;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_year")
    private StudyYear recommendedYear; // nullable for electives
}
```

### 2. New Enums

**CourseCategory Enum:**
```java
public enum CourseCategory {
    CS_CORE,        // Required CS courses
    CS_ELECTIVE,    // CS elective courses  
    GENERAL_ELECTIVE // College-wide electives
}
```

### 3. CourseDTO

New DTO for Course API responses:
```java
public record CourseDTO(
    Long id,
    String name,
    String description,
    CourseCategory category,
    Integer credits,
    StudyYear recommendedYear // null for electives
) {}
```

### 4. CourseRepository Enhancements

Extend existing repository with flexible filtering using JPA Specifications:
```java
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    // JpaSpecificationExecutor provides findAll(Specification<Course> spec)
    // This avoids method explosion and provides flexible filtering
}
```

**CourseSpecifications Utility Class:**
```java
public class CourseSpecifications {
    public static Specification<Course> hasNameContaining(String name);
    public static Specification<Course> hasCategory(CourseCategory category);
    public static Specification<Course> hasRecommendedYear(StudyYear year);
}
```

### 5. CourseService

New service layer implementing business logic:
```java
@Service
public class CourseService {
    
    public List<CourseDTO> findCourses(String search, CourseCategory category, StudyYear year);
    public Optional<CourseDTO> findCourseById(Long id);
    private CourseDTO toDTO(Course course);
    private List<Course> applyCombinedFilters(String search, CourseCategory category, StudyYear year);
}
```

### 6. CourseController Refactoring

Replace existing controller to follow DTO pattern:
```java
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @GetMapping
    public List<CourseDTO> getCourses(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) CourseCategory category,
        @RequestParam(required = false) StudyYear year);
        
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id);
}
```

### 7. Global Error Handling

Add centralized error handling for consistent API responses:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnumValue(MethodArgumentTypeMismatchException ex);
    
    @ExceptionHandler(EntityNotFoundException.class) 
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex);
}
```

## Data Models

### Course Entity Schema Alignment

The Course entity will be updated to align with the existing database structure, preserving current column names and using ddl-auto=update for local development:

| Field | Type | Constraints | Database Column | Description |
|-------|------|-------------|-----------------|-------------|
| id | Long | Primary Key, Auto-increment | id | Unique identifier |
| name | String | NOT NULL | name | Course name |
| description | String | TEXT | description | Course description |
| category | CourseCategory | NOT NULL | category | CS_CORE/CS_ELECTIVE/GENERAL_ELECTIVE |
| credits | Integer | NOT NULL | credits | Academic credit points |
| recommendedYear | StudyYear | Nullable | year | Y1/Y2/Y3 for CS_CORE, null for electives |

**Migration Strategy**: Use Spring Boot's ddl-auto=update for local MVP development. The entity annotations will be updated to match existing column names where possible, adding new fields as needed.

### Business Rules

1. **Category-Year Relationship**: CS_CORE courses should have recommendedYear, electives may have null
2. **Search Logic**: Case-insensitive partial matching on course name
3. **Filter Combination**: All provided filters applied with AND logic
4. **Year Filtering**: Exact match on recommendedYear (does not include electives with null year)

## Error Handling

### HTTP Status Codes

- **200 OK**: Successful retrieval of courses/course
- **400 Bad Request**: Invalid enum values for category or year parameters
- **404 Not Found**: Course ID not found
- **500 Internal Server Error**: Unexpected server errors

### Error Response Format

```json
{
    "error": "Invalid category value",
    "message": "Category must be one of: CS_CORE, CS_ELECTIVE, GENERAL_ELECTIVE",
    "timestamp": "2026-03-05T10:30:00Z"
}
```

### Validation Strategy

- **Enum Validation**: Spring automatically handles invalid enum values with 400 status
- **Parameter Validation**: Use @RequestParam validation for required constraints
- **Entity Validation**: Repository-level validation for data integrity

## Testing Strategy

### Unit Tests

1. **CourseService Tests**:
   - Test filtering logic with various parameter combinations
   - Test DTO mapping accuracy
   - Test business rule enforcement (category-year relationships)

2. **CourseRepository Tests**:
   - Test custom query methods with sample data
   - Test case-insensitive search functionality
   - Test combined filter queries

3. **CourseController Tests**:
   - Test HTTP parameter binding and validation
   - Test error response formatting
   - Test successful response structure

### Integration Tests

1. **API Integration Tests**:
   - Test complete request-response cycle
   - Test filter combinations with real database
   - Test error scenarios (invalid IDs, bad parameters)

2. **Database Integration Tests**:
   - Test entity persistence with updated schema
   - Test relationship integrity with CourseOffering
   - Test enum storage and retrieval

### Test Data Strategy

- **Seed Data**: Extend existing DataSeeder with diverse course examples
- **Test Categories**: Include examples of all CourseCategory types
- **Year Distribution**: Include courses with and without recommendedYear
- **Search Testing**: Include courses with overlapping names for search testing

## Migration Considerations

### Database Schema Updates

Using Spring Boot's ddl-auto=update for local MVP development:

1. **Add Missing Fields**: Add credits field to existing Course entity
2. **Enum Migration**: Update category and year fields to use enums (preserving existing column names)
3. **Preserve Data**: Existing data will be maintained during entity updates

### Backward Compatibility

- **API Breaking Changes**: The CourseController API will change from returning entities to DTOs
- **Data Format Changes**: Category and year fields will use enum values instead of strings
- **Client Impact**: Frontend applications will need updates to handle new response format

### Deployment Strategy

1. **Local Development**: Use ddl-auto=update for automatic schema updates
2. **Data Seeding**: Update existing DataSeeder to populate new fields
3. **API Versioning**: Consider versioning if existing clients depend on current API