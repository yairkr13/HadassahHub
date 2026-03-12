# Design Document

## Overview

The Admin Course Management System provides comprehensive tools for administrators to create, edit, archive, and delete courses. The system implements data integrity checks to prevent deletion of courses with resources and supports course archiving as a safe alternative to deletion.

## Business Requirements

### Functional Requirements

1. **Create Course**: Add new courses to the system
2. **Edit Course**: Update course details (name, description, credits, etc.)
3. **Delete Course**: Remove courses from the system
4. **Archive Course**: Hide courses without deleting them
5. **View Course Details**: See full course information including statistics
6. **Manage Prerequisites**: Define course dependencies

### Non-Functional Requirements

1. **Data Integrity**: Prevent deletion of courses with resources
2. **Performance**: Course operations should complete within 1 second
3. **Usability**: Intuitive course management interface
4. **Accessibility**: WCAG 2.1 AA compliant

## Design

### Course Management Dashboard

#### Course List Table
Columns:
- Course Code
- Course Name
- Category (badge)
- Credits
- Recommended Year
- Resources Count
- Status (ACTIVE/ARCHIVED)
- Actions (dropdown menu)

#### Filters
- Search by name or code
- Filter by category
- Filter by year
- Filter by status
- Sort by: Name, Code, Category, Resources Count

### Create/Edit Course Form

#### Basic Information
- Course Code (required, unique)
- Course Name (required, max 200 chars)
- Description (required, max 1000 chars)
- Category (required, dropdown)
- Credits (required, 1-10)
- Recommended Year (optional, 1-4)

#### Additional Information
- Prerequisites (multi-select)
- Department (optional)
- Instructor (optional)

## Backend Implementation

### Database Schema

#### Course Entity (Enhanced)
```java
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseCategory category;
    
    @Column(nullable = false)
    private Integer credits;
    
    @Column(name = "recommended_year")
    private Integer recommendedYear;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.ACTIVE;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "instructor", length = 200)
    private String instructor;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @ManyToMany
    @JoinTable(
        name = "course_prerequisites",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Course> prerequisites;
}
```

#### CourseStatus Enum (New)
```java
public enum CourseStatus {
    ACTIVE,
    ARCHIVED
}
```

### API Endpoints

#### GET /api/admin/courses
List all courses with filtering and pagination.

**Query Parameters:**
- `search`, `category`, `year`, `status`, `page`, `size`, `sortBy`, `sortDirection`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "code": "CS101",
      "name": "Introduction to Computer Science",
      "category": "CS_MANDATORY",
      "credits": 4,
      "recommendedYear": 1,
      "status": "ACTIVE",
      "resourcesCount": 45
    }
  ],
  "totalElements": 50,
  "totalPages": 3
}
```

#### POST /api/admin/courses
Create a new course.

**Request Body:**
```json
{
  "code": "CS102",
  "name": "Data Structures",
  "description": "Introduction to fundamental data structures...",
  "category": "CS_MANDATORY",
  "credits": 4,
  "recommendedYear": 2,
  "prerequisiteIds": [1]
}
```

#### PUT /api/admin/courses/{id}
Update an existing course.

#### DELETE /api/admin/courses/{id}
Delete a course (only if no resources exist).

#### PUT /api/admin/courses/{id}/archive
Archive a course.

#### PUT /api/admin/courses/{id}/activate
Activate an archived course.

### Service Layer

#### AdminCourseService
```java
@Service
public class AdminCourseService {
    
    private final CourseRepository courseRepository;
    private final ResourceRepository resourceRepository;
    private final AuditLogService auditLogService;
    
    public Page<AdminCourseDTO> listCourses(CourseFilterDTO filter, Pageable pageable);
    
    public AdminCourseDetailDTO getCourseDetails(Long courseId);
    
    public CourseDTO createCourse(CreateCourseRequestDTO request, Long adminId);
    
    public CourseDTO updateCourse(Long courseId, UpdateCourseRequestDTO request, Long adminId);
    
    public void deleteCourse(Long courseId, Long adminId) throws CourseHasResourcesException;
    
    public void archiveCourse(Long courseId, Long adminId);
    
    public void activateCourse(Long courseId, Long adminId);
    
    public boolean canDeleteCourse(Long courseId);
}
```

### Controller Layer

#### AdminCourseController
```java
@RestController
@RequestMapping("/api/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {
    
    private final AdminCourseService adminCourseService;
    
    @GetMapping
    public ResponseEntity<Page<AdminCourseDTO>> listCourses(...);
    
    @GetMapping("/{id}")
    public ResponseEntity<AdminCourseDetailDTO> getCourseDetails(@PathVariable Long id);
    
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(...);
    
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(...);
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(...);
    
    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveCourse(...);
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateCourse(...);
}
```

## Frontend Implementation

### Pages

#### CourseManagementPage.tsx
Main page for course management.

**Components:**
- CourseTable: Displays courses
- CourseFilters: Search and filter controls
- CourseFormModal: Create/edit form
- CourseDetailsModal: Detailed information
- DeleteConfirmationModal: Deletion confirmation

### Services

#### admin-course.service.ts
```typescript
export const adminCourseService = {
  async listCourses(filters: CourseFilters): Promise<PaginatedResponse<AdminCourse>> {
    // Implementation
  },
  
  async createCourse(data: CreateCourseRequest): Promise<Course> {
    // Implementation
  },
  
  async updateCourse(courseId: number, data: UpdateCourseRequest): Promise<Course> {
    // Implementation
  },
  
  async deleteCourse(courseId: number): Promise<void> {
    // Implementation
  },
  
  async archiveCourse(courseId: number): Promise<void> {
    // Implementation
  },
};
```

## Security Considerations

1. **Authorization**: Only ADMIN role can manage courses
2. **Validation**: Validate all course data (code format, credits range, etc.)
3. **Audit Trail**: Log all course operations
4. **Data Integrity**: Prevent deletion of courses with resources
5. **Input Sanitization**: Sanitize all text inputs

## Performance Considerations

1. **Indexing**: Add indexes on code, name, category, status
2. **Caching**: Cache course lists
3. **Pagination**: Always use pagination
4. **Query Optimization**: Use efficient queries for course statistics
