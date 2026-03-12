# Implementation Plan

## Backend Tasks

1. ✅ Create UserStatus enum
2. ✅ Update User entity with status fields (status, blockedAt, blockedBy, blockReason, lastLogin)
3. ✅ Create UserSuspension entity and repository
4. ✅ Create AdminUserService with all methods
5. ✅ Create Admin User DTOs (UserFilterDTO, AdminUserDTO, AdminUserDetailDTO, BlockUserRequestDTO, SuspendUserRequestDTO, ChangeRoleRequestDTO, UserActivityDTO)
6. ✅ Create AdminUserController with all endpoints
7. ✅ Add validation for user actions (can't block yourself, etc.)
8. ✅ Implement scheduled task for auto-activating expired suspensions
9. ⏸️ Add audit logging for all user management actions (blocked - requires admin-audit-log feature)
10. ✅ Update JwtAuthenticationFilter to check user status
11. ✅ Implement AdminUserService methods (listUsers, getUserDetails, getUserActivity)
12. ✅ Write unit tests for AdminUserService
13. ✅ Write integration tests for AdminUserController

## Frontend Tasks

1. ✅ Create UserManagementPage component
2. ✅ Create UserTable component with sorting and pagination
3. ✅ Create UserFilters component
4. ✅ Create UserDetailsModal component
5. ✅ Create BlockUserModal component
6. ✅ Create SuspendUserModal component
7. ✅ Create ChangeRoleModal component
8. ✅ Create admin-user.service.ts
9. ✅ Create useAdminUsers hook
10. ✅ Add user management route to router
11. ✅ Add "User Management" link to admin navigation
12. ✅ Add TypeScript types for user management
13. Write component tests

## Testing Checklist

- [ ] List users with no filters
- [ ] List users with search filter
- [ ] List users with role filter
- [ ] List users with status filter
- [ ] List users with pagination
- [ ] Get user details
- [ ] Block active user
- [ ] Cannot block already blocked user
- [ ] Cannot block yourself
- [ ] Suspend active user
- [ ] Cannot suspend already suspended user
- [ ] Activate blocked user
- [ ] Activate suspended user
- [ ] Change user role
- [ ] Cannot change your own role
- [ ] Auto-activate expired suspensions
- [ ] Blocked user cannot login
- [ ] Suspended user cannot login
- [ ] All actions create audit log entries
