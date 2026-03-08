package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.CourseDTO;
import com.hadassahhub.backend.dto.CourseWithResourcesDTO;
import com.hadassahhub.backend.dto.ResourceStatsDTO;
import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.StudyYear;
import com.hadassahhub.backend.repository.CourseRepository;
import com.hadassahhub.backend.repository.ResourceRepository;
import com.hadassahhub.backend.specification.CourseSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final ResourceRepository resourceRepository;

    public CourseService(CourseRepository courseRepository, ResourceRepository resourceRepository) {
        this.courseRepository = courseRepository;
        this.resourceRepository = resourceRepository;
    }

    public List<CourseDTO> findCourses(String search, CourseCategory category, StudyYear year) {
        Specification<Course> spec = Specification.where(CourseSpecifications.hasNameContaining(search))
                .and(CourseSpecifications.hasCategory(category))
                .and(CourseSpecifications.hasRecommendedYear(year));

        List<Course> courses = courseRepository.findAll(spec);
        
        return courses.stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<CourseDTO> findCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::toDTO);
    }
    
    /**
     * Gets course resource statistics for enhanced course details.
     */
    public ResourceStatsDTO getCourseResourceStats(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Course not found with id: " + courseId);
        }
        
        Course course = courseOpt.get();
        
        // Get approved resources for the course
        List<Resource> approvedResources = resourceRepository.findByCourseIdAndStatus(
            courseId, ResourceStatus.APPROVED
        );
        
        if (approvedResources.isEmpty()) {
            return ResourceStatsDTO.empty(courseId, course.getName());
        }
        
        // Calculate statistics
        long totalResources = approvedResources.size();
        long examCount = approvedResources.stream()
            .filter(r -> r.getType() == com.hadassahhub.backend.enums.ResourceType.EXAM)
            .count();
        long homeworkCount = approvedResources.stream()
            .filter(r -> r.getType() == com.hadassahhub.backend.enums.ResourceType.HOMEWORK)
            .count();
        long summaryCount = approvedResources.stream()
            .filter(r -> r.getType() == com.hadassahhub.backend.enums.ResourceType.SUMMARY)
            .count();
        long linkCount = approvedResources.stream()
            .filter(r -> r.getType() == com.hadassahhub.backend.enums.ResourceType.LINK)
            .count();
        
        return ResourceStatsDTO.basic(courseId, course.getName(), totalResources, 
                                    examCount, homeworkCount, summaryCount, linkCount);
    }
    
    /**
     * Gets course with enhanced resource information.
     */
    public Optional<CourseWithResourcesDTO> getCourseWithResources(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Course course = courseOpt.get();
        ResourceStatsDTO resourceStats = getCourseResourceStats(courseId);
        
        // Get recent approved resources (last 5)
        List<Resource> recentResources = resourceRepository.findByCourseIdAndStatus(courseId, ResourceStatus.APPROVED)
            .stream()
            .limit(5)
            .toList();
        
        return Optional.of(new CourseWithResourcesDTO(
            course.getId(),
            course.getName(),
            course.getDescription(),
            course.getCategory(),
            course.getCredits(),
            course.getRecommendedYear(),
            resourceStats,
            recentResources.size()
        ));
    }
    
    /**
     * Gets resource count aggregation by type for a course.
     */
    public java.util.Map<com.hadassahhub.backend.enums.ResourceType, Long> getResourceCountsByType(Long courseId) {
        List<Resource> approvedResources = resourceRepository.findByCourseIdAndStatus(courseId, ResourceStatus.APPROVED);
        
        return approvedResources.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Resource::getType,
                java.util.stream.Collectors.counting()
            ));
    }
    
    /**
     * Gets recent resources for course pages (last N resources).
     */
    public List<Resource> getRecentResourcesForCourse(Long courseId, int limit) {
        return resourceRepository.findByCourseIdAndStatus(courseId, ResourceStatus.APPROVED)
            .stream()
            .limit(limit)
            .toList();
    }

    private CourseDTO toDTO(Course course) {
        return CourseDTO.basic(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCategory(),
                course.getCredits(),
                course.getRecommendedYear()
        );
    }
}