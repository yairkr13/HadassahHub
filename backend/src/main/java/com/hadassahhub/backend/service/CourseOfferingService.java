package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.CourseOfferingDTO;
import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.CourseOffering;
import com.hadassahhub.backend.enums.Semester;
import com.hadassahhub.backend.enums.StudyYear;
import com.hadassahhub.backend.repository.CourseOfferingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseOfferingService {

    private final CourseOfferingRepository offeringRepo;

    public CourseOfferingService(CourseOfferingRepository offeringRepo) {
        this.offeringRepo = offeringRepo;
    }

    public List<CourseOfferingDTO> listOfferings(StudyYear year, Semester semester) {
        List<CourseOffering> offerings;

        if (year != null && semester != null) {
            offerings = offeringRepo.findByYearAndSemester(year, semester);
        } else if (year != null) {
            offerings = offeringRepo.findByYear(year);
        } else if (semester != null) {
            offerings = offeringRepo.findBySemester(semester);
        } else {
            offerings = offeringRepo.findAll();
        }

        return offerings.stream()
                .map(this::toDTO)
                .toList();
    }

    private CourseOfferingDTO toDTO(CourseOffering offering) {
        Course c = offering.getCourse();

        return new CourseOfferingDTO(
                offering.getId(),
                offering.getYear(),
                offering.getSemester(),
                c.getId(),
                c.getName(),
                c.getCategory(),
                c.getDescription()
        );
    }
}