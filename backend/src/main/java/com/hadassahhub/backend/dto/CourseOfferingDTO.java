package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.Semester;
import com.hadassahhub.backend.enums.StudyYear;

public record CourseOfferingDTO(
        Long offeringId,
        StudyYear year,
        Semester semester,

        Long courseId,
        String courseName,
        CourseCategory category,
        String description
) {}