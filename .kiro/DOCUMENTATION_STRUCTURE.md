# Documentation Structure

This document describes the clean, organized documentation structure for the Hadassah Hub project.

## Specs Folder Structure

All feature specifications are located in `.kiro/specs/` with a consistent structure:

```
.kiro/specs/
├── authentication-system/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── course-catalog-mvp/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── file-upload-system/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── resources-system/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── admin-moderation-system/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── admin-user-management/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── admin-dashboard/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── admin-course-management/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
├── admin-system-tools/
│   ├── requirements.md
│   ├── design.md
│   └── tasks.md
└── admin-audit-log/
    ├── requirements.md
    ├── design.md
    └── tasks.md
```

## File Descriptions

### requirements.md
Contains user stories and acceptance criteria in the format:
- Introduction paragraph
- Requirement 1, 2, 3... with User Story and Acceptance Criteria
- Uses "WHEN... THEN the system SHALL..." format for acceptance criteria

### design.md
Contains technical architecture and design:
- Feature Overview
- Business Requirements (Functional + Non-Functional)
- Design section with architecture details
- Backend Implementation (Database Schema, API Endpoints, Service Layer, Controller Layer)
- Frontend Implementation (Pages, Components, Services)
- Security Considerations
- Performance Considerations

### tasks.md
Contains implementation checklist:
- Backend Tasks (numbered list)
- Frontend Tasks (numbered list)
- Testing Checklist (checkboxes)

## Admin System Specs

### 1. admin-moderation-system
Resource moderation with approval/rejection workflows. Allows admins and moderators to review, approve, reject, and manage user-submitted resources.

**Status:** Partially implemented (core features done)

### 2. admin-user-management
User access control, blocking, suspending, and role management. Allows admins to control user access and permissions.

**Status:** Not implemented (Phase 2)

### 3. admin-dashboard
System statistics, metrics, and activity monitoring. Provides comprehensive overview of system health and user engagement.

**Status:** Not implemented

### 4. admin-course-management
Course CRUD operations with archiving. Allows admins to create, edit, archive, and delete courses.

**Status:** Not implemented

### 5. admin-system-tools
Maintenance tools, cleanup operations, and health checks. Provides system maintenance and data integrity tools.

**Status:** Not implemented

### 6. admin-audit-log
Immutable audit trail for all admin actions. Tracks all administrative actions for accountability and compliance.

**Status:** Not implemented

## Documentation Principles

1. **Consistency**: All specs follow the same structure (requirements.md, design.md, tasks.md)
2. **Clarity**: Each file has a clear, focused purpose
3. **Minimal**: No extra files beyond the standard three
4. **Organized**: Logical folder structure with clear naming
5. **Maintainable**: Easy to find and update documentation

## Next Steps

When implementing a new feature:
1. Read the requirements.md to understand user stories
2. Review the design.md for technical architecture
3. Follow the tasks.md checklist for implementation
4. Update tasks.md to track progress

