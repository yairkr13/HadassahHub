package com.hadassahhub.backend.enums;

/**
 * Represents the status of a user account.
 * 
 * ACTIVE: User can access the system normally
 * BLOCKED: User cannot login (permanent until unblocked by admin)
 * SUSPENDED: User cannot login (temporary with expiration date)
 */
public enum UserStatus {
    ACTIVE,      // Default status - user can access system
    BLOCKED,     // Permanently blocked until admin unblocks
    SUSPENDED    // Temporarily suspended with expiration date
}
