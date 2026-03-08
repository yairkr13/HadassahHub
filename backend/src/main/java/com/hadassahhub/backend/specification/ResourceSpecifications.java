package com.hadassahhub.backend.specification;

import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for Resource entity to enable dynamic query building.
 * Provides flexible filtering capabilities for the resource system.
 */
public class ResourceSpecifications {
    
    /**
     * Filter resources by course ID.
     */
    public static Specification<Resource> hasCourseId(Long courseId) {
        return (root, query, criteriaBuilder) -> {
            if (courseId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("course").get("id"), courseId);
        };
    }
    
    /**
     * Filter resources by status.
     */
    public static Specification<Resource> hasStatus(ResourceStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
    
    /**
     * Filter resources by type.
     */
    public static Specification<Resource> hasType(ResourceType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }
    
    /**
     * Filter resources by uploader ID.
     */
    public static Specification<Resource> hasUploaderId(Long uploaderId) {
        return (root, query, criteriaBuilder) -> {
            if (uploaderId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("uploadedBy").get("id"), uploaderId);
        };
    }
    
    /**
     * Filter resources by academic year.
     */
    public static Specification<Resource> hasAcademicYear(String academicYear) {
        return (root, query, criteriaBuilder) -> {
            if (academicYear == null || academicYear.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("academicYear"), academicYear);
        };
    }
    
    /**
     * Filter resources by exam term.
     */
    public static Specification<Resource> hasExamTerm(String examTerm) {
        return (root, query, criteriaBuilder) -> {
            if (examTerm == null || examTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("examTerm"), examTerm);
        };
    }
    
    /**
     * Search resources by title (case-insensitive partial match).
     */
    public static Specification<Resource> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")), 
                "%" + title.toLowerCase() + "%"
            );
        };
    }
    
    /**
     * Filter resources that are approved (convenience method).
     */
    public static Specification<Resource> isApproved() {
        return hasStatus(ResourceStatus.APPROVED);
    }
    
    /**
     * Filter resources that are pending (convenience method).
     */
    public static Specification<Resource> isPending() {
        return hasStatus(ResourceStatus.PENDING);
    }
    
    /**
     * Filter resources that are rejected (convenience method).
     */
    public static Specification<Resource> isRejected() {
        return hasStatus(ResourceStatus.REJECTED);
    }
    
    /**
     * Complex specification builder for course page filtering.
     * Combines course ID, status, and optional type/year filters.
     */
    public static Specification<Resource> forCoursePageFiltering(
            Long courseId, 
            ResourceType type, 
            String academicYear, 
            String searchTitle) {
        
        return Specification.where(hasCourseId(courseId))
                .and(isApproved())
                .and(hasType(type))
                .and(hasAcademicYear(academicYear))
                .and(titleContains(searchTitle));
    }
    
    /**
     * Specification for admin moderation interface.
     * Allows filtering pending resources by course, type, and uploader.
     */
    public static Specification<Resource> forAdminModeration(
            Long courseId, 
            ResourceType type, 
            Long uploaderId) {
        
        return Specification.where(isPending())
                .and(hasCourseId(courseId))
                .and(hasType(type))
                .and(hasUploaderId(uploaderId));
    }
    
    /**
     * Specification for user's resource management.
     * Shows all resources uploaded by a specific user with optional status filter.
     */
    public static Specification<Resource> forUserResources(
            Long uploaderId, 
            ResourceStatus status, 
            ResourceType type) {
        
        return Specification.where(hasUploaderId(uploaderId))
                .and(hasStatus(status))
                .and(hasType(type));
    }
    
    /**
     * Dynamic specification builder that accepts multiple optional criteria.
     * Useful for building flexible search interfaces.
     */
    public static Specification<Resource> withCriteria(
            Long courseId,
            ResourceStatus status,
            ResourceType type,
            Long uploaderId,
            String academicYear,
            String examTerm,
            String titleSearch) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (courseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("id"), courseId));
            }
            
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            
            if (uploaderId != null) {
                predicates.add(criteriaBuilder.equal(root.get("uploadedBy").get("id"), uploaderId));
            }
            
            if (academicYear != null && !academicYear.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("academicYear"), academicYear));
            }
            
            if (examTerm != null && !examTerm.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("examTerm"), examTerm));
            }
            
            if (titleSearch != null && !titleSearch.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")), 
                    "%" + titleSearch.toLowerCase() + "%"
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}