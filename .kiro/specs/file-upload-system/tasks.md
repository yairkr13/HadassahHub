# Implementation Plan: File Upload System

## Overview

This implementation plan extends HadassahHub's resource sharing functionality to support direct file uploads alongside URL-based resources. The implementation follows Spring Boot best practices, maintains backward compatibility, and implements defense-in-depth security measures including MIME type validation, file size limits, filename sanitization, and path traversal prevention.

The tasks are organized to build incrementally: first establishing the file storage infrastructure, then extending the data model, followed by API layer modifications, and finally comprehensive testing.

## Tasks

- [x] 1. Create FileStorageService with file operations
  - [x] 1.1 Create FileUploadProperties configuration class
    - Create `@ConfigurationProperties` class with uploadDir, maxFileSize, and allowedMimeTypes
    - Add default values matching requirements (10MB limit, 5 allowed MIME types)
    - _Requirements: 2.2, 3.2, 4.3_
  
  - [x] 1.2 Create FileMetadata record
    - Define record with originalFileName, storedFileName, filePath, fileSize, mimeType
    - _Requirements: 1.5_
  
  - [x] 1.3 Create custom exception classes
    - Create FileStorageException, InvalidFileTypeException, FileSizeLimitExceededException, FileDownloadException
    - Each exception should extend RuntimeException with appropriate constructors
    - _Requirements: 9.1, 2.3, 3.2_
  
  - [x] 1.4 Create FileStorageService interface
    - Define interface with storeFile, loadFileAsResource, deleteFile, isValidMimeType, isValidFileSize methods
    - Add comprehensive JavaDoc for each method
    - _Requirements: 1.2, 2.1, 3.1, 4.1, 5.1, 6.1_
  
  - [x] 1.5 Implement FileStorageServiceImpl - MIME type validation
    - Implement isValidMimeType using Apache Tika for content-based detection
    - Validate against whitelist from FileUploadProperties
    - _Requirements: 2.1, 2.2, 2.4_
  
  - [x] 1.6 Implement FileStorageServiceImpl - file size validation
    - Implement isValidFileSize checking against maxFileSize from properties
    - _Requirements: 3.1, 3.2_
  
  - [x] 1.7 Implement FileStorageServiceImpl - filename sanitization
    - Create sanitizeFilename method removing path traversal characters (../, ..\, :, absolute paths)
    - _Requirements: 4.1_
  
  - [x] 1.8 Implement FileStorageServiceImpl - UUID filename generation
    - Create generateStoredFilename method using UUID.randomUUID() + file extension
    - Extract extension from sanitized original filename
    - _Requirements: 4.2_
  
  - [x] 1.9 Implement FileStorageServiceImpl - path validation
    - Create validatePath method ensuring no parent directory references
    - Verify resolved path is within upload directory
    - _Requirements: 4.5, 5.2_
  
  - [x] 1.10 Implement FileStorageServiceImpl - storeFile method
    - Validate MIME type and file size
    - Sanitize filename and generate UUID-based stored filename
    - Create upload directory if not exists
    - Write file to disk with error handling
    - Return FileMetadata
    - Add logging for all operations
    - _Requirements: 1.2, 2.1, 2.3, 3.1, 3.2, 4.1, 4.2, 4.3, 9.2, 9.3, 9.5_
  
  - [x] 1.11 Implement FileStorageServiceImpl - loadFileAsResource method
    - Construct file path and validate path safety
    - Load file as Spring Resource
    - Throw FileDownloadException if file not found
    - _Requirements: 5.1, 5.2_
  
  - [x] 1.12 Implement FileStorageServiceImpl - deleteFile method
    - Validate path safety
    - Delete physical file
    - Log errors but don't throw exceptions (graceful degradation)
    - _Requirements: 6.1, 6.2, 6.3, 9.5_

- [x] 2. Extend Resource entity with file-related fields
  - [x] 2.1 Add file-related fields to Resource entity
    - Make url field nullable
    - Add isFileUpload (Boolean, default false), fileName, filePath, fileSize, mimeType fields
    - Add JPA annotations (@Column with appropriate constraints)
    - _Requirements: 1.5, 7.2, 7.3_
  
  - [x] 2.2 Add business methods to Resource entity
    - Add isFileResource() and isUrlResource() helper methods
    - _Requirements: 7.3_

- [x] 3. Create database migration
  - [x] 3.1 Create Flyway migration script
    - Alter resources table: make url nullable
    - Add columns: is_file_upload (NOT NULL DEFAULT false), file_name, file_path, file_size, mime_type
    - Create index on file_path (WHERE file_path IS NOT NULL)
    - Create index on is_file_upload
    - _Requirements: 1.5, 7.2_

- [x] 4. Extend DTOs (CreateResourceRequestDTO, ResourceDTO)
  - [x] 4.1 Modify CreateResourceRequestDTO
    - Make url field optional (remove @NotNull, keep @Pattern validation)
    - Add hasUrlOrFile validation method using XOR logic
    - Update JavaDoc to reflect optional url
    - _Requirements: 7.1, 8.1, 8.2, 8.3_
  
  - [x] 4.2 Extend ResourceDTO
    - Add isFileUpload, fileName, fileSize, mimeType fields
    - Add isFileResource() helper method
    - Ensure filePath is NOT included (security requirement)
    - _Requirements: 1.4, 10.1, 10.2, 10.3, 10.4_

- [x] 5. Add file upload support to ResourceController
  - [x] 5.1 Modify createResource endpoint to accept multipart/form-data
    - Change @PostMapping to accept MediaType.MULTIPART_FORM_DATA_VALUE
    - Add @RequestPart for optional MultipartFile parameter
    - Add @RequestPart for CreateResourceRequestDTO
    - _Requirements: 1.1, 1.3_
  
  - [x] 5.2 Add mutual exclusivity validation in createResource
    - Validate exactly one of url or file is provided using hasUrlOrFile
    - Return 400 Bad Request with descriptive message if validation fails
    - _Requirements: 8.1, 8.2, 8.3, 8.5_
  
  - [x] 5.3 Add required field validation
    - Ensure @Valid annotation is present on CreateResourceRequestDTO
    - Validation should check title, courseId, type are provided
    - _Requirements: 8.4, 8.5_

- [x] 6. Add file download endpoint to ResourceController
  - [x] 6.1 Create downloadFile endpoint
    - Add GET mapping at /{id}/download
    - Extract user ID and role from Authentication
    - Call ResourceService to get file Resource
    - Set Content-Disposition header with original filename
    - Set Content-Type header with stored MIME type
    - Return ResponseEntity with Resource body
    - _Requirements: 5.1, 5.4, 5.5_
  
  - [x] 6.2 Add access control validation in downloadFile
    - Verify user has permission (approved resource OR owner OR admin)
    - Return 403 Forbidden if unauthorized
    - _Requirements: 5.2, 5.3_
  
  - [x] 6.3 Add URL resource download rejection
    - Check if resource is file-based before download
    - Return 400 Bad Request if attempting to download URL resource
    - _Requirements: 5.6_

- [x] 7. Extend ResourceService with file resource creation
  - [x] 7.1 Modify createResource method signature
    - Add MultipartFile parameter (nullable)
    - _Requirements: 1.1, 1.3_
  
  - [x] 7.2 Add file upload logic to createResource
    - Check if file is provided
    - Call FileStorageService.storeFile if file present
    - Populate Resource entity with FileMetadata
    - Set isFileUpload to true for file resources
    - Maintain existing moderation workflow (PENDING status)
    - _Requirements: 1.2, 1.4, 1.5, 7.3, 7.4_
  
  - [x] 7.3 Maintain URL resource creation logic
    - Ensure URL resources still work (isFileUpload = false, url populated)
    - Apply same moderation workflow to both types
    - _Requirements: 7.1, 7.2, 7.4, 7.5_
  
  - [x] 7.4 Create getFileForDownload method
    - Verify resource exists
    - Check access permissions (approved OR owner OR admin)
    - Verify resource is file-based
    - Call FileStorageService.loadFileAsResource
    - Return Resource with original filename
    - _Requirements: 5.1, 5.2, 5.3, 5.6_

- [x] 8. Update delete logic to handle physical files
  - [x] 8.1 Modify deleteResource method
    - Check if resource is file-based using isFileResource()
    - Call FileStorageService.deleteFile if file resource
    - Continue with database deletion even if file deletion fails
    - Skip file operations for URL resources
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [x] 9. Add exception handling
  - [x] 9.1 Extend GlobalExceptionHandler
    - Add handler for InvalidFileTypeException (400 Bad Request)
    - Add handler for FileSizeLimitExceededException (400 Bad Request)
    - Add handler for FileStorageException (500 Internal Server Error)
    - Add handler for FileDownloadException (404 or 500 based on cause)
    - Add handler for MaxUploadSizeExceededException (400 Bad Request)
    - Each handler should return ErrorResponse with descriptive message
    - _Requirements: 2.3, 3.2, 8.5, 9.1, 9.4_

- [x] 10. Add configuration properties
  - [x] 10.1 Update application.properties
    - Add file.upload.upload-dir=./uploads/resources/
    - Add file.upload.max-file-size=10485760
    - Add file.upload.allowed-mime-types list
    - Add spring.servlet.multipart configuration
    - _Requirements: 2.2, 3.2, 4.3_

- [x] 11. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 12. Write unit tests
  - [ ]* 12.1 Write FileStorageService unit tests - MIME type validation
    - Test each allowed MIME type (PDF, PNG, JPEG, DOCX, TXT)
    - Test rejection of disallowed MIME types
    - Test content-based detection (mismatched extension)
    - _Requirements: 2.1, 2.2, 2.3, 2.4_
  
  - [ ]* 12.2 Write FileStorageService unit tests - file size validation
    - Test file at size limit (exactly 10MB)
    - Test file over size limit (10MB + 1 byte)
    - Test file under size limit
    - _Requirements: 3.1, 3.2_
  
  - [ ]* 12.3 Write FileStorageService unit tests - filename sanitization
    - Test path traversal patterns: ../, ..\, /etc/passwd, C:\Windows
    - Test absolute paths
    - Test normal filenames
    - _Requirements: 4.1_
  
  - [ ]* 12.4 Write FileStorageService unit tests - UUID filename generation
    - Test UUID format pattern
    - Test extension preservation
    - Test uniqueness across multiple calls
    - _Requirements: 4.2_
  
  - [ ]* 12.5 Write FileStorageService unit tests - path validation
    - Test paths with parent directory references
    - Test paths outside upload directory
    - Test valid paths within upload directory
    - _Requirements: 4.5_
  
  - [ ]* 12.6 Write FileStorageService unit tests - file operations
    - Test successful file storage
    - Test directory creation when missing
    - Test file loading for download
    - Test file deletion
    - Test I/O error handling
    - _Requirements: 9.1, 9.2, 9.3, 9.5_
  
  - [ ]* 12.7 Write ResourceService unit tests - file resource creation
    - Test file resource creation with valid file
    - Test URL resource creation (backward compatibility)
    - Test mutual exclusivity validation
    - Test moderation workflow applies to both types
    - _Requirements: 1.2, 7.1, 7.2, 7.3, 7.4, 8.1, 8.2, 8.3_
  
  - [ ]* 12.8 Write ResourceService unit tests - file deletion
    - Test physical file deletion for file resources
    - Test URL resource deletion skips file operations
    - Test graceful handling of file deletion failures
    - _Requirements: 6.1, 6.2, 6.3, 6.4_
  
  - [ ]* 12.9 Write ResourceService unit tests - download access control
    - Test approved resource access by any user
    - Test pending resource access by owner
    - Test pending resource access by admin
    - Test pending resource access denied for non-owner
    - _Requirements: 5.2, 5.3_
  
  - [ ]* 12.10 Write ResourceController unit tests - multipart requests
    - Test endpoint accepts multipart/form-data
    - Test endpoint accepts JSON (backward compatibility)
    - Test file parameter is optional
    - _Requirements: 1.1, 1.3_
  
  - [ ]* 12.11 Write ResourceController unit tests - download endpoint
    - Test download with correct Content-Disposition header
    - Test download with correct Content-Type header
    - Test 403 for unauthorized access
    - Test 400 for URL resource download attempt
    - _Requirements: 5.1, 5.4, 5.5, 5.6_
  
  - [ ]* 12.12 Write ResourceController unit tests - validation
    - Test required field validation errors
    - Test mutual exclusivity validation errors
    - Test error response format
    - _Requirements: 8.4, 8.5_

- [ ] 13. Write property-based tests
  - [ ]* 13.1 Write property test for Property 1: File resource entity completeness
    - **Property 1: File resource entity completeness**
    - **Validates: Requirements 1.2, 1.5**
    - Generate random valid files and verify all metadata fields are populated
  
  - [ ]* 13.2 Write property test for Property 3: Invalid MIME type rejection
    - **Property 3: Invalid MIME type rejection**
    - **Validates: Requirements 2.1, 2.3**
    - Generate random invalid MIME types and verify rejection
  
  - [ ]* 13.3 Write property test for Property 5: File size validation
    - **Property 5: File size validation**
    - **Validates: Requirements 3.1, 3.2**
    - Generate files of various sizes and verify size limit enforcement
  
  - [ ]* 13.4 Write property test for Property 6: Filename sanitization
    - **Property 6: Filename sanitization**
    - **Validates: Requirements 4.1**
    - Generate filenames with path traversal patterns and verify sanitization
  
  - [ ]* 13.5 Write property test for Property 7: UUID-based filename generation
    - **Property 7: UUID-based filename generation**
    - **Validates: Requirements 4.2**
    - Generate random filenames and verify UUID pattern in stored names
  
  - [ ]* 13.6 Write property test for Property 8: Path traversal prevention
    - **Property 8: Path traversal prevention**
    - **Validates: Requirements 4.5**
    - Generate various file paths and verify they resolve within upload directory
  
  - [ ]* 13.7 Write property test for Property 9: Download access control
    - **Property 9: Download access control**
    - **Validates: Requirements 5.2, 5.3**
    - Generate random resource/user combinations and verify access control logic
  
  - [ ]* 13.8 Write property test for Property 12: Physical file deletion
    - **Property 12: Physical file deletion on resource removal**
    - **Validates: Requirements 6.1, 6.2, 6.3**
    - Generate file resources and verify physical deletion on removal
  
  - [ ]* 13.9 Write property test for Property 14: Backward compatibility
    - **Property 14: Backward compatibility for URL resources**
    - **Validates: Requirements 7.1, 7.2**
    - Generate URL resource requests and verify correct entity creation
  
  - [ ]* 13.10 Write property test for Property 15: Resource type field population
    - **Property 15: Resource type field population**
    - **Validates: Requirements 7.3**
    - Generate both resource types and verify exactly one field set is populated
  
  - [ ]* 13.11 Write property test for Property 18: Mutual exclusivity validation
    - **Property 18: Mutual exclusivity validation**
    - **Validates: Requirements 8.1, 8.2, 8.3**
    - Generate requests with various url/file combinations and verify XOR enforcement

- [ ] 14. Write integration tests
  - [ ]* 14.1 Write integration test for complete file upload flow
    - Test multipart request from controller through to database and filesystem
    - Verify file exists on disk
    - Verify database record with correct metadata
    - Verify response DTO contains file metadata
    - _Requirements: 1.1, 1.2, 1.4, 1.5_
  
  - [ ]* 14.2 Write integration test for complete file download flow
    - Upload file, then download it
    - Verify correct headers (Content-Disposition, Content-Type)
    - Verify file content matches uploaded content
    - _Requirements: 5.1, 5.4, 5.5_
  
  - [ ]* 14.3 Write integration test for file deletion flow
    - Upload file, verify it exists, delete resource
    - Verify physical file is deleted
    - Verify database record is deleted
    - _Requirements: 6.1, 6.2_
  
  - [ ]* 14.4 Write integration test for access control
    - Upload file as user A
    - Attempt download as user B (should fail with 403)
    - Approve resource as admin
    - Attempt download as user B (should succeed)
    - _Requirements: 5.2, 5.3, 7.4_
  
  - [ ]* 14.5 Write integration test for validation errors
    - Test file too large (should return 400)
    - Test invalid MIME type (should return 400)
    - Test both url and file provided (should return 400)
    - Test neither url nor file provided (should return 400)
    - Verify error response format
    - _Requirements: 2.3, 3.2, 8.1, 8.2, 8.3, 8.5_
  
  - [ ]* 14.6 Write integration test for backward compatibility
    - Create URL resource using existing flow
    - Verify URL resource creation still works
    - Verify moderation workflow applies
    - List resources and verify both types appear correctly
    - _Requirements: 7.1, 7.2, 7.4, 7.5, 10.4_

- [ ] 15. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- The implementation uses Java with Spring Boot framework
- Property-based tests use jqwik library with minimum 100 iterations
- Unit tests and property tests are complementary - both provide value
- Integration tests verify end-to-end flows across all layers
- Checkpoints ensure incremental validation at key milestones
