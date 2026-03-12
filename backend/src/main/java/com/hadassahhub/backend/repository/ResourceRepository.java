package com.hadassahhub.backend.repository;

import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Resource entity operations.
 * Provides both basic CRUD operations and complex query capabilities through JPA Specifications.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {
    
    /**
     * Find all approved resources for a specific course.
     * Used for displaying resources on course details page.
     */
    @Query("SELECT r FROM Resource r WHERE r.course.id = :courseId AND r.status = :status ORDER BY r.createdAt DESC")
    List<Resource> findByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") ResourceStatus status);
    
    /**
     * Find approved resources for a course with pagination.
     */
    @Query("SELECT r FROM Resource r WHERE r.course.id = :courseId AND r.status = :status")
    Page<Resource> findByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") ResourceStatus status, Pageable pageable);
    
    /**
     * Find approved resources for a course filtered by type.
     */
    @Query("SELECT r FROM Resource r WHERE r.course.id = :courseId AND r.status = :status AND r.type = :type ORDER BY r.createdAt DESC")
    List<Resource> findByCourseIdAndStatusAndType(@Param("courseId") Long courseId, @Param("status") ResourceStatus status, @Param("type") ResourceType type);
    
    /**
     * Find all resources uploaded by a specific user.
     */
    List<Resource> findByUploadedByIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Count resources uploaded by a specific user.
     */
    long countByUploadedById(Long userId);
    
    /**
     * Find resources by uploader and status.
     */
    List<Resource> findByUploadedByIdAndStatusOrderByCreatedAtDesc(Long userId, ResourceStatus status);
    
    /**
     * Find all pending resources for admin moderation.
     */
    List<Resource> findByStatusOrderByCreatedAtAsc(ResourceStatus status);
    
    /**
     * Find pending resources with pagination for admin interface.
     */
    Page<Resource> findByStatus(ResourceStatus status, Pageable pageable);
    
    /**
     * Count resources by status for dashboard statistics.
     */
    long countByStatus(ResourceStatus status);
    
    /**
     * Count approved resources for a specific course.
     */
    long countByCourseIdAndStatus(Long courseId, ResourceStatus status);
    
    /**
     * Check if a user has already uploaded a resource with the same title for a course.
     * Helps prevent duplicate submissions.
     */
    boolean existsByCourseIdAndUploadedByIdAndTitle(Long courseId, Long userId, String title);
    
    /**
     * Find resources by academic year for a course.
     */
    @Query("SELECT r FROM Resource r WHERE r.course.id = :courseId AND r.status = :status AND r.academicYear = :academicYear ORDER BY r.createdAt DESC")
    List<Resource> findByCourseIdAndStatusAndAcademicYear(@Param("courseId") Long courseId, @Param("status") ResourceStatus status, @Param("academicYear") String academicYear);
}