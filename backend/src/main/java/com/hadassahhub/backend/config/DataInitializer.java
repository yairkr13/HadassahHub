package com.hadassahhub.backend.config;

import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;
import com.hadassahhub.backend.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    // Disabled DataInitializer to avoid conflicts with DataSeeder
    // DataSeeder provides more comprehensive test data with the new Course structure
    
    /*
    @Bean
    CommandLineRunner initCourses(CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.count() > 0) {
                return; // יש כבר נתונים, לא נוגעים
            }

            List<Course> demoCourses = List.of(
                    new Course("Intro to CS", "Basics of programming and CS thinking", CourseCategory.CS_CORE, 4, StudyYear.Y1),
                    new Course("Data Structures", "Lists, stacks, queues, trees, complexity", CourseCategory.CS_CORE, 4, StudyYear.Y1),
                    new Course("Discrete Math", "Logic, sets, graphs, proofs", CourseCategory.CS_CORE, 3, StudyYear.Y1),
                    new Course("Linear Algebra", "Vectors, matrices, transformations", CourseCategory.CS_CORE, 3, StudyYear.Y2),
                    new Course("Communication Skills", "Presentation and writing skills", CourseCategory.GENERAL_ELECTIVE, 2, null)
            );

            courseRepository.saveAll(demoCourses);
            System.out.println("Inserted demo courses: " + demoCourses.size());
        };
    }
    */
}