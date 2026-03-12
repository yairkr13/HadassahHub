# Design Document

## Overview

The Admin Audit Log System provides comprehensive tracking of all administrative actions with immutable records. The system captures who performed each action, when it occurred, what changed, and from where, ensuring complete accountability and compliance support.

## Business Requirements

### Functional Requirements

1. **Action Tracking**: Record all admin actions automatically
2. **View Audit Log**: Display audit log with filtering and search
3. **Export Audit Log**: Export logs for compliance or analysis
4. **Retention Policy**: Automatically archive old logs
5. **Search and Filter**: Find specific actions quickly

### Non-Functional Requirements

1. **Immutability**: Audit logs cannot be modified or deleted
2. **Performance**: Log writes should not impact system performance
3. **Storage**: Efficient storage for large volumes of logs
4. **Compliance**: Meet audit requirements for educational institutions

## Design

### Audit Log Dashboard

#### Audit Log Table
Columns:
- Timestamp
- Action Type (badge with color)
- Admin/Moderator Name
- Target Entity (User, Resource, Course)
- Target ID
- Details/Changes
- IP Address
- Actions (View Details)

#### Filters
- Date range picker
- Action type filter
- Admin/Moderator filter
- Entity type filter
- Search by target ID or details

## Backend Implementation

### Database Schema

#### AuditLog Entity
```java
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_action_type", columnList = "action_type"),
    @Index(name = "idx_audit_admin_id", columnList = "admin_id")
})
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private AuditActionType actionType;
    
    @Column(name = "admin_id", nullable = false)
    private Long adminId;
    
    @Column(name = "admin_name", nullable = false)
    private String adminName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Column(name = "before_state", columnDefinition = "TEXT")
    private String beforeState;
    
    @Column(name = "after_state", columnDefinition = "TEXT")
    private String afterState;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    // Getters only (immutable)
}
```


### API Endpoints

#### GET /api/admin/audit-logs
Get audit logs with filtering and pagination.

**Query Parameters:**
- `startDate`, `endDate`, `actionType`, `adminId`, `entityType`, `entityId`, `search`, `page`, `size`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "timestamp": "2024-03-11T10:30:00",
      "actionType": "RESOURCE_APPROVED",
      "adminName": "Admin User",
      "entityType": "RESOURCE",
      "entityId": 123,
      "entityName": "Calculus Final Exam 2024",
      "changesSummary": "Approved resource",
      "ipAddress": "192.168.1.100"
    }
  ],
  "totalElements": 1250,
  "totalPages": 25
}
```

#### GET /api/admin/audit-logs/{id}
Get detailed information about a specific audit log entry.

#### GET /api/admin/audit-logs/export
Export audit logs to CSV or JSON.

#### GET /api/admin/audit-logs/stats
Get audit log statistics.

### Service Layer

#### AuditLogService
```java
@Service
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    public void logUserAction(AuditActionType actionType, Long adminId, User user, 
                             Object beforeState, Object afterState, String summary);
    
    public void logResourceAction(AuditActionType actionType, Long adminId, Resource resource,
                                 Object beforeState, Object afterState, String summary);
    
    public void logCourseAction(AuditActionType actionType, Long adminId, Course course,
                               Object beforeState, Object afterState, String summary);
    
    public void logSystemAction(AuditActionType actionType, Long adminId, String summary,
                               Map<String, Object> context);
    
    public Page<AuditLogDTO> getAuditLogs(AuditLogFilterDTO filter, Pageable pageable);
    
    public AuditLogDetailDTO getAuditLogDetails(Long id);
    
    public byte[] exportAuditLogs(String format, AuditLogFilterDTO filter);
    
    @Scheduled(cron = "0 0 1 1 * *")
    public void archiveOldLogs();
}
```

### Controller Layer

#### AdminAuditLogController
```java
@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditLogController {
    
    private final AuditLogService auditLogService;
    
    @GetMapping
    public ResponseEntity<Page<AuditLogDTO>> getAuditLogs(...);
    
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDetailDTO> getAuditLogDetails(@PathVariable Long id);
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAuditLogs(...);
    
    @GetMapping("/stats")
    public ResponseEntity<AuditLogStatsDTO> getAuditLogStats(...);
}
```

## Frontend Implementation

### Pages

#### AuditLogPage.tsx
Main page for viewing audit logs.

**Components:**
- AuditLogTable: Displays audit logs
- AuditLogFilters: Date range, action type, admin filters
- AuditLogStats: Statistics cards
- AuditLogDetailsModal: Full details of an entry
- ExportButton: Export logs to CSV/JSON

### Services

#### admin-audit-log.service.ts
```typescript
export const adminAuditLogService = {
  async getAuditLogs(filters: AuditLogFilters): Promise<PaginatedResponse<AuditLog>> {
    // Implementation
  },
  
  async getAuditLogDetails(id: number): Promise<AuditLogDetail> {
    // Implementation
  },
  
  async exportAuditLogs(format: string, filters: AuditLogFilters): Promise<void> {
    // Implementation
  },
  
  async getAuditLogStats(startDate?: string, endDate?: string): Promise<AuditLogStats> {
    // Implementation
  },
};
```

## Security Considerations

1. **Immutability**: Audit logs cannot be modified or deleted
2. **Authorization**: Only ADMIN role can view audit logs
3. **Data Privacy**: Mask sensitive information in logs
4. **Retention**: Implement retention policy for compliance
5. **Access Control**: Log access to audit logs themselves

## Performance Considerations

1. **Indexing**: Add indexes on timestamp, action_type, admin_id, entity_type
2. **Archiving**: Archive old logs to separate table/storage
3. **Pagination**: Always use pagination for large result sets
4. **Async Logging**: Log actions asynchronously to avoid performance impact
5. **Caching**: Cache statistics for performance
