package com.hadassahhub.backend.dto;

/**
 * DTO for moderation statistics in the admin dashboard.
 * Provides counts of resources by moderation status.
 */
public record ModerationStatsDTO(
    long pendingCount,
    long approvedCount,
    long rejectedCount
) {
    
    /**
     * Gets the total number of resources across all statuses.
     */
    public long getTotalResources() {
        return pendingCount + approvedCount + rejectedCount;
    }
    
    /**
     * Checks if there are any pending resources awaiting moderation.
     */
    public boolean hasPendingResources() {
        return pendingCount > 0;
    }
    
    /**
     * Gets the approval rate as a percentage (0-100).
     * Returns 0 if no resources have been moderated.
     */
    public double getApprovalRate() {
        long moderatedCount = approvedCount + rejectedCount;
        if (moderatedCount == 0) {
            return 0.0;
        }
        return (double) approvedCount / moderatedCount * 100.0;
    }
    
    /**
     * Gets the rejection rate as a percentage (0-100).
     * Returns 0 if no resources have been moderated.
     */
    public double getRejectionRate() {
        long moderatedCount = approvedCount + rejectedCount;
        if (moderatedCount == 0) {
            return 0.0;
        }
        return (double) rejectedCount / moderatedCount * 100.0;
    }
    
    /**
     * Creates empty moderation stats.
     */
    public static ModerationStatsDTO empty() {
        return new ModerationStatsDTO(0L, 0L, 0L);
    }
}