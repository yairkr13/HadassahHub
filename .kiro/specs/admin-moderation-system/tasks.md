# Implementation Plan

## Backend Tasks

1. Add moderation fields to Resource entity (moderatedBy, moderatedAt, rejectionReason)
2. Create ModerationStatsDTO
3. Implement getPendingResources method in ResourceService
4. Implement approveResource method in ResourceService
5. Implement rejectResource method in ResourceService
6. Implement resetResourceStatus method in ResourceService
7. Implement deleteResource method with file deletion in ResourceService
8. Implement getModerationStats method in ResourceService
9. Create AdminResourceController with all moderation endpoints
10. Add audit logging for all moderation actions
11. Write unit tests for ResourceService moderation methods
12. Write integration tests for AdminResourceController

## Frontend Tasks

1. Create PendingResourcesPage component
2. Create ModerationActions component
3. Enhance ResourceCard component with moderation support
4. Create rejection modal component
5. Create moderation.service.ts
6. Create usePendingResources hook
7. Add moderation route to router
8. Add "Pending Resources" link to admin navigation
9. Add TypeScript types for moderation
10. Write component tests

## Testing Checklist

- [ ] Get pending resources
- [ ] Approve resource
- [ ] Reject resource with reason
- [ ] Reset resource status
- [ ] Delete resource
- [ ] Delete resource with file
- [ ] Get moderation statistics
- [ ] Visibility rules work correctly
- [ ] Moderators can moderate
- [ ] Students cannot access admin endpoints
- [ ] Rejection reason displayed to uploader
- [ ] Audit log entries created for all actions
