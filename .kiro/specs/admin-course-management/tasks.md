# Implementation Plan

## Backend Tasks

1. Add CourseStatus enum
2. Update Course entity with new fields (status, createdBy, updatedBy, createdAt, updatedAt)
3. Create course prerequisites join table
4. Create AdminCourseService with all methods
5. Create AdminCourseController with all endpoints
6. Add validation for course data (code format, credits range, etc.)
7. Implement course deletion check (prevent if resources exist)
8. Add audit logging for all course operations
9. Write unit tests for AdminCourseService
10. Write integration tests for AdminCourseController

## Frontend Tasks

1. Create CourseManagementPage component
2. Create CourseTable component
3. Create CourseFilters component
4. Create CourseFormModal component
5. Create CourseDetailsModal component
6. Create DeleteConfirmationModal component
7. Create admin-course.service.ts
8. Create useAdminCourses hook
9. Add course management route to router
10. Add "Course Management" link to admin navigation
11. Add TypeScript types
12. Write component tests

## Testing Checklist

- [ ] List courses with no filters
- [ ] List courses with search filter
- [ ] List courses with category filter
- [ ] List courses with pagination
- [ ] Get course details
- [ ] Create new course
- [ ] Cannot create course with duplicate code
- [ ] Update course
- [ ] Delete course without resources
- [ ] Cannot delete course with resources
- [ ] Archive course
- [ ] Activate archived course
- [ ] All actions create audit log entries
