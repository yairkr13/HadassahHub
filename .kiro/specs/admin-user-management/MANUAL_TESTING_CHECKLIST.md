# Admin User Management - Manual Testing Checklist

## Test Environment Setup

### Required Test Users

You'll need at least 3 test users with different roles:

1. **Admin User** (for testing admin features)
   - Email: `admin@edu.hac.ac.il`
   - Role: ADMIN
   - Status: ACTIVE

2. **Regular Student User** (for testing access restrictions)
   - Email: `student@edu.hac.ac.il`
   - Role: STUDENT
   - Status: ACTIVE

3. **Target Test User** (for performing actions on)
   - Email: `testuser@edu.hac.ac.il`
   - Role: STUDENT
   - Status: ACTIVE

### Pre-Test Checklist
- [ ] Backend server is running
- [ ] Frontend development server is running
- [ ] Database has test users with different roles and statuses
- [ ] You have login credentials for ADMIN and STUDENT users

---

## 1. Page Access & Authorization

### Test 1.1: Admin User Access
**Steps:**
1. Login as ADMIN user
2. Navigate to the navigation bar
3. Look for "User Management" link (should be visible)
4. Click "User Management" link
5. Verify URL changes to `/admin/users`
6. Verify page loads successfully

**Expected Results:**
- [ ] "User Management" link is visible in navigation
- [ ] Page loads without errors
- [ ] User list is displayed
- [ ] Filters are visible
- [ ] No console errors

### Test 1.2: Non-Admin User Access Restriction
**Steps:**
1. Logout from admin account
2. Login as STUDENT user
3. Check navigation bar

**Expected Results:**
- [ ] "User Management" link is NOT visible
- [ ] No admin navigation options visible

### Test 1.3: Direct URL Access Protection
**Steps:**
1. While logged in as STUDENT user
2. Manually navigate to `/admin/users` in browser address bar
3. Press Enter

**Expected Results:**
- [ ] Access is denied (403 Forbidden or redirect)
- [ ] User is redirected away from the page
- [ ] Appropriate error message is shown

### Test 1.4: Unauthenticated Access
**Steps:**
1. Logout completely
2. Manually navigate to `/admin/users`

**Expected Results:**
- [ ] Redirected to login page
- [ ] After login as ADMIN, can access the page

---

## 2. User Listing & Display

### Test 2.1: Initial Page Load
**Steps:**
1. Login as ADMIN
2. Navigate to `/admin/users`
3. Wait for page to load

**Expected Results:**
- [ ] Loading spinner appears briefly
- [ ] User list loads successfully
- [ ] Table displays with correct columns:
  - User (name + email)
  - Role
  - Status
  - Registration Date
  - Last Login
  - Resources
  - Actions
- [ ] At least 4 users are visible (admin, student, moderator, test users)
- [ ] Data is formatted correctly (dates, badges, etc.)

### Test 2.2: Pagination
**Steps:**
1. Check if pagination controls appear at bottom
2. Note the total number of users
3. If more than 20 users exist:
   - Click "Next" button
   - Verify page 2 loads
   - Click "Previous" button
   - Click specific page numbers

**Expected Results:**
- [ ] Pagination shows correct total count
- [ ] "Showing X to Y of Z results" is accurate
- [ ] Page navigation works smoothly
- [ ] URL updates with page parameter (optional)
- [ ] Previous/Next buttons disable appropriately

### Test 2.3: Empty State
**Steps:**
1. Apply filters that return no results (e.g., search for "nonexistentuser")

**Expected Results:**
- [ ] "No users found" message appears
- [ ] No table rows displayed
- [ ] Filters remain visible
- [ ] Can clear filters to see users again

### Test 2.4: Role Badges
**Steps:**
1. Verify role badges for different users

**Expected Results:**
- [ ] ADMIN role: Purple badge
- [ ] MODERATOR role: Blue badge
- [ ] STUDENT role: Gray badge
- [ ] Badges are clearly visible and readable

### Test 2.5: Status Badges
**Steps:**
1. Verify status badges for different users

**Expected Results:**
- [ ] ACTIVE status: Green badge
- [ ] BLOCKED status: Red badge (if any blocked users exist)
- [ ] SUSPENDED status: Yellow badge (if any suspended users exist)

---

## 3. Filters & Search

### Test 3.1: Search by Name
**Steps:**
1. In the search box, type part of a user's name (e.g., "Admin")
2. Wait for results to update

**Expected Results:**
- [ ] Results filter immediately or after typing stops
- [ ] Only users matching the search term are shown
- [ ] Search is case-insensitive
- [ ] Pagination resets to page 0

### Test 3.2: Search by Email
**Steps:**
1. Clear previous search
2. Type part of an email address (e.g., "student@")
3. Wait for results

**Expected Results:**
- [ ] Users with matching email are shown
- [ ] Search works for partial email matches
- [ ] Results update correctly

### Test 3.3: Filter by Role
**Steps:**
1. Clear search box
2. Select "Student" from Role dropdown
3. Verify results
4. Change to "Admin"
5. Change to "Moderator"
6. Change back to "All Roles"

**Expected Results:**
- [ ] Only users with selected role are shown
- [ ] Changing role updates results immediately
- [ ] "All Roles" shows all users again
- [ ] Pagination resets when filter changes

### Test 3.4: Filter by Status
**Steps:**
1. Select "Active" from Status dropdown
2. Verify results
3. Change to "Blocked" (if blocked users exist)
4. Change to "Suspended" (if suspended users exist)
5. Change back to "All Statuses"

**Expected Results:**
- [ ] Only users with selected status are shown
- [ ] Filter updates results immediately
- [ ] "All Statuses" shows all users

### Test 3.5: Combined Filters
**Steps:**
1. Enter search term: "test"
2. Select Role: "Student"
3. Select Status: "Active"
4. Verify results match ALL criteria

**Expected Results:**
- [ ] Results match all filter criteria simultaneously
- [ ] Correct count of filtered results
- [ ] Filters work together properly

### Test 3.6: Clear Filters
**Steps:**
1. Apply multiple filters (search + role + status)
2. Click "Clear Filters" button

**Expected Results:**
- [ ] "Clear Filters" button appears when filters are active
- [ ] All filters reset to default
- [ ] Full user list is shown again
- [ ] Button disappears when no filters active

---

## 4. User Actions

### Test 4.1: View User Details
**Steps:**
1. Find a test user in the list
2. Click "View" button
3. Examine the modal

**Expected Results:**
- [ ] Modal opens with user details
- [ ] Basic Information section shows:
  - Full Name
  - Email
  - Role (with badge)
  - Status (with badge)
  - Registration Date
  - Last Login
- [ ] Resource Statistics section shows:
  - Total Uploaded
  - Approved
  - Pending
  - Rejected
- [ ] Activity section shows:
  - Total Downloads
  - Last Activity
- [ ] "Close" button works
- [ ] Clicking outside modal closes it (optional)
- [ ] No console errors

### Test 4.2: Block User
**Steps:**
1. Find an ACTIVE test user (NOT yourself)
2. Click Actions dropdown
3. Select "Block User"
4. Verify modal opens
5. Read the warning message
6. Try to submit without reason
7. Enter reason: "Test blocking - policy violation"
8. Click "Block User" button

**Expected Results:**
- [ ] Block modal opens
- [ ] Warning message is displayed (red background)
- [ ] User info is shown correctly
- [ ] Cannot submit without reason (validation error)
- [ ] After submitting:
  - [ ] Success message appears
  - [ ] Modal closes
  - [ ] User list refreshes
  - [ ] User status changes to BLOCKED
  - [ ] User status badge is now red
- [ ] No console errors

### Test 4.3: Suspend User
**Steps:**
1. Find another ACTIVE test user
2. Click Actions → "Suspend User"
3. Verify modal opens
4. Check default expiration date (should be 7 days from now)
5. Try to submit without reason
6. Enter reason: "Test suspension - inappropriate content"
7. Change expiration date to 3 days from now
8. Click "Suspend User"

**Expected Results:**
- [ ] Suspend modal opens
- [ ] Info message is displayed (yellow background)
- [ ] Default expiration is 7 days in future
- [ ] Cannot submit without reason
- [ ] Cannot set past expiration date
- [ ] After submitting:
  - [ ] Success message appears
  - [ ] Modal closes
  - [ ] User list refreshes
  - [ ] User status changes to SUSPENDED
  - [ ] User status badge is now yellow
- [ ] No console errors

### Test 4.4: Activate User
**Steps:**
1. Find the BLOCKED user from Test 4.2
2. Click Actions → "Activate User"
3. Confirm action (no modal, direct action)

**Expected Results:**
- [ ] Success message appears immediately
- [ ] User list refreshes
- [ ] User status changes to ACTIVE
- [ ] User status badge is now green
- [ ] No console errors

**Repeat for SUSPENDED user:**
1. Find the SUSPENDED user from Test 4.3
2. Click Actions → "Activate User"

**Expected Results:**
- [ ] Same as above
- [ ] Suspension is lifted in database

### Test 4.5: Change User Role
**Steps:**
1. Find a STUDENT test user
2. Click Actions → "Change Role"
3. Verify modal opens
4. Check current role is displayed
5. Try to select the same role (should show error)
6. Select "Moderator" from dropdown
7. Enter optional reason: "Promotion for good contributions"
8. Read role permissions info
9. Click "Change Role"

**Expected Results:**
- [ ] Change Role modal opens
- [ ] Current role is displayed
- [ ] Cannot select same role (validation)
- [ ] Role permissions are explained
- [ ] After submitting:
  - [ ] Success message appears
  - [ ] Modal closes
  - [ ] User list refreshes
  - [ ] User role changes to MODERATOR
  - [ ] Role badge updates to blue
- [ ] No console errors

---

## 5. Validation & Error Cases

### Test 5.1: Cannot Block Yourself
**Steps:**
1. Find YOUR admin user in the list
2. Click Actions dropdown
3. Check available actions

**Expected Results:**
- [ ] "Block User" option should NOT appear
- [ ] OR clicking it shows error: "Cannot block yourself"
- [ ] No action is performed

### Test 5.2: Cannot Suspend Yourself
**Steps:**
1. Find YOUR admin user in the list
2. Click Actions dropdown
3. Check available actions

**Expected Results:**
- [ ] "Suspend User" option should NOT appear
- [ ] OR clicking it shows error: "Cannot suspend yourself"
- [ ] No action is performed

### Test 5.3: Cannot Change Your Own Role
**Steps:**
1. Find YOUR admin user in the list
2. Click Actions → "Change Role"
3. Try to change role

**Expected Results:**
- [ ] Error message: "Cannot change your own role"
- [ ] No role change occurs
- [ ] Modal closes or shows error

### Test 5.4: Cannot Suspend Already Suspended User
**Steps:**
1. Suspend a test user (if not already suspended)
2. Try to suspend the same user again
3. Click Actions dropdown

**Expected Results:**
- [ ] "Suspend User" option should NOT appear for suspended users
- [ ] Only "Activate User" option is available
- [ ] OR backend returns error if attempted

### Test 5.5: Cannot Block Already Blocked User
**Steps:**
1. Block a test user (if not already blocked)
2. Try to block the same user again

**Expected Results:**
- [ ] "Block User" option should NOT appear for blocked users
- [ ] Only "Activate User" option is available
- [ ] OR backend returns error if attempted

### Test 5.6: Past Expiration Date Validation
**Steps:**
1. Try to suspend a user
2. Set expiration date to yesterday
3. Try to submit

**Expected Results:**
- [ ] Validation error: "Expiration date must be in the future"
- [ ] Cannot submit form
- [ ] Error message is displayed

### Test 5.7: Network Error Handling
**Steps:**
1. Stop the backend server
2. Try to perform any action (block, suspend, etc.)

**Expected Results:**
- [ ] Error message appears
- [ ] User-friendly error text
- [ ] No application crash
- [ ] Can retry after backend restarts

---

## 6. UI Behavior & UX

### Test 6.1: Loading States
**Steps:**
1. Refresh the page
2. Observe loading behavior
3. Perform an action (block user)
4. Observe loading during action

**Expected Results:**
- [ ] Loading spinner shows while fetching users
- [ ] Table shows loading state
- [ ] Action buttons show loading state during mutations
- [ ] "Blocking..." / "Suspending..." text appears
- [ ] Buttons are disabled during loading
- [ ] No double-submission possible

### Test 6.2: Success Messages
**Steps:**
1. Perform each action successfully
2. Observe success messages

**Expected Results:**
- [ ] Green success message appears at top
- [ ] Message is clear and specific
- [ ] Message auto-dismisses after 5 seconds
- [ ] Message includes checkmark icon

### Test 6.3: Error Messages
**Steps:**
1. Trigger validation errors
2. Trigger backend errors (if possible)

**Expected Results:**
- [ ] Red error message appears at top
- [ ] Error message is clear and helpful
- [ ] Message auto-dismisses after 5 seconds
- [ ] Message includes warning icon

### Test 6.4: Modal Behavior
**Steps:**
1. Open each modal type
2. Test closing mechanisms

**Expected Results:**
- [ ] Modal opens smoothly
- [ ] Background is dimmed
- [ ] Can close with X button
- [ ] Can close with Cancel button
- [ ] ESC key closes modal (optional)
- [ ] Clicking outside closes modal (optional)
- [ ] Form resets when modal closes

### Test 6.5: Table Refresh After Actions
**Steps:**
1. Note current user list
2. Perform an action (block user)
3. Observe table

**Expected Results:**
- [ ] Table refreshes automatically
- [ ] Updated data is shown
- [ ] No need to manually refresh page
- [ ] Pagination stays on same page (or resets appropriately)

### Test 6.6: Mobile Responsive Layout
**Steps:**
1. Resize browser to mobile width (< 768px)
2. Test all features on mobile

**Expected Results:**
- [ ] Table is scrollable horizontally
- [ ] Filters stack vertically
- [ ] Modals fit mobile screen
- [ ] Navigation menu works
- [ ] All actions are accessible
- [ ] Text is readable
- [ ] Buttons are tappable

### Test 6.7: Desktop Layout
**Steps:**
1. View on desktop (> 1024px)

**Expected Results:**
- [ ] Filters are in one row
- [ ] Table uses full width
- [ ] Modals are centered
- [ ] Navigation is horizontal
- [ ] Everything is well-spaced

---

## 7. Backend Integration

### Test 7.1: API Endpoints
**Steps:**
1. Open browser DevTools → Network tab
2. Perform various actions
3. Observe network requests

**Expected Results:**
- [ ] GET `/api/admin/users` - List users
- [ ] GET `/api/admin/users/{id}` - User details
- [ ] PUT `/api/admin/users/{id}/block` - Block user
- [ ] PUT `/api/admin/users/{id}/suspend` - Suspend user
- [ ] PUT `/api/admin/users/{id}/activate` - Activate user
- [ ] PUT `/api/admin/users/{id}/role` - Change role
- [ ] All requests include Authorization header
- [ ] All requests return expected status codes (200, 400, 404, etc.)

### Test 7.2: Data Persistence
**Steps:**
1. Block a user
2. Refresh the page
3. Check user status

**Expected Results:**
- [ ] User remains blocked after refresh
- [ ] Status persists in database
- [ ] All changes are permanent

### Test 7.3: Pagination Parameters
**Steps:**
1. Navigate to page 2
2. Check network request

**Expected Results:**
- [ ] Request includes `page=1` parameter
- [ ] Request includes `size=20` parameter
- [ ] Correct page of data is returned

### Test 7.4: Filter Parameters
**Steps:**
1. Apply filters
2. Check network request

**Expected Results:**
- [ ] Search: `search=value` parameter
- [ ] Role: `role=STUDENT` parameter
- [ ] Status: `status=ACTIVE` parameter
- [ ] All active filters are sent to backend

---

## 8. Edge Cases & Stress Testing

### Test 8.1: Large User List
**Steps:**
1. If database has many users (>100)
2. Test pagination performance
3. Test filtering performance

**Expected Results:**
- [ ] Page loads in reasonable time
- [ ] Pagination works smoothly
- [ ] Filters respond quickly
- [ ] No performance degradation

### Test 8.2: Special Characters in Search
**Steps:**
1. Search for: `test@email.com`
2. Search for: `O'Brien`
3. Search for: `José`

**Expected Results:**
- [ ] Special characters are handled correctly
- [ ] No errors occur
- [ ] Results are accurate

### Test 8.3: Concurrent Actions
**Steps:**
1. Open two browser tabs as ADMIN
2. In tab 1: Block a user
3. In tab 2: Try to suspend the same user

**Expected Results:**
- [ ] Second action fails with appropriate error
- [ ] OR second tab refreshes and shows updated status
- [ ] No data corruption

### Test 8.4: Session Expiration
**Steps:**
1. Login as ADMIN
2. Wait for token to expire (or manually clear token)
3. Try to perform an action

**Expected Results:**
- [ ] 401 Unauthorized error
- [ ] Redirected to login page
- [ ] Can login again and continue

---

## Test Summary Checklist

After completing all tests, verify:

- [ ] All page access tests passed
- [ ] User listing works correctly
- [ ] All filters function properly
- [ ] All user actions work end-to-end
- [ ] All validation cases are handled
- [ ] UI behavior is smooth and responsive
- [ ] Backend integration is correct
- [ ] No console errors throughout testing
- [ ] Mobile and desktop layouts work
- [ ] Error handling is user-friendly

---

## Issues Found

Document any issues discovered during testing:

| Test # | Issue Description | Severity | Steps to Reproduce |
|--------|------------------|----------|-------------------|
|        |                  |          |                   |

---

## Notes

- Test with different browsers (Chrome, Firefox, Safari) if possible
- Test with different screen sizes
- Check browser console for any warnings or errors
- Verify database state after actions using database tools
- Take screenshots of any issues found

---

## Sign-off

- [ ] All critical tests passed
- [ ] All blocking issues resolved
- [ ] Ready for component test implementation

**Tester:** _______________  
**Date:** _______________  
**Environment:** _______________
