package com.hadassahhub.backend.repository;

import com.hadassahhub.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    // JpaSpecificationExecutor provides findAll(Specification<Course> spec)
    // This avoids method explosion and provides flexible filtering
}