# Design Document

## Overview

The Resources System extends the existing HadassahHub platform to enable authenticated users to upload, share, and manage course-related educational resources. The system integrates seamlessly with the existing Course Catalog and Authentication System, following the established layered architecture pattern (Controller → Service → Repository → Entity). The design emphasizes security, moderation workflows, and enhanced user experience through richer course detail pages.

## Architecture

The Resources System follows the existing Spring Boot layered architecture:

```
[ResourceController]     [CourseController (Enhanced)]
        |                           |
        v                           v
[ResourceService]        [CourseService (Enhanced)]
        |                           |
        v                           v
[ResourceRepository]     [CourseRepository]
        |                           |
        v                           v
[Resource Entity] ←→ [Course Entity] ←→ [User Entity]
```

### Integration Points

- **Authentication System**: Leverages existing JWT authentication and user roles
- **Course Catalog**: Extends course detail pages with resource listings
- **Database**: Adds new tables while maintaining referential integrity with existing schema

## Components and Interfaces

### 1. Entity Layer

#### Resource Entity
```java
@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;
    
    @Column(nullable = false)
    private String url; // External URL for MVP, can be file path in future
    
    @Column
    private String academicYear; // e.g., "2024-2025"
    
    @Column
    private String examTerm; // e.g., "Moed A", "Moed B"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.PENDING;
    
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column
    private LocalDateTime approvedAt;
    
    @Column
    private String rejectionReason;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

#### New Enums

**ResourceType**
```java
public enum ResourceType {
    EXAM,      // Past exams (URLs for now, files in future)
    HOMEWORK,  // Homework assignments (URLs for now, files in future)  
    SUMMARY,   // Course summaries/notes (URLs for now, files in future)
    LINK       // External links (always URLs)
}
```

**ResourceStatus**
```java
public enum ResourceStatus {
    PENDING,   // Awaiting approval
    APPROVED,  // Approved and visible
    REJECTED   // Rejected by moderator
}
```

### 2. Repository Layer

#### ResourceRepository
```java
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {
    List<Resource> findByCourseIdAndStatus(Long courseId, ResourceStatus status);
    List<Resource> findByUploadedByIdOrderByCreatedAtDesc(Long userId);
    List<Resource> findByStatusOrderByCreatedAtAsc(ResourceStatus status);
    List<Resource> findByCourseIdAndStatusAndType(Long courseId, ResourceStatus status, ResourceType type);
    long countByCourseIdAndStatus(Long courseId, ResourceStatus status);
    long countByCourseIdAndStatusAndType(Long courseId, ResourceStatus status, ResourceType type);
}
```

#### ResourceSpecifications
```java
public class ResourceSpecifications {
    public static Specification<Resource> hasStatus(ResourceStatus status);
    public static Specification<Resource> hasCourse(Long courseId);
    public static Specification<Resource> hasType(ResourceType type);
    public static Specification<Resource> hasUploader(Long userId);
    public static Specification<Resource> hasAcademicYear(String academicYear);
}
```

### 3. Service Layer

#### ResourceService
```java
@Service
@Transactional
public class ResourceService {
    // Resource Management
    public ResourceDTO createResource(CreateResourceRequestDTO request, String userEmail);
    public Optional<ResourceDTO> findResourceById(Long id);
    public List<ResourceDTO> findResourcesByCourse(Long courseId, ResourceType type);
    public List<ResourceDTO> findUserResources(String userEmail);
    
    // Moderation
    public List<ResourceDTO> findPendingResources();
    public ResourceDTO approveResource(Long resourceId, String moderatorEmail);
    public ResourceDTO rejectResource(Long resourceId, String moderatorEmail, String reason);
    
    // Statistics
    public ResourceStatsDTO getCourseResourceStats(Long courseId);
    
    // URL Validation (MVP - simple validation)
    public boolean isValidUrl(String url);
    public boolean isValidResourceType(ResourceType type);
    
    // Future: File management methods will be added here
    // public String uploadFile(MultipartFile file, ResourceType type) throws IOException;
    // public void deleteFile(String filePath);
}
```

#### Enhanced CourseService
```java
// Add to existing CourseService
public CourseDetailDTO getCourseWithResources(Long courseId);
public CourseResourceSummaryDTO getCourseResourceSummary(Long courseId);
```

### 4. Controller Layer

#### ResourceController
```java
@RestController
@RequestMapping("/api/resources")
@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
public class ResourceController {
    
    @PostMapping
    public ResponseEntity<ResourceDTO> createResource(@Valid @RequestBody CreateResourceRequestDTO request);
    
    @GetMapping("/my")
    public ResponseEntity<List<ResourceDTO>> getMyResources();
    
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getResource(@PathVariable Long id);
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id);
    
    @GetMapping("/{id}/download")
    public ResponseEntity<Void> accessResource(@PathVariable Long id); // Redirect to URL for MVP
}
```

#### Enhanced CourseController
```java
// Add to existing CourseController
@GetMapping("/{id}/resources")
public ResponseEntity<List<ResourceDTO>> getCourseResources(
    @PathVariable Long id,
    @RequestParam(required = false) ResourceType type
);

@GetMapping("/{id}/resources/stats")
public ResponseEntity<ResourceStatsDTO> getCourseResourceStats(@PathVariable Long id);

@PostMapping("/{id}/resources")
@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
public ResponseEntity<ResourceDTO> uploadCourseResource(
    @PathVariable Long id,
    @Valid @RequestBody CreateResourceRequestDTO request
);
```

#### AdminResourceController
```java
@RestController
@RequestMapping("/api/admin/resources")
@PreAuthorize("hasRole('ADMIN')")
public class AdminResourceController {
    
    @GetMapping("/pending")
    public ResponseEntity<List<ResourceDTO>> getPendingResources();
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<ResourceDTO> approveResource(@PathVariable Long id);
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<ResourceDTO> rejectResource(
        @PathVariable Long id,
        @RequestBody RejectResourceRequestDTO request
    );
}
```

## Data Models

### DTOs

#### ResourceDTO
```java
public record ResourceDTO(
    Long id,
    Long courseId,
    String courseName,
    String title,
    ResourceType type,
    String url,
    String academicYear,
    String examTerm,
    ResourceStatus status,
    String uploaderName,
    Long uploaderId,
    String approvedByName,
    LocalDateTime approvedAt,
    String rejectionReason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

#### CreateResourceRequestDTO
```java
public record CreateResourceRequestDTO(
    @NotNull Long courseId,
    @NotBlank @Size(max = 255) String title,
    @NotNull ResourceType type,
    @NotBlank String url,
    @Size(max = 50) String academicYear,
    @Size(max = 50) String examTerm
) {}
```

#### ResourceStatsDTO
```java
public record ResourceStatsDTO(
    Long courseId,
    long totalResources,
    long examCount,
    long homeworkCount,
    long summaryCount,
    long linkCount,
    LocalDateTime lastUpdated
) {}
```

#### Enhanced CourseDetailDTO
```java
public record CourseDetailDTO(
    Long id,
    String name,
    String description,
    CourseCategory category,
    Integer credits,
    StudyYear recommendedYear,
    List<CourseOfferingDTO> offerings,
    ResourceStatsDTO resourceStats,
    List<ResourceDTO> recentResources // Last 5 approved resources
) {}
```

## Error Handling

### Custom Exceptions
```java
public class ResourceNotFoundException extends RuntimeException {}
public class UnauthorizedResourceAccessException extends RuntimeException {}
public class InvalidUrlException extends RuntimeException {} // MVP: URL validation
// Future: File-related exceptions will be added
// public class InvalidFileTypeException extends RuntimeException {}
// public class FileSizeExceededException extends RuntimeException {}
public class ResourceAlreadyModerateException extends RuntimeException {}
```

### Global Exception Handler Extensions
```java
// Add to existing GlobalExceptionHandler
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex);

@ExceptionHandler(UnauthorizedResourceAccessException.class)
public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(UnauthorizedResourceAccessException ex);

@ExceptionHandler(InvalidUrlException.class)
public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException ex);
```

## Testing Strategy

### Unit Tests
- **ResourceService**: Test business logic for resource creation, approval, rejection
- **ResourceRepository**: Test custom queries and specifications
- **ResourceController**: Test endpoint behavior and security
- **File Upload Service**: Test URL validation and format checking (MVP)
- **Future**: Test file validation and storage when implemented

### Integration Tests
- **Resource Upload Flow**: End-to-end resource creation and approval
- **Course Resource Integration**: Test enhanced course details with resources
- **Security Integration**: Test role-based access control
- **File Management**: Test URL access and validation (MVP)
- **Future**: Test file upload, download, and deletion when implemented

### Test Data Strategy
- **ResourceTestDataBuilder**: Builder pattern for test resource creation
- **Enhanced DataSeeder**: Add sample resources for development
- **Test File Management**: Mock file operations in tests

## Security Considerations

### URL and Link Security (MVP)
- **URL Validation**: Validate URL format and accessibility
- **Safe Redirects**: Ensure external URLs are safe for redirection
- **Link Verification**: Optional verification that URLs are accessible
- **Malicious URL Detection**: Basic checks for suspicious URLs

### Future File Upload Security
- **File Type Validation**: Whitelist allowed file extensions per resource type
- **File Size Limits**: Enforce maximum file sizes (e.g., 10MB for documents)
- **Virus Scanning**: Integrate antivirus scanning for uploaded files
- **Secure Storage**: Store files outside web root with controlled access

### Access Control
- **Resource Visibility**: Only approved resources visible to students
- **Owner Permissions**: Users can only modify their own pending resources
- **Admin Privileges**: Admins can moderate any resource
- **Download Tracking**: Log resource downloads for analytics

### Data Validation
- **Input Sanitization**: Sanitize all user inputs, especially URLs
- **URL Validation**: Validate external URLs for format and basic safety
- **Academic Year Format**: Validate academic year format (e.g., "2024-2025")
- **Exam Term Validation**: Validate exam term values

## Performance Considerations

### Database Optimization
- **Indexing Strategy**: Index on course_id, status, type, uploaded_by for fast queries
- **Query Optimization**: Use specifications for complex filtering
- **Pagination**: Implement pagination for resource listings
- **Caching**: Cache resource statistics and counts

### Future File Management
- **CDN Integration**: Use CDN for file delivery (when file uploads added)
- **Lazy Loading**: Load resource content on demand
- **Compression**: Compress files during upload (future)
- **Cleanup Jobs**: Scheduled cleanup of orphaned files (future)

## Future Enhancements

### Phase 2 Features
- **Resource Ratings**: Allow users to rate resource quality
- **Resource Comments**: Enable discussion threads on resources
- **Advanced Search**: Full-text search across resource titles and descriptions
- **Resource Collections**: Allow users to create curated resource collections

### Technical Improvements
- **File Storage Service**: Migrate to cloud storage (AWS S3, Google Cloud Storage)
- **Real-time Notifications**: Notify users of resource approval/rejection
- **Analytics Dashboard**: Admin dashboard with resource usage statistics
- **API Versioning**: Implement API versioning for backward compatibility