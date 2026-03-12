# Design Document

## Overview

The Admin User Management System provides comprehensive tools for administrators to control user access, roles, and permissions. The system implements user state management (ACTIVE, BLOCKED, SUSPENDED), role assignment, and detailed activity tracking to maintain system security and user accountability.

## Business Requirements

### Functional Requirements

1. **List Users**: Display all users with filtering and search capabilities
2. **Block User**: Permanently prevent a user from accessing the system
3. **Suspend User**: Temporarily prevent access with automatic expiration
4. **Activate User**: Restore access to blocked or suspended users
5. **Change Role**: Modify user roles (STUDENT ↔ MODERATOR ↔ ADMIN)
6. **View User Activity**: See user's resource uploads, downloads, and actions

### Non-Functional Requirements

1. **Performance**: User list should load within 2 seconds for 10,000 users
2. **Security**: All actions must be logged in audit trail
3. **Usability**: Clear confirmation dialogs for destructive actions
4. **Accessibility**: WCAG 2.1 AA compliant interface

## Design

### User States

```
ACTIVE → User can access system normally
BLOCKED → User cannot login (permanent until unblocked)
SUSPENDED → User cannot login (temporary with expiration date)
```

### State Transitions

```
ACTIVE → BLOCKED (admin action)
ACTIVE → SUSPENDED (admin action with expiration)
BLOCKED → ACTIVE (admin action)
SUSPENDED → ACTIVE (admin action or automatic expiration)
```

### User Management Dashboard

#### User List Table
Columns:
- User ID
- Full Name
- Email
- Role (badge with color coding)
- Status (ACTIVE/BLOCKED/SUSPENDED badge)
- Registration Date
- Last Login
- Resources Uploaded
- Actions (dropdown menu)

#### Filters
- Search by name or email
- Filter by role (ALL, STUDENT, MODERATOR, ADMIN)
- Filter by status (ALL, ACTIVE, BLOCKED, SUSPENDED)
- Sort by: Name, Email, Registration Date, Last Login, Resources Count

## Backend Implementation

### Database Schema

#### User Entity Updates
```java
@Entity
@Table(name = "users")
public class User {
    // Existing fields...
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;
    
    @Column(name = "blocked_by")
    private Long blockedBy;
    
    @Column(name = "block_reason", length = 500)
    private String blockReason;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}
```

#### UserSuspension Entity (New)
```java
@Entity
@Table(name = "user_suspensions")
public class UserSuspension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "suspended_at", nullable = false)
    private LocalDateTime suspendedAt;
    
    @Column(name = "suspended_by", nullable = false)
    private Long suspendedBy;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "reason", length = 500, nullable = false)
    private String reason;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "lifted_at")
    private LocalDateTime liftedAt;
    
    @Column(name = "lifted_by")
    private Long liftedBy;
}
```

#### UserStatus Enum (New)
```java
public enum UserStatus {
    ACTIVE,
    BLOCKED,
    SUSPENDED
}
```

### API Endpoints

#### GET /api/admin/users
List all users with filtering and pagination.

**Query Parameters:**
- `search` (optional): Search by name or email
- `role` (optional): Filter by role
- `status` (optional): Filter by status
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size
- `sortBy` (optional, default: "createdAt"): Sort field
- `sortDirection` (optional, default: "desc"): Sort direction

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "fullName": "John Doe",
      "email": "john@edu.hac.ac.il",
      "role": "STUDENT",
      "status": "ACTIVE",
      "registrationDate": "2024-01-15T10:30:00",
      "lastLogin": "2024-03-10T14:20:00",
      "resourcesUploaded": 5,
      "emailVerified": true
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "currentPage": 0,
  "pageSize": 20
}
```

#### GET /api/admin/users/{id}
Get detailed information about a specific user.

**Response:**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@edu.hac.ac.il",
  "role": "STUDENT",
  "status": "ACTIVE",
  "registrationDate": "2024-01-15T10:30:00",
  "lastLogin": "2024-03-10T14:20:00",
  "emailVerified": true,
  "resourcesUploaded": 5,
  "resourcesApproved": 4,
  "resourcesPending": 1,
  "resourcesRejected": 0,
  "totalDownloads": 23,
  "lastActivity": "2024-03-10T14:20:00"
}
```

#### PUT /api/admin/users/{id}/block
Block a user permanently.

**Request Body:**
```json
{
  "reason": "Repeated policy violations"
}
```

**Response:** 200 OK

#### PUT /api/admin/users/{id}/suspend
Suspend a user temporarily.

**Request Body:**
```json
{
  "reason": "Inappropriate content upload",
  "expiresAt": "2024-03-18T10:00:00"
}
```

**Response:** 200 OK

#### PUT /api/admin/users/{id}/activate
Activate a blocked or suspended user.

**Response:** 200 OK

#### PUT /api/admin/users/{id}/role
Change a user's role.

**Request Body:**
```json
{
  "newRole": "MODERATOR",
  "reason": "Promoted for excellent contributions"
}
```

**Response:** 200 OK

#### GET /api/admin/users/{id}/activity
Get user activity history.

**Response:**
```json
{
  "userId": 1,
  "resourcesUploaded": [
    {
      "id": 10,
      "title": "Calculus Exam 2024",
      "type": "EXAM",
      "status": "APPROVED",
      "uploadedAt": "2024-03-01T10:00:00"
    }
  ],
  "recentDownloads": [
    {
      "resourceId": 25,
      "resourceTitle": "Data Structures Summary",
      "downloadedAt": "2024-03-10T14:20:00"
    }
  ],
  "totalUploads": 5,
  "totalDownloads": 23
}
```

### Service Layer

#### AdminUserService
```java
@Service
public class AdminUserService {
    
    private final UserRepository userRepository;
    private final UserSuspensionRepository suspensionRepository;
    private final AuditLogService auditLogService;
    
    public Page<AdminUserDTO> listUsers(UserFilterDTO filter, Pageable pageable);
    
    public AdminUserDetailDTO getUserDetails(Long userId);
    
    public void blockUser(Long userId, String reason, Long adminId);
    
    public void suspendUser(Long userId, String reason, LocalDateTime expiresAt, Long adminId);
    
    public void activateUser(Long userId, Long adminId);
    
    public void changeUserRole(Long userId, UserRole newRole, String reason, Long adminId);
    
    public UserActivityDTO getUserActivity(Long userId);
    
    @Scheduled(cron = "0 */5 * * * *")
    public void autoActivateExpiredSuspensions();
}
```

### Controller Layer

#### AdminUserController
```java
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    
    private final AdminUserService adminUserService;
    
    @GetMapping
    public ResponseEntity<Page<AdminUserDTO>> listUsers(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) UserRole role,
        @RequestParam(required = false) UserStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDirection
    );
    
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserDetailDTO> getUserDetails(@PathVariable Long id);
    
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(
        @PathVariable Long id,
        @Valid @RequestBody BlockUserRequestDTO request,
        Authentication authentication
    );
    
    @PutMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendUser(
        @PathVariable Long id,
        @Valid @RequestBody SuspendUserRequestDTO request,
        Authentication authentication
    );
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(
        @PathVariable Long id,
        Authentication authentication
    );
    
    @PutMapping("/{id}/role")
    public ResponseEntity<Void> changeUserRole(
        @PathVariable Long id,
        @Valid @RequestBody ChangeRoleRequestDTO request,
        Authentication authentication
    );
    
    @GetMapping("/{id}/activity")
    public ResponseEntity<UserActivityDTO> getUserActivity(@PathVariable Long id);
}
```

## Frontend Implementation

### Pages

#### UserManagementPage.tsx
Main page for user management with table, filters, and actions.

**Components:**
- UserTable: Displays users in a table format
- UserFilters: Search and filter controls
- UserDetailsModal: Shows detailed user information
- BlockUserModal: Confirmation dialog for blocking
- SuspendUserModal: Form for suspending with duration
- ChangeRoleModal: Form for changing user role

### Services

#### admin-user.service.ts
```typescript
export const adminUserService = {
  async listUsers(filters: UserFilters): Promise<PaginatedResponse<AdminUser>> {
    const params = new URLSearchParams();
    if (filters.search) params.append('search', filters.search);
    if (filters.role) params.append('role', filters.role);
    if (filters.status) params.append('status', filters.status);
    params.append('page', filters.page.toString());
    params.append('size', filters.size.toString());
    
    const response = await apiClient.get<PaginatedResponse<AdminUser>>(
      `/admin/users?${params.toString()}`
    );
    return response.data;
  },
  
  async getUserDetails(userId: number): Promise<AdminUserDetail> {
    const response = await apiClient.get<AdminUserDetail>(`/admin/users/${userId}`);
    return response.data;
  },
  
  async blockUser(userId: number, reason: string): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/block`, { reason });
  },
  
  async suspendUser(userId: number, reason: string, expiresAt: string): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/suspend`, { reason, expiresAt });
  },
  
  async activateUser(userId: number): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/activate`);
  },
  
  async changeUserRole(userId: number, newRole: UserRole, reason?: string): Promise<void> {
    await apiClient.put(`/admin/users/${userId}/role`, { newRole, reason });
  },
  
  async getUserActivity(userId: number): Promise<UserActivity> {
    const response = await apiClient.get<UserActivity>(`/admin/users/${userId}/activity`);
    return response.data;
  },
};
```

## Security Considerations

1. **Authorization**: Only ADMIN role can access user management
2. **Self-Protection**: Admins cannot block/suspend themselves or change their own role
3. **Session Invalidation**: When user is blocked/suspended, all active sessions must be invalidated
4. **Audit Trail**: All actions must be logged with admin ID, timestamp, and reason
5. **Input Validation**: Validate all inputs (reason length, expiration date, etc.)
6. **Rate Limiting**: Prevent abuse of user management endpoints

## Performance Considerations

1. **Indexing**: Add indexes on status, role, last_login for fast queries
2. **Pagination**: Always use pagination for user lists
3. **Caching**: Cache user statistics
4. **Scheduled Tasks**: Run suspension expiration checks every 5 minutes
