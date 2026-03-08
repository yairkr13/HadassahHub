package com.hadassahhub.backend.repository;

import com.hadassahhub.backend.entity.CourseOffering;
import com.hadassahhub.backend.enums.Semester;
import com.hadassahhub.backend.enums.StudyYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    List<CourseOffering> findByYear(StudyYear year);
    List<CourseOffering> findBySemester(Semester semester);
    List<CourseOffering> findByYearAndSemester(StudyYear year, Semester semester);
}