# Design Document

## Overview

The Admin Moderation System provides a comprehensive interface for administrators and moderators to review, approve, reject, and manage user-submitted resources. The system implements a queue-based workflow with status tracking, audit logging, and visibility rules to ensure quality control while maintaining transparency with uploaders.

## Business Requirements

### Functional Requirements

1. **Queue Management**: Efficiently manage pending resources queue
2. **Quality Control**: Ensure only appropriate resources are approved
3. **Feedback Loop**: Provide clear rejection reasons to uploaders
4. **Audit Trail**: Track all moderation actions
5. **Performance**: Handle large volumes of resources efficiently

### Non-Functional Requirements

1. **Response Time**: Moderation actions should complete within 1 second
2. **Scalability**: Support 1000+ pending resources
3. **Usability**: Clear, intuitive moderation interface
4. **Accessibility**: WCAG 2.1 AA compliant

## Design

### Resource Status Flow

```
PENDING → APPROVED (moderator approves)
PENDING → REJECTED (moderator rejects with reason)
REJECTED → PENDING (moderator resets)
APPROVED → REJECTED (moderator rejects with reason)
ANY → DELETED (moderator deletes permanently)
```

### Moderation Dashboard

#### Statistics Cards
- **Pending Resources**: Count of resources awaiting moderation
- **Approved Resources**: Total approved resources
- **Rejected Resources**: Total rejected resources
- **Total Resources**: All resources in system

#### Pending Resources List
Displays all pending resources with:
- Resource title
- Type badge (EXAM, SUMMARY, LECTURE_NOTES, etc.)
- Course name
- Uploader name
- Upload date
- Academic year
- File size (if file upload)
- Action buttons (Approve, Reject)

### Moderation Actions

#### Approve Resource
1. Moderator clicks "Approve" button
2. Confirmation dialog appears (optional)
3. On confirm:
   - Resource status set to APPROVED
   - Moderator ID and timestamp recorded
   - Resource becomes visible to all users
   - Audit log entry created
   - Success notification shown

#### Reject Resource
1. Moderator clicks "Reject" button
2. Rejection dialog appears:
   - Reason field (required, max 500 chars)
   - Common reasons dropdown (optional quick select)
   - Cancel / Confirm buttons
3. On confirm:
   - Resource status set to REJECTED
   - Rejection reason, moderator ID, and timestamp recorded
   - Resource visible only to uploader and moderators
   - Audit log entry created
   - Success notification shown

#### Reset Resource
1. Moderator views rejected resource
2. Clicks "Reset to Pending" button
3. Confirmation dialog appears
4. On confirm:
   - Resource status set back to PENDING
   - Previous rejection reason preserved in history
   - Resource back in moderation queue
   - Audit log entry created
   - Success notification shown

#### Delete Resource
1. Moderator clicks "Delete" button
2. Confirmation dialog appears with warning
3. On confirm:
   - Resource deleted from database
   - Associated file deleted from storage (if file upload)
   - Audit log entry created
   - Success notification shown

### Common Rejection Reasons

Predefined reasons for quick selection:
- "Inappropriate content"
- "Low quality or incomplete"
- "Duplicate resource"
- "Wrong course or category"
- "Copyright violation"
- "Spam or irrelevant content"
- "File corrupted or unreadable"

## Backend Implementation

### Database Schema

#### Resource Entity (Existing)
```java
@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.PENDING;
    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    
    @Column(name = "moderated_by")
    private Long moderatedBy;
    
    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;
    
    // Additional fields...
}
```

### API Endpoints

#### GET /api/admin/resources/pending
Get all pending resources.

**Response:**
```json
[
  {
    "id": 1,
    "title": "Calculus Final Exam 2024",
    "type": "EXAM",
    "status": "PENDING",
    "courseName": "Calculus I",
    "uploaderName": "John Doe",
    "uploadedAt": "2024-03-10T10:00:00",
    "academicYear": "2023-2024",
    "isFileUpload": true,
    "fileName": "calculus_exam_2024.pdf",
    "fileSize": 2048576
  }
]
```

#### PUT /api/admin/resources/{id}/approve
Approve a resource.

**Response:** 200 OK

#### PUT /api/admin/resources/{id}/reject
Reject a resource with a reason.

**Request Body:**
```json
{
  "reason": "Low quality or incomplete"
}
```

**Response:** 200 OK

#### PUT /api/admin/resources/{id}/reset
Reset a rejected resource back to pending.

**Response:** 200 OK

#### DELETE /api/admin/resources/{id}
Delete a resource permanently.

**Response:** 204 No Content

#### GET /api/admin/resources/stats
Get moderation statistics.

**Response:**
```json
{
  "pendingCount": 15,
  "approvedCount": 234,
  "rejectedCount": 12,
  "totalCount": 261
}
```

### Service Layer

#### ResourceService
```java
@Service
public class ResourceService {
    
    public List<ResourceDTO> getPendingResources();
    
    public void approveResource(Long resourceId, Long moderatorId);
    
    public void rejectResource(Long resourceId, String reason, Long moderatorId);
    
    public void resetResourceStatus(Long resourceId, Long moderatorId);
    
    public void deleteResource(Long resourceId, Long moderatorId);
    
    public ModerationStatsDTO getModerationStats();
}
```

### Controller Layer

#### AdminResourceController
```java
@RestController
@RequestMapping("/api/admin/resources")
@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
public class AdminResourceController {
    
    private final ResourceService resourceService;
    
    @GetMapping("/pending")
    public ResponseEntity<List<ResourceDTO>> getPendingResources();
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveResource(
        @PathVariable Long id,
        Authentication authentication
    );
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectResource(
        @PathVariable Long id,
        @Valid @RequestBody RejectResourceRequestDTO request,
        Authentication authentication
    );
    
    @PutMapping("/{id}/reset")
    public ResponseEntity<Void> resetResourceStatus(
        @PathVariable Long id,
        Authentication authentication
    );
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
        @PathVariable Long id,
        Authentication authentication
    );
    
    @GetMapping("/stats")
    public ResponseEntity<ModerationStatsDTO> getModerationStats();
}
```

## Frontend Implementation

### Pages

#### PendingResourcesPage.tsx
Main page for resource moderation with statistics and pending resources list.

**Components:**
- Statistics cards showing pending, approved, and rejected counts
- Pending resources list with ResourceCard components
- ModerationActions component for approve/reject buttons
- Rejection modal for entering rejection reason

### Components

#### ModerationActions.tsx
Component with approve and reject buttons.

**Features:**
- Approve button with confirmation
- Reject button that opens rejection modal
- Loading states during API calls
- Error handling

#### ResourceCard.tsx (Enhanced)
Resource card component with moderation support.

**Features:**
- Status badge (PENDING, APPROVED, REJECTED)
- Owner feedback message (shows rejection reason to uploader)
- Moderation actions (if user is admin/moderator)
- File download button (if file upload)
- Resource details display

### Services

#### moderation.service.ts
```typescript
export const moderationService = {
  async getPendingResources(): Promise<Resource[]> {
    const response = await apiClient.get<Resource[]>('/admin/resources/pending');
    return response.data;
  },
  
  async approveResource(resourceId: number): Promise<void> {
    await apiClient.put(`/admin/resources/${resourceId}/approve`);
  },
  
  async rejectResource(resourceId: number, reason: string): Promise<void> {
    await apiClient.put(`/admin/resources/${resourceId}/reject`, { reason });
  },
  
  async resetResource(resourceId: number): Promise<void> {
    await apiClient.put(`/admin/resources/${resourceId}/reset`);
  },
  
  async deleteResource(resourceId: number): Promise<void> {
    await apiClient.delete(`/admin/resources/${resourceId}`);
  },
  
  async getModerationStats(): Promise<ModerationStats> {
    const response = await apiClient.get<ModerationStats>('/admin/resources/stats');
    return response.data;
  },
};
```

## Security Considerations

1. **Authorization**: Only ADMIN and MODERATOR roles can access moderation endpoints
2. **Audit Trail**: All moderation actions must be logged
3. **File Deletion**: Ensure files are securely deleted when resource is deleted
4. **Input Validation**: Validate rejection reasons, filter parameters, etc.
5. **Rate Limiting**: Prevent abuse of moderation endpoints

## Performance Considerations

1. **Pagination**: Implement pagination for large pending queues
2. **Caching**: Cache moderation statistics
3. **Indexing**: Add database indexes on status, moderatedAt, uploadedAt
4. **Async Processing**: Use async processing for bulk operations
