# Design Document

## Overview

The Admin Dashboard provides a comprehensive, data-rich interface for administrators to monitor system health, user engagement, and resource management. The dashboard uses charts, statistics cards, and activity feeds to present actionable insights at a glance.

## Business Requirements

### Functional Requirements

1. **System Overview**: Display key metrics at a glance
2. **User Statistics**: Track user growth and activity
3. **Resource Metrics**: Monitor resource uploads and approvals
4. **Trend Analysis**: Show trends over time with charts
5. **Quick Actions**: Provide shortcuts to common admin tasks
6. **Real-time Updates**: Update statistics in real-time or near real-time

### Non-Functional Requirements

1. **Performance**: Dashboard should load within 2 seconds
2. **Scalability**: Handle large datasets efficiently
3. **Responsiveness**: Work well on desktop and tablet devices
4. **Accessibility**: WCAG 2.1 AA compliant

## Design

### Dashboard Layout

#### Top Section: Key Metrics Cards
1. **Total Users**: Count with growth indicator
2. **Total Resources**: Count with growth indicator
3. **Pending Moderation**: Count with alert badge if > 10
4. **Active Users (30 days)**: Count with percentage of total

#### Middle Section: Charts
1. **Resources by Status** (Pie Chart): Approved, Pending, Rejected
2. **Uploads Over Time** (Line Chart): Last 30 days daily uploads
3. **User Registrations Over Time** (Area Chart): Last 90 days
4. **Resource Types Distribution** (Bar Chart): Count by type

#### Bottom Section: Tables
1. **Most Active Users**: Top 10 users by activity
2. **Recent Activity**: Timeline of recent admin actions

## Backend Implementation

### API Endpoints

#### GET /api/admin/dashboard/stats
Get comprehensive dashboard statistics.

**Query Parameters:**
- `dateRange` (optional): "7d", "30d", "90d", or custom "YYYY-MM-DD:YYYY-MM-DD"
- `courseId` (optional): Filter by specific course

**Response:**
```json
{
  "users": {
    "total": 1250,
    "active30d": 450,
    "new30d": 75,
    "growthRate": 6.4,
    "byRole": {
      "STUDENT": 1200,
      "MODERATOR": 45,
      "ADMIN": 5
    }
  },
  "resources": {
    "total": 3420,
    "approved": 3100,
    "pending": 280,
    "rejected": 40,
    "new30d": 320,
    "growthRate": 10.3,
    "byType": {
      "EXAM": 1200,
      "SUMMARY": 980,
      "LECTURE_NOTES": 750,
      "ASSIGNMENT": 320,
      "OTHER": 170
    }
  },
  "activity": {
    "uploadsOverTime": [
      { "date": "2024-03-01", "count": 12 },
      { "date": "2024-03-02", "count": 15 }
    ],
    "registrationsOverTime": [
      { "date": "2024-01-01", "count": 5 },
      { "date": "2024-01-02", "count": 8 }
    ]
  }
}
```

#### GET /api/admin/dashboard/top-users
Get most active users.

**Query Parameters:**
- `limit` (optional, default: 10): Number of users
- `sortBy` (optional, default: "uploads"): "uploads", "approved", "downloads"
- `dateRange` (optional): Filter by date range

**Response:**
```json
[
  {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@edu.hac.ac.il",
    "role": "STUDENT",
    "resourcesUploaded": 45,
    "resourcesApproved": 42,
    "totalDownloads": 230,
    "lastActive": "2024-03-10T14:20:00"
  }
]
```

#### GET /api/admin/dashboard/recent-activity
Get recent admin/moderation activity.

**Query Parameters:**
- `limit` (optional, default: 20): Number of activities
- `type` (optional): Filter by activity type

**Response:**
```json
[
  {
    "id": 1,
    "type": "RESOURCE_APPROVED",
    "timestamp": "2024-03-11T10:30:00",
    "moderatorName": "Admin User",
    "resourceId": 123,
    "resourceTitle": "Calculus Final Exam 2024",
    "details": "Approved resource"
  }
]
```

### Service Layer

#### AdminDashboardService
```java
@Service
public class AdminDashboardService {
    
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final AuditLogRepository auditLogRepository;
    
    public DashboardStatsDTO getDashboardStats(String dateRange, Long courseId);
    
    public List<TopUserDTO> getTopUsers(int limit, String sortBy, String dateRange);
    
    public List<RecentActivityDTO> getRecentActivity(int limit, String type);
    
    public ResourceStatsDTO getResourceStats(String dateRange, Long courseId);
    
    @Cacheable(value = "dashboardStats", key = "#dateRange + '_' + #courseId")
    public DashboardStatsDTO getCachedDashboardStats(String dateRange, Long courseId);
}
```

### Controller Layer

#### AdminDashboardController
```java
@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
public class AdminDashboardController {
    
    private final AdminDashboardService dashboardService;
    
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
        @RequestParam(defaultValue = "30d") String dateRange,
        @RequestParam(required = false) Long courseId
    );
    
    @GetMapping("/top-users")
    public ResponseEntity<List<TopUserDTO>> getTopUsers(
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "uploads") String sortBy,
        @RequestParam(required = false) String dateRange
    );
    
    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivityDTO>> getRecentActivity(
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(required = false) String type
    );
}
```

## Frontend Implementation

### Pages

#### DashboardPage.tsx
Main admin dashboard page.

**Components:**
- DashboardStats: Key metrics cards
- ResourceStatusChart: Pie chart of resource statuses
- UploadsOverTimeChart: Line chart of uploads
- ResourceTypesChart: Bar chart of resource types
- TopUsersTable: Table of most active users
- RecentActivityTimeline: Timeline of recent actions
- QuickActionsPanel: Shortcuts to common tasks

### Services

#### admin-dashboard.service.ts
```typescript
export const adminDashboardService = {
  async getDashboardStats(dateRange: string = '30d', courseId?: number): Promise<DashboardStats> {
    const params = new URLSearchParams();
    params.append('dateRange', dateRange);
    if (courseId) {
      params.append('courseId', courseId.toString());
    }
    
    const response = await apiClient.get<DashboardStats>(
      `/admin/dashboard/stats?${params.toString()}`
    );
    return response.data;
  },
  
  async getTopUsers(limit: number = 10, sortBy: string = 'uploads'): Promise<TopUser[]> {
    const params = new URLSearchParams();
    params.append('limit', limit.toString());
    params.append('sortBy', sortBy);
    
    const response = await apiClient.get<TopUser[]>(
      `/admin/dashboard/top-users?${params.toString()}`
    );
    return response.data;
  },
  
  async getRecentActivity(limit: number = 20): Promise<RecentActivity[]> {
    const params = new URLSearchParams();
    params.append('limit', limit.toString());
    
    const response = await apiClient.get<RecentActivity[]>(
      `/admin/dashboard/recent-activity?${params.toString()}`
    );
    return response.data;
  },
};
```

## Security Considerations

1. **Authorization**: Only ADMIN and MODERATOR can access dashboard
2. **Data Privacy**: Don't expose sensitive user information
3. **Rate Limiting**: Prevent abuse of dashboard endpoints
4. **Input Validation**: Validate date ranges and filter parameters

## Performance Considerations

1. **Caching**: Cache dashboard statistics for 5-10 minutes
2. **Database Indexing**: Add indexes on created_at, status, uploaded_by
3. **Query Optimization**: Use efficient aggregate queries
4. **Lazy Loading**: Load charts and tables on demand
5. **Pagination**: Paginate large tables
