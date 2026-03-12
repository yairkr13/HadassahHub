# Requirements Document

## Introduction

The File Upload System extends HadassahHub's existing resource sharing functionality to support direct file uploads in addition to URL-based resources. This feature enables students to upload study materials (PDFs, documents, images) directly to the platform while maintaining backward compatibility with existing URL resources and the moderation workflow.

## Glossary

- **File_Upload_System**: The subsystem responsible for handling file uploads, storage, validation, and retrieval
- **Resource_Controller**: The REST API controller that handles HTTP requests for resource operations
- **Resource_Service**: The service layer component that implements business logic for resource management
- **File_Storage_Service**: The service component responsible for physical file storage and retrieval operations
- **Resource_Entity**: The database entity representing an academic resource (URL or file-based)
- **Uploader**: A user with USER or ADMIN role who uploads resources
- **Moderator**: A user with ADMIN role who approves or rejects resources
- **Resource_Owner**: The user who originally uploaded a resource
- **File_Resource**: A resource that contains an uploaded file rather than an external URL
- **URL_Resource**: A resource that contains an external URL link
- **MIME_Type**: Multipurpose Internet Mail Extensions type that identifies file format
- **Path_Traversal**: A security vulnerability where file paths are manipulated to access unauthorized directories
- **Multipart_Form_Data**: HTTP content type for uploading files in form submissions

## Requirements

### Requirement 1: File Upload Support

**User Story:** As a student, I want to upload study materials as files, so that I can share resources that are not available as external links

#### Acceptance Criteria

1. WHEN an Uploader submits a file upload request with valid file and metadata, THE Resource_Controller SHALL accept multipart/form-data requests
2. WHEN an Uploader submits a file upload request, THE File_Upload_System SHALL create a Resource_Entity with file metadata
3. THE Resource_Controller SHALL support both file uploads and URL submissions through the same endpoint
4. WHEN a file upload is successful, THE File_Upload_System SHALL return a response containing the resource ID and file metadata
5. THE Resource_Entity SHALL store fileName, filePath, fileSize, mimeType, and isFileUpload fields for File_Resources

### Requirement 2: File Type Validation

**User Story:** As a system administrator, I want to restrict uploaded file types, so that only appropriate academic materials are stored

#### Acceptance Criteria

1. WHEN an Uploader submits a file, THE File_Storage_Service SHALL validate the MIME_Type
2. THE File_Storage_Service SHALL accept files with MIME_Type of application/pdf, image/png, image/jpeg, application/vnd.openxmlformats-officedocument.wordprocessingml.document, or text/plain
3. IF an Uploader submits a file with an unsupported MIME_Type, THEN THE File_Storage_Service SHALL reject the upload and return an error message
4. THE File_Storage_Service SHALL validate MIME_Type using file content inspection, not file extension alone

### Requirement 3: File Size Limits

**User Story:** As a system administrator, I want to limit file upload sizes, so that storage resources are managed efficiently

#### Acceptance Criteria

1. WHEN an Uploader submits a file, THE File_Storage_Service SHALL validate the file size
2. IF an Uploader submits a file larger than 10485760 bytes, THEN THE File_Storage_Service SHALL reject the upload and return an error message
3. WHEN a file size validation fails, THE File_Upload_System SHALL return an error response within 100 milliseconds

### Requirement 4: Secure File Storage

**User Story:** As a security administrator, I want uploaded files to be stored securely, so that the system is protected from file-based attacks

#### Acceptance Criteria

1. WHEN the File_Storage_Service stores a file, THE File_Storage_Service SHALL sanitize the original filename to remove Path_Traversal characters
2. WHEN the File_Storage_Service stores a file, THE File_Storage_Service SHALL generate a unique filename using UUID concatenated with the file extension
3. THE File_Storage_Service SHALL store files in the ./uploads/resources/ directory
4. THE File_Storage_Service SHALL store files outside the web application root directory
5. WHEN the File_Storage_Service generates a file path, THE File_Storage_Service SHALL validate that the path does not contain parent directory references

### Requirement 5: File Download

**User Story:** As a student, I want to download uploaded files, so that I can access study materials

#### Acceptance Criteria

1. THE Resource_Controller SHALL provide a GET endpoint at /api/resources/{id}/download for file downloads
2. WHEN a user requests a File_Resource download, THE Resource_Service SHALL verify the resource is approved or the user is the Resource_Owner or the user is a Moderator
3. IF a user requests a File_Resource download without proper access, THEN THE Resource_Controller SHALL return HTTP 403 Forbidden
4. WHEN a user downloads a File_Resource, THE Resource_Controller SHALL set the Content-Disposition header to attachment with the original filename
5. WHEN a user downloads a File_Resource, THE Resource_Controller SHALL set the Content-Type header to the stored MIME_Type
6. IF a user requests download for a URL_Resource, THEN THE Resource_Controller SHALL return HTTP 400 Bad Request

### Requirement 6: File Deletion

**User Story:** As a system administrator, I want uploaded files to be deleted when resources are removed, so that storage space is reclaimed

#### Acceptance Criteria

1. WHEN a Moderator deletes a File_Resource, THE Resource_Service SHALL delete the physical file from storage
2. WHEN a Resource_Owner deletes their own File_Resource, THE Resource_Service SHALL delete the physical file from storage
3. IF the physical file deletion fails, THEN THE Resource_Service SHALL log the error and continue with database deletion
4. WHEN a URL_Resource is deleted, THE Resource_Service SHALL not attempt file deletion operations

### Requirement 7: Backward Compatibility

**User Story:** As a developer, I want the file upload feature to maintain backward compatibility, so that existing URL resources continue to function

#### Acceptance Criteria

1. THE Resource_Controller SHALL continue to accept URL-based resource creation requests
2. WHEN an Uploader submits a URL resource, THE Resource_Service SHALL create a Resource_Entity with url field populated and isFileUpload set to false
3. WHEN an Uploader submits a File_Resource, THE Resource_Service SHALL create a Resource_Entity with file fields populated and isFileUpload set to true
4. THE Resource_Service SHALL apply the existing moderation workflow to both File_Resources and URL_Resources
5. WHEN retrieving resources, THE Resource_Service SHALL return appropriate metadata for both File_Resources and URL_Resources

### Requirement 8: Request Validation

**User Story:** As a developer, I want upload requests to be validated, so that invalid data is rejected early

#### Acceptance Criteria

1. WHEN an Uploader submits a file upload request, THE Resource_Controller SHALL validate that either url or file is provided
2. IF an Uploader submits a request with both url and file, THEN THE Resource_Controller SHALL reject the request and return HTTP 400 Bad Request
3. IF an Uploader submits a request with neither url nor file, THEN THE Resource_Controller SHALL reject the request and return HTTP 400 Bad Request
4. WHEN an Uploader submits a file upload request, THE Resource_Controller SHALL validate that title, courseId, and type fields are provided
5. WHEN validation fails, THE Resource_Controller SHALL return an error response with descriptive error messages

### Requirement 9: File Storage Error Handling

**User Story:** As a system administrator, I want file storage errors to be handled gracefully, so that users receive meaningful error messages

#### Acceptance Criteria

1. IF the File_Storage_Service encounters an I/O error during file write, THEN THE File_Storage_Service SHALL throw a storage exception
2. IF the storage directory does not exist, THEN THE File_Storage_Service SHALL create the directory structure
3. IF directory creation fails, THEN THE File_Storage_Service SHALL throw a storage exception with a descriptive message
4. WHEN a storage exception occurs, THE Resource_Controller SHALL return HTTP 500 Internal Server Error with an error message
5. WHEN a file operation fails, THE File_Storage_Service SHALL log the error with file details and exception information

### Requirement 10: Metadata Exposure

**User Story:** As a frontend developer, I want file metadata in API responses, so that I can display file information to users

#### Acceptance Criteria

1. WHEN the Resource_Controller returns a File_Resource, THE Resource_Controller SHALL include fileName, fileSize, mimeType, and isFileUpload in the response DTO
2. WHEN the Resource_Controller returns a URL_Resource, THE Resource_Controller SHALL include url and isFileUpload set to false in the response DTO
3. THE Resource_Controller SHALL not expose internal file paths in API responses
4. WHEN listing resources, THE Resource_Controller SHALL include file metadata for File_Resources in the list response
