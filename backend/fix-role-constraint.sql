-- ============================================================================
-- Database Migration: Fix Role Constraint to Include MODERATOR
-- ============================================================================
-- Purpose: Remove or update CHECK constraint on users.role to allow MODERATOR
-- Date: 2026-03-12
-- Issue: Cannot change user role to MODERATOR due to database constraint
-- ============================================================================

-- Step 1: Check current constraints on users table
SELECT 
    conname AS constraint_name,
    contype AS constraint_type,
    pg_get_constraintdef(oid) AS constraint_definition
FROM pg_constraint
WHERE conrelid = 'users'::regclass
AND contype = 'c';  -- CHECK constraints

-- Step 2: Drop the existing role CHECK constraint if it exists
-- Note: The constraint name might vary, common names are:
-- - users_role_check
-- - role_check
-- - check_role
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    -- Find the CHECK constraint on the role column
    SELECT conname INTO constraint_name
    FROM pg_constraint
    WHERE conrelid = 'users'::regclass
    AND contype = 'c'
    AND pg_get_constraintdef(oid) LIKE '%role%'
    LIMIT 1;
    
    -- Drop the constraint if found
    IF constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE users DROP CONSTRAINT %I', constraint_name);
        RAISE NOTICE 'Dropped constraint: %', constraint_name;
    ELSE
        RAISE NOTICE 'No role CHECK constraint found';
    END IF;
END $$;

-- Step 3: Add new CHECK constraint that includes MODERATOR
ALTER TABLE users 
ADD CONSTRAINT users_role_check 
CHECK (role IN ('STUDENT', 'MODERATOR', 'ADMIN'));

-- Step 4: Verify the new constraint
SELECT 
    conname AS constraint_name,
    pg_get_constraintdef(oid) AS constraint_definition
FROM pg_constraint
WHERE conrelid = 'users'::regclass
AND contype = 'c'
AND conname = 'users_role_check';

-- Step 5: Test that MODERATOR role is now allowed
-- This should succeed without errors
DO $$
BEGIN
    -- Try to update a test user to MODERATOR (will rollback)
    BEGIN
        UPDATE users SET role = 'MODERATOR' WHERE id = 1;
        RAISE NOTICE 'MODERATOR role is now allowed';
        ROLLBACK;
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'Error: %', SQLERRM;
        ROLLBACK;
    END;
END $$;

-- Expected result: "MODERATOR role is now allowed"
