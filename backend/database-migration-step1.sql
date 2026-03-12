-- Step 1: Database Migration for User Status Management
-- Run this script to fix the database schema after Step 1 changes

-- Connect to the database first:
-- psql -U hadassahhub -d hadassahhub -h localhost

-- 1. Add status column (if it doesn't exist)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'status'
    ) THEN
        ALTER TABLE users ADD COLUMN status VARCHAR(20);
    END IF;
END $$;

-- 2. Backfill existing users with ACTIVE status
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;

-- 3. Make status column NOT NULL with default
ALTER TABLE users ALTER COLUMN status SET NOT NULL;
ALTER TABLE users ALTER COLUMN status SET DEFAULT 'ACTIVE';

-- 4. Add other new columns (if they don't exist)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'blocked_at'
    ) THEN
        ALTER TABLE users ADD COLUMN blocked_at TIMESTAMP;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'blocked_by'
    ) THEN
        ALTER TABLE users ADD COLUMN blocked_by BIGINT;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'block_reason'
    ) THEN
        ALTER TABLE users ADD COLUMN block_reason VARCHAR(500);
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'last_login'
    ) THEN
        ALTER TABLE users ADD COLUMN last_login TIMESTAMP;
    END IF;
END $$;

-- 5. Create user_suspensions table (if it doesn't exist)
CREATE TABLE IF NOT EXISTS user_suspensions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    suspended_at TIMESTAMP NOT NULL,
    suspended_by BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    reason VARCHAR(500) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    lifted_at TIMESTAMP,
    lifted_by BIGINT
);

-- 6. Create index on user_id for performance
CREATE INDEX IF NOT EXISTS idx_user_suspensions_user_id ON user_suspensions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_suspensions_is_active ON user_suspensions(is_active);

-- Verification queries
SELECT 'Users table columns:' as info;
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'users' 
AND column_name IN ('status', 'blocked_at', 'blocked_by', 'block_reason', 'last_login')
ORDER BY column_name;

SELECT 'User status distribution:' as info;
SELECT status, COUNT(*) as count FROM users GROUP BY status;

SELECT 'User suspensions table exists:' as info;
SELECT table_name FROM information_schema.tables WHERE table_name = 'user_suspensions';
