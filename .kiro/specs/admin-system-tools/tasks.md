# Implementation Plan

## Backend Tasks

1. Create SystemMaintenanceService with all methods
2. Implement clean rejected resources logic
3. Implement clean orphan files logic with quarantine
4. Implement clean test data logic
5. Implement database optimization
6. Implement system health check
7. Create AdminSystemController with all endpoints
8. Add scheduled tasks with Spring @Scheduled
9. Add configuration for scheduled task cron expressions
10. Write unit tests for SystemMaintenanceService
11. Write integration tests for AdminSystemController

## Frontend Tasks

1. Create SystemToolsPage component
2. Create StorageOverview component
3. Create CleanupTools component
4. Create SystemHealthCheck component
5. Create ScheduledTasksPanel component
6. Create CleanRejectedModal component
7. Create CleanOrphansModal component
8. Create CleanTestDataModal component
9. Create admin-system.service.ts
10. Create useSystemTools hook
11. Add system tools route to router
12. Add "System Tools" link to admin navigation
13. Add TypeScript types
14. Write component tests

## Testing Checklist

- [ ] Get storage statistics
- [ ] Clean rejected resources (dry run)
- [ ] Clean rejected resources (actual)
- [ ] Clean orphan files (dry run)
- [ ] Clean orphan files (actual)
- [ ] Quarantine files correctly
- [ ] Clean test data (dry run)
- [ ] Clean test data (actual)
- [ ] Optimize database
- [ ] Perform health check
- [ ] Get scheduled tasks
- [ ] Update scheduled task
- [ ] Scheduled tasks run automatically
- [ ] All operations create audit log entries
