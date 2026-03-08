package com.hadassahhub.backend.specification;

import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecifications {

    public static Specification<Course> hasNameContaining(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // Always true - no filtering
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Course> hasCategory(CourseCategory category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction(); // Always true - no filtering
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    public static Specification<Course> hasRecommendedYear(StudyYear year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null) {
                return criteriaBuilder.conjunction(); // Always true - no filtering
            }
            return criteriaBuilder.equal(root.get("recommendedYear"), year);
        };
    }
}