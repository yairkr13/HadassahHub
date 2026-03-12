# File Upload System - Design Document

## Overview

The File Upload System extends HadassahHub's existing resource sharing functionality to support direct file uploads alongside URL-based resources. This design maintains backward compatibility with the existing architecture while adding secure file storage, validation, and retrieval capabilities.

The system follows Spring Boot best practices and integrates seamlessly with the existing moderation workflow. Files are stored locally in the filesystem with metadata tracked in the PostgreSQL database. The design prioritizes security through filename sanitization, path validation, MIME type verification, and size limits.

Key design principles:
- Extend existing endpoints rather than create new ones
- Maintain backward compatibility with URL resources
- Follow existing Spring Boot layered architecture
- Implement defense-in-depth security measures
- Provide clear error messages for validation failures

## Architecture

### High-Level Architecture

The file upload system follows the existing three-tier Spring Boot architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         ResourceController (REST API)                 │  │
│  │  - Handles multipart/form-data requests              │  │
│  │  - Validates request parameters                       │  │
│  │  - Manages file download responses                    │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│  ┌──────────────────┐         ┌──────────────────────────┐ │
│  │ ResourceService  │ ←────→  │  FileStorageService      │ │
│  │ - Business logic │         │  - File I/O operations   │ │
│  │ - Moderation     │         │  - Filename generation   │ │
│  │ - Access control │         │  - MIME validation       │ │
│  └──────────────────┘         │  - Path sanitization     │ │
│                                └──────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Persistence Layer                         │
│  ┌──────────────────┐         ┌──────────────────────────┐ │
│  │ ResourceRepo     │         │  Filesystem Storage      │ │
│  │ (JPA/Hibernate)  │         │  ./uploads/resources/    │ │
│  └──────────────────┘         └──────────────────────────┘ │
│           ↓                              ↓                   │
│  ┌──────────────────┐         ┌──────────────────────────┐ │
│  │  PostgreSQL DB   │         │  Local Filesystem        │ │
│  └──────────────────┘         └──────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Request Flow

#### File Upload Flow

```
User → POST /api/resources (multipart/form-data)
  ↓
ResourceController
  - Validates @RequestParam MultipartFile
  - Validates @Valid CreateResourceRequestDTO
  - Extracts user ID from JWT
  ↓
ResourceService.createResource()
  - Validates course exists
  - Validates user exists
  - Checks for duplicates
  - Validates file vs URL mutual exclusivity
  ↓
FileStorageService.storeFile()
  - Validates MIME type
  - Validates file size
  - Sanitizes filename
  - Generates UUID-based filename
  - Validates path safety
  - Writes file to disk
  - Returns file metadata
  ↓
ResourceService (continued)
  - Creates Resource entity with file metadata
  - Saves to database
  - Returns ResourceDTO
  ↓
ResourceController
  - Returns HTTP 201 Created with ResourceDTO
```

#### File Download Flow

```
User → GET /api/resources/{id}/download
  ↓
ResourceController
  - Extracts user ID from JWT
  - Checks user role
  ↓
ResourceService.canAccessResource()
  - Checks if resource is approved OR
  - User is owner OR
  - User is admin
  ↓
FileStorageService.loadFileAsResource()
  - Validates resource is file-based
  - Constructs file path
  - Validates path safety
  - Loads file as Resource
  - Returns file content
  ↓
ResourceController
  - Sets Content-Disposition header
  - Sets Content-Type header
  - Streams file to response
```

#### File Deletion Flow

```
User → DELETE /api/resources/{id}
  ↓
ResourceController
  - Validates ownership or admin role
  ↓
ResourceService.deleteResource()
  - Checks if resource is file-based
  - Calls FileStorageService.deleteFile()
  - Deletes database record
  ↓
FileStorageService.deleteFile()
  - Constructs file path
  - Validates path safety
  - Deletes physical file
  - Logs errors if deletion fails
```

### Component Responsibilities

**ResourceController**
- Accept multipart/form-data requests
- Validate that either file OR url is provided (not both, not neither)
- Handle file download requests with proper headers
- Return appropriate HTTP status codes

**ResourceService**
- Orchestrate file upload workflow
- Maintain existing moderation logic
- Coordinate between FileStorageService and database
- Enforce access control rules
- Handle file deletion on resource removal

**FileStorageService** (new component)
- Validate MIME types against whitelist
- Validate file sizes against limit
- Generate secure filenames using UUID
- Sanitize original filenames
- Perform path traversal validation
- Execute file I/O operations
- Load files for download
- Delete physical files

**Resource Entity**
- Store file metadata (fileName, filePath, fileSize, mimeType)
- Add isFileUpload boolean flag
- Make url field nullable
- Maintain backward compatibility

## Components and Interfaces

### FileStorageService Interface

```java
public interface FileStorageService {
    /**
     * Stores an uploaded file and returns metadata.
     * 
     * @param file The multipart file to store
     * @return FileMetadata containing stored file information
     * @throws InvalidFileTypeException if MIME type is not allowed
     * @throws FileSizeLimitExceededException if file exceeds size limit
     * @throws FileStorageException if I/O error occurs
     */
    FileMetadata storeFile(MultipartFile file);
    
    /**
     * Loads a file as a Spring Resource for download.
     * 
     * @param filePath The stored file path
     * @return Resource containing file content
     * @throws FileStorageException if file not found or I/O error
     */
    Resource loadFileAsResource(String filePath);
    
    /**
     * Deletes a physical file from storage.
     * 
     * @param filePath The stored file path
     * @throws FileStorageException if deletion fails
     */
    void deleteFile(String filePath);
    
    /**
     * Validates MIME type against whitelist.
     * 
     * @param file The file to validate
     * @return true if MIME type is allowed
     */
    boolean isValidMimeType(MultipartFile file);
    
    /**
     * Validates file size against limit.
     * 
     * @param file The file to validate
     * @return true if size is within limit
     */
    boolean isValidFileSize(MultipartFile file);
}
```

### FileMetadata Record

```java
public record FileMetadata(
    String originalFileName,
    String storedFileName,
    String filePath,
    long fileSize,
    String mimeType
) {}
```

### Modified ResourceService Methods

```java
// Modified signature to accept optional file
public ResourceDTO createResource(
    CreateResourceRequestDTO request, 
    MultipartFile file,  // new parameter, can be null
    Long userId
);

// Modified to handle file deletion
public boolean deleteResource(Long resourceId, Long userId, boolean isAdmin);

// New method for file access
public Resource getFileForDownload(Long resourceId, Long userId, boolean isAdmin);
```

### Modified ResourceController Endpoints

```java
// Modified to accept multipart/form-data
@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public ResponseEntity<ResourceDTO> createResource(
    @RequestPart(value = "file", required = false) MultipartFile file,
    @RequestPart(value = "data") @Valid CreateResourceRequestDTO request,
    Authentication authentication
);

// New endpoint for file download
@GetMapping("/{id}/download")
public ResponseEntity<Resource> downloadFile(
    @PathVariable Long id,
    Authentication authentication
);
```

### Exception Classes

```java
// New exceptions for file operations
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Throwable cause);
}

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String message);
}

public class FileSizeLimitExceededException extends RuntimeException {
    public FileSizeLimitExceededException(String message);
}

public class FileDownloadException extends RuntimeException {
    public FileDownloadException(String message, Throwable cause);
}
```

## Data Models

### Resource Entity Extensions

```java
@Entity
@Table(name = "resources")
public class Resource {
    // Existing fields...
    
    // Modified: make url nullable
    @Column(nullable = true)  // Changed from nullable = false
    private String url;
    
    // New fields for file uploads
    @Column(name = "is_file_upload", nullable = false)
    private Boolean isFileUpload = false;
    
    @Column(name = "file_name")
    private String fileName;  // Original filename for display
    
    @Column(name = "file_path")
    private String filePath;  // Stored path (UUID-based)
    
    @Column(name = "file_size")
    private Long fileSize;    // Size in bytes
    
    @Column(name = "mime_type")
    private String mimeType;  // Content type
    
    // New business methods
    public boolean isFileResource() {
        return Boolean.TRUE.equals(isFileUpload);
    }
    
    public boolean isUrlResource() {
        return !Boolean.TRUE.equals(isFileUpload);
    }
}
```

### Database Migration

```sql
-- Migration: Add file upload support to resources table

ALTER TABLE resources 
    ALTER COLUMN url DROP NOT NULL;

ALTER TABLE resources 
    ADD COLUMN is_file_upload BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE resources 
    ADD COLUMN file_name VARCHAR(255);

ALTER TABLE resources 
    ADD COLUMN file_path VARCHAR(512);

ALTER TABLE resources 
    ADD COLUMN file_size BIGINT;

ALTER TABLE resources 
    ADD COLUMN mime_type VARCHAR(127);

-- Add check constraint to ensure either url or file fields are populated
-- Note: Skipping database-level constraint as per design decision
-- Validation will be enforced at application layer

-- Add index for file path lookups
CREATE INDEX idx_resources_file_path ON resources(file_path) 
    WHERE file_path IS NOT NULL;

-- Add index for file upload flag
CREATE INDEX idx_resources_is_file_upload ON resources(is_file_upload);
```

### CreateResourceRequestDTO Extensions

```java
public record CreateResourceRequestDTO(
    @NotNull(message = "Course ID is required")
    Long courseId,
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,
    
    @NotNull(message = "Resource type is required")
    ResourceType type,
    
    // Modified: url is now optional
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    @Pattern(
        regexp = "^https?://.*|^$", 
        message = "URL must be a valid HTTP or HTTPS URL"
    )
    String url,  // Can be null if file is provided
    
    @Size(max = 50, message = "Academic year must not exceed 50 characters")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$|^$", 
        message = "Academic year must be in format YYYY-YYYY"
    )
    String academicYear,
    
    @Size(max = 50, message = "Exam term must not exceed 50 characters")
    String examTerm
) {
    // New validation method
    public boolean hasUrlOrFile(boolean hasFile) {
        boolean hasUrl = url != null && !url.trim().isEmpty();
        
        // Exactly one must be provided
        return hasUrl ^ hasFile;  // XOR operation
    }
}
```

### ResourceDTO Extensions

```java
public record ResourceDTO(
    Long id,
    String title,
    ResourceType type,
    String url,              // Null for file resources
    String academicYear,
    String examTerm,
    ResourceStatus status,
    String rejectionReason,
    Long courseId,
    String courseName,
    Long uploadedById,
    String uploaderName,
    Long approvedById,
    String approverName,
    LocalDateTime approvedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    
    // New fields for file resources
    Boolean isFileUpload,
    String fileName,         // Original filename
    Long fileSize,           // Size in bytes
    String mimeType          // Content type
    // Note: filePath is NOT exposed for security
) {
    // Helper method
    public boolean isFileResource() {
        return Boolean.TRUE.equals(isFileUpload);
    }
}
```

### Configuration Properties

```java
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    
    private String uploadDir = "./uploads/resources/";
    private long maxFileSize = 10485760L; // 10MB in bytes
    private List<String> allowedMimeTypes = List.of(
        "application/pdf",
        "image/png",
        "image/jpeg",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain"
    );
    
    // Getters and setters
}
```

### application.properties Configuration

```properties
# File Upload Configuration
file.upload.upload-dir=./uploads/resources/
file.upload.max-file-size=10485760
file.upload.allowed-mime-types=application/pdf,image/png,image/jpeg,application/vnd.openxmlformats-officedocument.wordprocessingml.document,text/plain

# Spring multipart configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: File resource entity completeness

*For any* file resource created through upload, the Resource entity SHALL contain all required file metadata fields (fileName, filePath, fileSize, mimeType) populated with non-null values and isFileUpload set to true.

**Validates: Requirements 1.2, 1.5**

### Property 2: Endpoint resource type support

*For any* resource creation request (file or URL), the POST /api/resources endpoint SHALL successfully process the request and return the appropriate ResourceDTO with correct metadata based on the resource type.

**Validates: Requirements 1.3, 1.4**

### Property 3: Invalid MIME type rejection

*For any* file submitted with a MIME type not in the allowed list (application/pdf, image/png, image/jpeg, application/vnd.openxmlformats-officedocument.wordprocessingml.document, text/plain), the File_Storage_Service SHALL reject the upload and throw InvalidFileTypeException.

**Validates: Requirements 2.1, 2.3**

### Property 4: MIME type content-based validation

*For any* file submitted, the File_Storage_Service SHALL validate MIME type by inspecting file content rather than relying solely on file extension, ensuring files with mismatched extensions and content are correctly validated.

**Validates: Requirements 2.4**

### Property 5: File size validation

*For any* file submitted, the File_Storage_Service SHALL validate that the file size does not exceed 10485760 bytes, rejecting files that exceed this limit with FileSizeLimitExceededException.

**Validates: Requirements 3.1, 3.2 (edge case)**

### Property 6: Filename sanitization

*For any* filename containing path traversal characters (../, ..\, absolute paths), the File_Storage_Service SHALL sanitize the filename by removing or replacing these characters before storage.

**Validates: Requirements 4.1**

### Property 7: UUID-based filename generation

*For any* file stored, the File_Storage_Service SHALL generate a unique filename in the format {UUID}.{extension}, ensuring no filename collisions occur even for files uploaded simultaneously.

**Validates: Requirements 4.2**

### Property 8: Path traversal prevention

*For any* file path generated by the File_Storage_Service, the path SHALL not contain parent directory references (../) and SHALL resolve to a location within the configured upload directory.

**Validates: Requirements 4.5**

### Property 9: Download access control

*For any* file download request, the Resource_Service SHALL grant access only if the resource is approved OR the requesting user is the resource owner OR the requesting user has admin role, returning HTTP 403 Forbidden otherwise.

**Validates: Requirements 5.2, 5.3**

### Property 10: Download response headers

*For any* successful file download, the Resource_Controller SHALL set both Content-Disposition header to "attachment; filename={originalFileName}" and Content-Type header to the stored MIME type.

**Validates: Requirements 5.4, 5.5**

### Property 11: URL resource download rejection

*For any* download request for a URL-based resource (isFileUpload = false), the Resource_Controller SHALL return HTTP 400 Bad Request with an appropriate error message.

**Validates: Requirements 5.6**

### Property 12: Physical file deletion on resource removal

*For any* file resource deletion by an authorized user (owner or admin), the Resource_Service SHALL attempt to delete the physical file from storage, and if deletion fails, SHALL log the error and continue with database record deletion.

**Validates: Requirements 6.1, 6.2, 6.3**

### Property 13: URL resource deletion without file operations

*For any* URL resource deletion, the Resource_Service SHALL complete the deletion without attempting any file system operations.

**Validates: Requirements 6.4**

### Property 14: Backward compatibility for URL resources

*For any* URL-based resource creation request (with url field populated and no file), the Resource_Service SHALL create a Resource entity with url field populated, isFileUpload set to false, and all file-specific fields set to null, maintaining backward compatibility.

**Validates: Requirements 7.1, 7.2**

### Property 15: Resource type field population

*For any* resource created (file or URL), the Resource entity SHALL have exactly one set of fields populated: either (url, isFileUpload=false) for URL resources OR (fileName, filePath, fileSize, mimeType, isFileUpload=true) for file resources, never both or neither.

**Validates: Requirements 7.3**

### Property 16: Moderation workflow consistency

*For any* resource created (file or URL), the Resource_Service SHALL apply the same moderation workflow, creating the resource with PENDING status and requiring admin approval before becoming visible on course pages.

**Validates: Requirements 7.4**

### Property 17: Resource metadata retrieval

*For any* resource retrieval operation, the Resource_Service SHALL return a ResourceDTO containing the appropriate metadata fields based on resource type (file fields for file resources, url field for URL resources).

**Validates: Requirements 7.5, 10.1, 10.2**

### Property 18: Mutual exclusivity validation

*For any* resource creation request, the Resource_Controller SHALL validate that exactly one of url or file is provided (not both, not neither), rejecting requests that violate this constraint with HTTP 400 Bad Request.

**Validates: Requirements 8.1, 8.2 (edge case), 8.3 (edge case)**

### Property 19: Required field validation

*For any* resource creation request, the Resource_Controller SHALL validate that title, courseId, and type fields are provided and non-empty, rejecting requests with missing required fields.

**Validates: Requirements 8.4**

### Property 20: Descriptive validation errors

*For any* validation failure (MIME type, file size, required fields, mutual exclusivity), the system SHALL return an error response containing a descriptive error message that clearly indicates what validation rule was violated.

**Validates: Requirements 8.5**

### Property 21: Storage exception handling

*For any* I/O error during file operations (write, read, delete), the File_Storage_Service SHALL throw FileStorageException, which the Resource_Controller SHALL catch and convert to HTTP 500 Internal Server Error with an appropriate error message.

**Validates: Requirements 9.1, 9.4**

### Property 22: File operation error logging

*For any* file operation failure (I/O error, directory creation failure, deletion failure), the File_Storage_Service SHALL log the error with relevant details including filename, operation type, and exception information.

**Validates: Requirements 9.5**

### Property 23: Directory creation failure handling

*For any* directory creation failure when the upload directory does not exist, the File_Storage_Service SHALL throw FileStorageException with a descriptive message indicating the directory creation failure.

**Validates: Requirements 9.3**

### Property 24: Internal path protection

*For any* API response containing resource information (single resource or list), the Resource_Controller SHALL not include the internal file path (filePath field) in the response DTO, exposing only safe metadata like fileName.

**Validates: Requirements 10.3**

### Property 25: List response metadata inclusion

*For any* resource list response containing file resources, each file resource in the list SHALL include file metadata fields (fileName, fileSize, mimeType, isFileUpload) in the response.

**Validates: Requirements 10.4**


## Error Handling

### Exception Hierarchy

```
RuntimeException
├── FileStorageException (new)
│   ├── InvalidFileTypeException (new)
│   ├── FileSizeLimitExceededException (new)
│   └── FileDownloadException (new)
├── ResourceNotFoundException (existing)
├── UnauthorizedResourceAccessException (existing)
└── InvalidUrlException (existing)
```

### Error Scenarios and Responses

| Scenario | Exception | HTTP Status | Error Message |
|----------|-----------|-------------|---------------|
| Unsupported MIME type | InvalidFileTypeException | 400 Bad Request | "File type not allowed. Supported types: PDF, PNG, JPEG, DOCX, TXT" |
| File size exceeds limit | FileSizeLimitExceededException | 400 Bad Request | "File size exceeds maximum limit of 10MB" |
| Both url and file provided | IllegalArgumentException | 400 Bad Request | "Provide either url or file, not both" |
| Neither url nor file provided | IllegalArgumentException | 400 Bad Request | "Either url or file must be provided" |
| Missing required fields | MethodArgumentNotValidException | 400 Bad Request | "Validation failed: {field} is required" |
| File not found for download | FileDownloadException | 404 Not Found | "File not found" |
| Unauthorized download | UnauthorizedResourceAccessException | 403 Forbidden | "You do not have permission to access this resource" |
| Download URL resource | IllegalArgumentException | 400 Bad Request | "Cannot download URL-based resource" |
| I/O error during upload | FileStorageException | 500 Internal Server Error | "Failed to store file" |
| I/O error during download | FileDownloadException | 500 Internal Server Error | "Failed to retrieve file" |
| Directory creation failure | FileStorageException | 500 Internal Server Error | "Failed to initialize storage directory" |

### GlobalExceptionHandler Extensions

```java
@ExceptionHandler(InvalidFileTypeException.class)
public ResponseEntity<ErrorResponse> handleInvalidFileType(InvalidFileTypeException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "Invalid file type",
        ex.getMessage(),
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
}

@ExceptionHandler(FileSizeLimitExceededException.class)
public ResponseEntity<ErrorResponse> handleFileSizeLimit(FileSizeLimitExceededException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "File size limit exceeded",
        ex.getMessage(),
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
}

@ExceptionHandler(FileStorageException.class)
public ResponseEntity<ErrorResponse> handleFileStorage(FileStorageException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "File storage error",
        ex.getMessage(),
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}

@ExceptionHandler(FileDownloadException.class)
public ResponseEntity<ErrorResponse> handleFileDownload(FileDownloadException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "File download error",
        ex.getMessage(),
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}

@ExceptionHandler(MaxUploadSizeExceededException.class)
public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        "File size limit exceeded",
        "File size exceeds maximum limit of 10MB",
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
}
```

### Error Handling Principles

1. **Fail Fast**: Validate inputs at the controller layer before processing
2. **Descriptive Messages**: Provide clear, actionable error messages to users
3. **Security**: Never expose internal file paths or system details in error messages
4. **Logging**: Log all errors with sufficient context for debugging
5. **Graceful Degradation**: Continue database operations even if file deletion fails
6. **Consistent Format**: Use ErrorResponse DTO for all error responses

### Logging Strategy

```java
// FileStorageService logging examples
logger.info("Storing file: originalName={}, size={}, mimeType={}", 
    originalFilename, file.getSize(), mimeType);

logger.error("Failed to store file: originalName={}, error={}", 
    originalFilename, e.getMessage(), e);

logger.warn("Failed to delete file: path={}, continuing with database deletion", 
    filePath);

logger.debug("Generated filename: original={}, stored={}", 
    originalFilename, storedFilename);
```

## Testing Strategy

### Dual Testing Approach

The file upload system requires both unit tests and property-based tests for comprehensive coverage:

- **Unit tests**: Verify specific examples, edge cases, and integration points
- **Property-based tests**: Verify universal properties across randomized inputs

Both testing approaches are complementary and necessary. Unit tests catch concrete bugs in specific scenarios, while property-based tests verify general correctness across a wide range of inputs.

### Property-Based Testing Configuration

**Library**: Use **jqwik** for Java property-based testing

**Configuration**:
- Minimum 100 iterations per property test (configured via @Property annotation)
- Each property test must reference its design document property via comment
- Tag format: `// Feature: file-upload-system, Property {number}: {property_text}`

**Example Property Test Structure**:

```java
@Property(tries = 100)
// Feature: file-upload-system, Property 7: UUID-based filename generation
void generatedFilenamesShouldBeUniqueAndFollowUuidPattern(
    @ForAll @StringLength(min = 1, max = 255) String originalFilename,
    @ForAll @StringLength(min = 1, max = 10) String extension
) {
    // Test that generated filenames follow UUID.extension pattern
    // and are unique across multiple invocations
}
```

### Unit Testing Strategy

#### FileStorageService Tests

**Focus Areas**:
- MIME type validation with each allowed type (PDF, PNG, JPEG, DOCX, TXT)
- File size validation at boundary (exactly 10MB, 10MB + 1 byte)
- Filename sanitization with path traversal attempts
- UUID filename generation format
- Directory creation when missing
- File storage and retrieval
- File deletion
- Error handling for I/O failures

**Example Tests**:
```java
@Test
void shouldAcceptPdfFile() {
    // Test specific allowed MIME type
}

@Test
void shouldRejectFileExactlyOverSizeLimit() {
    // Test boundary condition: 10485761 bytes
}

@Test
void shouldSanitizeFilenameWithPathTraversal() {
    // Test specific path traversal patterns: ../, ..\, /etc/passwd
}

@Test
void shouldCreateDirectoryIfNotExists() {
    // Test directory initialization
}
```

#### ResourceService Tests

**Focus Areas**:
- File resource creation with valid file
- URL resource creation (backward compatibility)
- Mutual exclusivity validation (file XOR url)
- File deletion triggers physical file deletion
- URL deletion skips file operations
- Access control for downloads
- Moderation workflow applies to both types

**Example Tests**:
```java
@Test
void shouldCreateFileResourceWithMetadata() {
    // Test file resource creation
}

@Test
void shouldRejectRequestWithBothUrlAndFile() {
    // Test mutual exclusivity edge case
}

@Test
void shouldDeletePhysicalFileWhenDeletingFileResource() {
    // Test file cleanup
}
```

#### ResourceController Tests

**Focus Areas**:
- Multipart/form-data request handling
- File download with correct headers
- Access control enforcement
- Error response format
- Validation error messages

**Example Tests**:
```java
@Test
void shouldAcceptMultipartFormDataRequest() {
    // Test endpoint accepts multipart content type
}

@Test
void shouldSetCorrectHeadersForFileDownload() {
    // Test Content-Disposition and Content-Type headers
}

@Test
void shouldReturn403ForUnauthorizedDownload() {
    // Test access control
}
```

### Property-Based Testing Strategy

Property-based tests should focus on universal behaviors that must hold for all valid inputs. Each correctness property from the design document should have a corresponding property-based test.

#### Key Properties to Test

**Property 1: File resource entity completeness**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 1: File resource entity completeness
void fileResourcesShouldHaveAllMetadataFields(
    @ForAll("validFiles") MockMultipartFile file,
    @ForAll("validResourceRequest") CreateResourceRequestDTO request
) {
    ResourceDTO result = resourceService.createResource(request, file, userId);
    
    assertThat(result.isFileUpload()).isTrue();
    assertThat(result.fileName()).isNotNull();
    assertThat(result.fileSize()).isGreaterThan(0);
    assertThat(result.mimeType()).isNotNull();
}
```

**Property 3: Invalid MIME type rejection**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 3: Invalid MIME type rejection
void shouldRejectFilesWithInvalidMimeTypes(
    @ForAll("invalidMimeTypes") String mimeType,
    @ForAll @ByteArray(minLength = 1, maxLength = 1000) byte[] content
) {
    MockMultipartFile file = new MockMultipartFile("file", "test.bin", mimeType, content);
    
    assertThatThrownBy(() -> fileStorageService.storeFile(file))
        .isInstanceOf(InvalidFileTypeException.class);
}
```

**Property 6: Filename sanitization**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 6: Filename sanitization
void shouldSanitizeFilenamesWithPathTraversal(
    @ForAll("filenamesWithPathTraversal") String maliciousFilename
) {
    String sanitized = fileStorageService.sanitizeFilename(maliciousFilename);
    
    assertThat(sanitized).doesNotContain("../");
    assertThat(sanitized).doesNotContain("..\\");
    assertThat(sanitized).doesNotStartWith("/");
    assertThat(sanitized).doesNotContain(":");
}
```

**Property 7: UUID-based filename generation**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 7: UUID-based filename generation
void generatedFilenamesShouldFollowUuidPattern(
    @ForAll @StringLength(min = 1, max = 255) String originalFilename
) {
    String generated = fileStorageService.generateStoredFilename(originalFilename);
    
    // Should match pattern: {UUID}.{extension}
    assertThat(generated).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\..+$");
}
```

**Property 9: Download access control**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 9: Download access control
void shouldEnforceAccessControlForDownloads(
    @ForAll("resources") Resource resource,
    @ForAll("users") User user,
    @ForAll boolean isAdmin
) {
    boolean canAccess = resourceService.canAccessResource(
        resource.getId(), user.getId(), isAdmin
    );
    
    boolean expected = resource.isApproved() || 
                      resource.isOwnedBy(user) || 
                      isAdmin;
    
    assertThat(canAccess).isEqualTo(expected);
}
```

**Property 12: Physical file deletion on resource removal**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 12: Physical file deletion on resource removal
void shouldDeletePhysicalFileWhenDeletingFileResource(
    @ForAll("fileResources") Resource fileResource,
    @ForAll("authorizedUsers") User user
) {
    // Store a test file
    Path testFile = Paths.get(fileResource.getFilePath());
    Files.createFile(testFile);
    
    resourceService.deleteResource(fileResource.getId(), user.getId(), user.isAdmin());
    
    assertThat(Files.exists(testFile)).isFalse();
}
```

**Property 18: Mutual exclusivity validation**
```java
@Property(tries = 100)
// Feature: file-upload-system, Property 18: Mutual exclusivity validation
void shouldEnforceMutualExclusivityOfUrlAndFile(
    @ForAll("resourceRequests") CreateResourceRequestDTO request,
    @ForAll("optionalFiles") Optional<MockMultipartFile> file
) {
    boolean hasUrl = request.url() != null && !request.url().isEmpty();
    boolean hasFile = file.isPresent();
    
    if (hasUrl && hasFile) {
        assertThatThrownBy(() -> resourceController.createResource(file.orElse(null), request, auth))
            .satisfies(ex -> assertThat(((ResponseEntity<?>) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST));
    } else if (!hasUrl && !hasFile) {
        assertThatThrownBy(() -> resourceController.createResource(null, request, auth))
            .satisfies(ex -> assertThat(((ResponseEntity<?>) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST));
    } else {
        // Should succeed
        assertThatCode(() -> resourceController.createResource(file.orElse(null), request, auth))
            .doesNotThrowAnyException();
    }
}
```

### Test Data Generators

Property-based tests require custom generators for domain objects:

```java
@Provide
Arbitrary<MockMultipartFile> validFiles() {
    return Combinators.combine(
        Arbitraries.of("application/pdf", "image/png", "image/jpeg", 
                      "application/vnd.openxmlformats-officedocument.wordprocessingml.document", 
                      "text/plain"),
        Arbitraries.bytes().array(byte[].class).ofMinSize(1).ofMaxSize(10485760),
        Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100)
    ).as((mimeType, content, filename) -> 
        new MockMultipartFile("file", filename, mimeType, content)
    );
}

@Provide
Arbitrary<String> invalidMimeTypes() {
    return Arbitraries.of(
        "application/x-executable",
        "application/x-sh",
        "text/html",
        "application/javascript",
        "image/svg+xml"
    );
}

@Provide
Arbitrary<String> filenamesWithPathTraversal() {
    return Arbitraries.of(
        "../../../etc/passwd",
        "..\\..\\..\\windows\\system32\\config\\sam",
        "/etc/passwd",
        "C:\\Windows\\System32\\config\\sam",
        "test/../../../etc/passwd",
        "test/../../file.pdf"
    );
}
```

### Integration Testing

Integration tests should verify the complete flow from HTTP request to file storage:

```java
@SpringBootTest
@AutoConfigureMockMvc
class FileUploadIntegrationTest {
    
    @Test
    void shouldUploadFileAndStoreInDatabase() {
        // Test complete upload flow
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "test content".getBytes()
        );
        
        mockMvc.perform(multipart("/api/resources")
                .file(file)
                .param("courseId", "1")
                .param("title", "Test Resource")
                .param("type", "SUMMARY")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.isFileUpload").value(true))
            .andExpect(jsonPath("$.fileName").value("test.pdf"))
            .andExpect(jsonPath("$.fileSize").value(12));
        
        // Verify file exists on disk
        // Verify database record
    }
    
    @Test
    void shouldDownloadFileWithCorrectHeaders() {
        // Test complete download flow
    }
}
```

### Test Coverage Goals

- **Line Coverage**: Minimum 80% for all new code
- **Branch Coverage**: Minimum 75% for all new code
- **Property Tests**: One test per correctness property (25 properties)
- **Unit Tests**: Comprehensive coverage of edge cases and error conditions
- **Integration Tests**: Cover main user flows (upload, download, delete)

### Testing Checklist

- [ ] All 25 correctness properties have corresponding property-based tests
- [ ] Each allowed MIME type has a unit test
- [ ] File size boundary conditions are tested
- [ ] Path traversal attempts are tested with multiple patterns
- [ ] Backward compatibility with URL resources is verified
- [ ] Access control is tested for all user roles
- [ ] Error handling is tested for all exception types
- [ ] File cleanup on deletion is verified
- [ ] Multipart request handling is tested
- [ ] Download headers are verified
- [ ] Integration tests cover happy path and error scenarios

