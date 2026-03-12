# Admin System Documentation

This directory serves as an index to the admin system specifications.

## Admin System Specifications

All admin system specifications are now organized in `.kiro/specs/` following the standard spec structure:

### 1. Admin Moderation System
**Location:** `.kiro/specs/admin-moderation-system/`

Resource moderation system for reviewing, approving, rejecting, and managing user-submitted resources.

- [Requirements](../../specs/admin-moderation-system/requirements.md)
- [Design](../../specs/admin-moderation-system/design.md)
- [Tasks](../../specs/admin-moderation-system/tasks.md)

### 2. Admin User Management
**Location:** `.kiro/specs/admin-user-management/`

User management system for controlling user access, roles, and permissions (blocking, suspending, role changes).

- [Requirements](../../specs/admin-user-management/requirements.md)
- [Design](../../specs/admin-user-management/design.md)
- [Tasks](../../specs/admin-user-management/tasks.md)

### 3. Admin Dashboard
**Location:** `.kiro/specs/admin-dashboard/`

Comprehensive dashboard with system statistics, user activity, resource metrics, and trends.

- [Requirements](../../specs/admin-dashboard/requirements.md)
- [Design](../../specs/admin-dashboard/design.md)
- [Tasks](../../specs/admin-dashboard/tasks.md)

### 4. Admin Course Management
**Location:** `.kiro/specs/admin-course-management/`

Course management system for creating, editing, archiving, and deleting courses.

- [Requirements](../../specs/admin-course-management/requirements.md)
- [Design](../../specs/admin-course-management/design.md)
- [Tasks](../../specs/admin-course-management/tasks.md)

### 5. Admin System Tools
**Location:** `.kiro/specs/admin-system-tools/`

System maintenance tools for cleanup operations, database optimization, and health monitoring.

- [Requirements](../../specs/admin-system-tools/requirements.md)
- [Design](../../specs/admin-system-tools/design.md)
- [Tasks](../../specs/admin-system-tools/tasks.md)

### 6. Admin Audit Log
**Location:** `.kiro/specs/admin-audit-log/`

Audit logging system for tracking all administrative actions with immutable records.

- [Requirements](../../specs/admin-audit-log/requirements.md)
- [Design](../../specs/admin-audit-log/design.md)
- [Tasks](../../specs/admin-audit-log/tasks.md)

## Documentation Structure

Each spec folder contains three files:
- **requirements.md**: User stories and acceptance criteria
- **design.md**: Architecture, API endpoints, database schema, technical design
- **tasks.md**: Implementation checklist

This structure follows the established pattern used in other system specs (authentication-system, course-catalog-mvp, file-upload-system, resources-system).
