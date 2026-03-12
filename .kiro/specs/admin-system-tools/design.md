# Design Document

## Overview

The Admin System Tools provide essential maintenance and cleanup operations to keep the system running efficiently. The system implements safe cleanup procedures with dry run modes, quarantine periods, and comprehensive audit logging.

## Business Requirements

### Functional Requirements

1. **Clean Rejected Resources**: Remove old rejected resources and their files
2. **Clean Orphan Files**: Remove files without database records
3. **Clean Test Data**: Remove test/demo data from production
4. **System Health Check**: Verify system integrity
5. **Database Maintenance**: Optimize database performance
6. **Storage Management**: Monitor and manage file storage

### Non-Functional Requirements

1. **Safety**: All operations should be reversible or have confirmation
2. **Performance**: Operations should not impact system availability
3. **Logging**: All operations must be logged
4. **Scheduling**: Support scheduled automatic cleanup

## Design

### System Tools Dashboard

#### Storage Overview Card
- Total storage used
- Storage by resource type
- Orphan files count
- Storage limit warning (if > 80%)

#### Cleanup Tools Card
- Clean Rejected Resources button
- Clean Orphan Files button
- Clean Test Data button
- Optimize Database button

#### System Health Card
- Database status
- File system status
- Last cleanup timestamp
- Last backup timestamp

#### Scheduled Tasks Card
- List of scheduled cleanup tasks
- Enable/disable toggles
- Last run and next run timestamps

## Backend Implementation

### API Endpoints

#### GET /api/admin/system/storage-stats
Get storage statistics.

**Response:**
```json
{
  "totalStorageUsed": 5368709120,
  "totalStorageUsedFormatted": "5.0 GB",
  "storageByType": {
    "EXAM": 2147483648,
    "SUMMARY": 1073741824,
    "LECTURE_NOTES": 1610612736
  },
  "orphanFilesCount": 5,
  "orphanFilesSize": 10485760,
  "totalFiles": 3420
}
```

#### POST /api/admin/system/clean-rejected
Clean old rejected resources.

**Request Body:**
```json
{
  "ageThresholdDays": 30,
  "dryRun": false,
  "deleteFiles": true
}
```

**Response:**
```json
{
  "resourcesDeleted": 12,
  "filesDeleted": 8,
  "storageFreed": 25165824,
  "storageFreedFormatted": "24 MB"
}
```

#### POST /api/admin/system/clean-orphans
Clean orphan files.

**Request Body:**
```json
{
  "dryRun": false
}
```

**Response:**
```json
{
  "orphanFilesFound": 5,
  "filesQuarantined": 5,
  "storageFreed": 10485760,
  "quarantineExpiresAt": "2024-03-18T10:00:00"
}
```

#### POST /api/admin/system/clean-test-data
Clean test data.

**Request Body:**
```json
{
  "criteria": {
    "emailDomains": ["@test.com"],
    "usernamePatterns": ["test_user_*"],
    "userIds": [999, 1000]
  },
  "dryRun": false
}
```

**Response:**
```json
{
  "usersDeleted": 10,
  "resourcesDeleted": 45,
  "filesDeleted": 30,
  "storageFreed": 52428800
}
```

#### POST /api/admin/system/optimize-database
Optimize database performance.

**Response:**
```json
{
  "operationsPerformed": [
    "Vacuum completed",
    "Indexes rebuilt",
    "Statistics updated"
  ],
  "duration": 12500
}
```

#### GET /api/admin/system/health
Perform system health check.

**Response:**
```json
{
  "overall": "HEALTHY",
  "checks": {
    "database": {
      "status": "HEALTHY",
      "message": "Database connection successful",
      "responseTime": 15
    },
    "fileSystem": {
      "status": "HEALTHY",
      "message": "Upload directory accessible",
      "availableSpace": 107374182400
    },
    "storage": {
      "status": "WARNING",
      "message": "Storage usage at 85%",
      "usedPercentage": 85
    }
  }
}
```

#### GET /api/admin/system/scheduled-tasks
Get scheduled cleanup tasks.

**Response:**
```json
[
  {
    "id": "clean-rejected-resources",
    "name": "Clean Rejected Resources",
    "description": "Remove rejected resources older than 30 days",
    "schedule": "0 0 2 * * *",
    "scheduleFormatted": "Daily at 2:00 AM",
    "enabled": true,
    "lastRun": "2024-03-11T02:00:00",
    "nextRun": "2024-03-12T02:00:00",
    "lastResult": "12 resources deleted, 24 MB freed"
  }
]
```

### Service Layer

#### SystemMaintenanceService
```java
@Service
public class SystemMaintenanceService {
    
    private final ResourceRepository resourceRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    
    public CleanupResultDTO cleanRejectedResources(int ageThresholdDays, boolean dryRun, boolean deleteFiles);
    
    public CleanupResultDTO cleanOrphanFiles(boolean dryRun);
    
    public CleanupResultDTO cleanTestData(TestDataCriteriaDTO criteria, boolean dryRun);
    
    public OptimizationResultDTO optimizeDatabase();
    
    public SystemHealthDTO performHealthCheck();
    
    public StorageStatsDTO getStorageStats();
    
    public List<ScheduledTaskDTO> getScheduledTasks();
    
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledCleanRejectedResources();
    
    @Scheduled(cron = "0 0 3 * * 0")
    public void scheduledCleanOrphanFiles();
}
```

### Controller Layer

#### AdminSystemController
```java
@RestController
@RequestMapping("/api/admin/system")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSystemController {
    
    private final SystemMaintenanceService maintenanceService;
    
    @GetMapping("/storage-stats")
    public ResponseEntity<StorageStatsDTO> getStorageStats();
    
    @PostMapping("/clean-rejected")
    public ResponseEntity<CleanupResultDTO> cleanRejectedResources(...);
    
    @PostMapping("/clean-orphans")
    public ResponseEntity<CleanupResultDTO> cleanOrphanFiles(...);
    
    @PostMapping("/clean-test-data")
    public ResponseEntity<CleanupResultDTO> cleanTestData(...);
    
    @PostMapping("/optimize-database")
    public ResponseEntity<OptimizationResultDTO> optimizeDatabase(...);
    
    @GetMapping("/health")
    public ResponseEntity<SystemHealthDTO> performHealthCheck();
    
    @GetMapping("/scheduled-tasks")
    public ResponseEntity<List<ScheduledTaskDTO>> getScheduledTasks();
}
```

## Frontend Implementation

### Pages

#### SystemToolsPage.tsx
Main page for system maintenance tools.

**Sections:**
1. Storage Overview
2. Cleanup Tools
3. System Health
4. Scheduled Tasks

### Services

#### admin-system.service.ts
```typescript
export const adminSystemService = {
  async getStorageStats(): Promise<StorageStats> {
    // Implementation
  },
  
  async cleanRejectedResources(
    ageThresholdDays: number,
    dryRun: boolean,
    deleteFiles: boolean
  ): Promise<CleanupResult> {
    // Implementation
  },
  
  async cleanOrphanFiles(dryRun: boolean): Promise<CleanupResult> {
    // Implementation
  },
  
  async optimizeDatabase(): Promise<OptimizationResult> {
    // Implementation
  },
  
  async performHealthCheck(): Promise<SystemHealth> {
    // Implementation
  },
};
```

## Security Considerations

1. **Authorization**: Only ADMIN role can access system tools
2. **Confirmation**: All destructive operations require confirmation
3. **Dry Run**: Always offer dry run option for preview
4. **Audit Trail**: Log all system maintenance operations
5. **Rate Limiting**: Prevent abuse of cleanup endpoints
6. **Quarantine**: Use quarantine period before permanent deletion

## Performance Considerations

1. **Async Processing**: Run cleanup operations asynchronously
2. **Progress Tracking**: Provide progress updates for long operations
3. **Batch Processing**: Process large datasets in batches
4. **Scheduling**: Run heavy operations during off-peak hours
5. **Timeout Handling**: Handle long-running operations gracefully
