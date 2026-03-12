-- ============================================================================
-- Database Migration: Fix User Status Schema
-- ============================================================================
-- Purpose: Add missing status column and related fields to users table
-- Date: 2026-03-12
-- Issue: Login broken due to missing status column after Step 1 changes
-- ============================================================================

-- Step 1: Add status column if it doesn't exist
-- Default to 'ACTIVE' for all existing users
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'status'
    ) THEN
        ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
        RAISE NOTICE 'Added status column with default ACTIVE';
    ELSE
        RAISE NOTICE 'Status column already exists';
    END IF;
END $$;

-- Step 2: Backfill any NULL status values to ACTIVE
UPDATE users 
SET status = 'ACTIVE' 
WHERE status IS NULL;

-- Step 3: Add blocked_at column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'blocked_at'
    ) THEN
        ALTER TABLE users ADD COLUMN blocked_at TIMESTAMP;
        RAISE NOTICE 'Added blocked_at column';
    ELSE
        RAISE NOTICE 'blocked_at column already exists';
    END IF;
END $$;

-- Step 4: Add blocked_by column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'blocked_by'
    ) THEN
        ALTER TABLE users ADD COLUMN blocked_by BIGINT;
        RAISE NOTICE 'Added blocked_by column';
    ELSE
        RAISE NOTICE 'blocked_by column already exists';
    END IF;
END $$;

-- Step 5: Add block_reason column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'block_reason'
    ) THEN
        ALTER TABLE users ADD COLUMN block_reason VARCHAR(500);
        RAISE NOTICE 'Added block_reason column';
    ELSE
        RAISE NOTICE 'block_reason column already exists';
    END IF;
END $$;

-- Step 6: Add last_login column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'last_login'
    ) THEN
        ALTER TABLE users ADD COLUMN last_login TIMESTAMP;
        RAISE NOTICE 'Added last_login column';
    ELSE
        RAISE NOTICE 'last_login column already exists';
    END IF;
END $$;

-- Step 7: Verify the schema
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns
WHERE table_name = 'users'
AND column_name IN ('status', 'blocked_at', 'blocked_by', 'block_reason', 'last_login')
ORDER BY column_name;

-- Step 8: Verify all users have ACTIVE status
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as active_users,
    COUNT(CASE WHEN status IS NULL THEN 1 END) as null_status_users
FROM users;

-- Expected result: all users should have status = 'ACTIVE', null_status_users should be 0
