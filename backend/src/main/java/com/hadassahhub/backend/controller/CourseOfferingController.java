package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.CourseOfferingDTO;
import com.hadassahhub.backend.enums.Semester;
import com.hadassahhub.backend.enums.StudyYear;
import com.hadassahhub.backend.service.CourseOfferingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offerings")
public class CourseOfferingController {

    private final CourseOfferingService offeringService;

    public CourseOfferingController(CourseOfferingService offeringService) {
        this.offeringService = offeringService;
    }

    // GET /api/offerings
    @GetMapping
    public List<CourseOfferingDTO> listAll(
            @RequestParam(required = false) StudyYear year,
            @RequestParam(required = false) Semester semester
    ) {
        return offeringService.listOfferings(year, semester);
    }
}