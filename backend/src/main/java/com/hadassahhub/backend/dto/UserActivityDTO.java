package com.hadassahhub.backend.dto;

import java.util.List;

/**
 * DTO for user activity history.
 * Contains uploaded resources and download statistics.
 */
public record UserActivityDTO(
    Long userId,
    List<UserResourceDTO> resourcesUploaded,
    List<UserDownloadDTO> recentDownloads,
    Integer totalUploads,
    Integer totalDownloads
) {
    
    /**
     * DTO for a resource uploaded by the user.
     */
    public record UserResourceDTO(
        Long id,
        String title,
        String type,
        String status,
        String uploadedAt
    ) {
    }
    
    /**
     * DTO for a recent download by the user.
     */
    public record UserDownloadDTO(
        Long resourceId,
        String resourceTitle,
        String downloadedAt
    ) {
    }
}
