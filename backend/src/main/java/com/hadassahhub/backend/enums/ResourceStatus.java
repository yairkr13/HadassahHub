package com.hadassahhub.backend.enums;

/**
 * Enum representing the approval status of a resource.
 * Used for moderation workflow where resources must be approved before being visible to students.
 */
public enum ResourceStatus {
    PENDING,   // Awaiting approval from moderator
    APPROVED,  // Approved and visible to all users
    REJECTED   // Rejected by moderator with optional reason
}