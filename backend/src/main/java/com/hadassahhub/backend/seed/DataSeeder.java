package com.hadassahhub.backend.seed;

import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.entity.CourseOffering;
import com.hadassahhub.backend.entity.Resource;
import com.hadassahhub.backend.entity.User;
import com.hadassahhub.backend.enums.*;
import com.hadassahhub.backend.repository.CourseOfferingRepository;
import com.hadassahhub.backend.repository.CourseRepository;
import com.hadassahhub.backend.repository.ResourceRepository;
import com.hadassahhub.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepo;
    private final CourseOfferingRepository offeringRepo;
    private final UserRepository userRepo;
    private final ResourceRepository resourceRepo;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public DataSeeder(CourseRepository courseRepo, 
                     CourseOfferingRepository offeringRepo,
                     UserRepository userRepo,
                     ResourceRepository resourceRepo,
                     PasswordEncoder passwordEncoder) {
        this.courseRepo = courseRepo;
        this.offeringRepo = offeringRepo;
        this.userRepo = userRepo;
        this.resourceRepo = resourceRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("DataSeeder: Starting data seeding...");
            
            // Check if data already exists
            long courseCount = courseRepo.count();
            long userCount = userRepo.count();
            long resourceCount = resourceRepo.count();
            System.out.println("DataSeeder: Found " + courseCount + " existing courses, " + userCount + " existing users, and " + resourceCount + " existing resources");
            
            // Seed users if none exist
            if (userCount == 0) {
                seedUsers();
            } else {
                System.out.println("DataSeeder: Users already exist, skipping user seeding");
            }
            
            // Seed courses if none exist
            if (courseCount == 0) {
                seedCourses();
            } else {
                System.out.println("DataSeeder: Courses already exist, skipping course seeding");
            }
            
            // Seed resources if none exist and we have courses and users
            if (resourceCount == 0 && courseCount > 0 && userCount > 0) {
                seedResources();
            } else {
                System.out.println("DataSeeder: Resources already exist or prerequisites missing, skipping resource seeding");
            }

            System.out.println("DataSeeder: Data seeding completed successfully!");
            
        } catch (Exception e) {
            System.err.println("DataSeeder: Error during data seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedUsers() {
        System.out.println("DataSeeder: Creating test users...");

        // Create admin user
        User admin = new User(
                "admin@edu.jmc.ac.il",
                passwordEncoder.encode("admin123"),
                "System Administrator",
                UserRole.ADMIN
        );
        userRepo.save(admin);
        System.out.println("DataSeeder: Created admin user - " + admin.getEmail());

        // Create student user
        User student = new User(
                "student@edu.hac.ac.il",
                passwordEncoder.encode("student123"),
                "Test Student",
                UserRole.STUDENT
        );
        userRepo.save(student);
        System.out.println("DataSeeder: Created student user - " + student.getEmail());

        System.out.println("DataSeeder: User seeding completed!");
    }

    private void seedCourses() {
        System.out.println("DataSeeder: Creating courses...");

        // Create CS_CORE courses (required courses with recommended year)
        Course intro = new Course(
                "Introduction to Computer Science",
                "Basic programming concepts and problem-solving techniques",
                CourseCategory.CS_CORE,
                4,
                StudyYear.Y1
        );
        courseRepo.save(intro);
        System.out.println("DataSeeder: Created course - " + intro.getName());

        Course dataStructures = new Course(
                "Data Structures",
                "Arrays, linked lists, stacks, queues, trees, and graphs",
                CourseCategory.CS_CORE,
                4,
                StudyYear.Y1
        );
        courseRepo.save(dataStructures);
        System.out.println("DataSeeder: Created course - " + dataStructures.getName());

        Course discreteMath = new Course(
                "Discrete Mathematics",
                "Mathematical foundations for computer science",
                CourseCategory.CS_CORE,
                3,
                StudyYear.Y1
        );
        courseRepo.save(discreteMath);
        System.out.println("DataSeeder: Created course - " + discreteMath.getName());

        Course algorithms = new Course(
                "Algorithms",
                "Algorithm design and analysis techniques",
                CourseCategory.CS_CORE,
                4,
                StudyYear.Y2
        );
        courseRepo.save(algorithms);
        System.out.println("DataSeeder: Created course - " + algorithms.getName());

        Course databases = new Course(
                "Database Systems",
                "Relational databases, SQL, and database design",
                CourseCategory.CS_CORE,
                4,
                StudyYear.Y2
        );
        courseRepo.save(databases);
        System.out.println("DataSeeder: Created course - " + databases.getName());

        // Create CS_ELECTIVE courses (no recommended year)
        Course webDev = new Course(
                "Web Development",
                "Modern web technologies and frameworks",
                CourseCategory.CS_ELECTIVE,
                3,
                null
        );
        courseRepo.save(webDev);
        System.out.println("DataSeeder: Created course - " + webDev.getName());

        Course machineLearning = new Course(
                "Machine Learning",
                "Introduction to ML algorithms and applications",
                CourseCategory.CS_ELECTIVE,
                4,
                null
        );
        courseRepo.save(machineLearning);
        System.out.println("DataSeeder: Created course - " + machineLearning.getName());

        // Create GENERAL_ELECTIVE courses (no recommended year)
        Course psychology = new Course(
                "Psychology",
                "Introduction to psychological principles",
                CourseCategory.GENERAL_ELECTIVE,
                2,
                null
        );
        courseRepo.save(psychology);
        System.out.println("DataSeeder: Created course - " + psychology.getName());

        Course philosophy = new Course(
                "Philosophy of Science",
                "Philosophical foundations of scientific inquiry",
                CourseCategory.GENERAL_ELECTIVE,
                2,
                null
        );
        courseRepo.save(philosophy);
        System.out.println("DataSeeder: Created course - " + philosophy.getName());

        System.out.println("DataSeeder: Creating course offerings...");

        // Create course offerings
        offeringRepo.save(new CourseOffering(intro, StudyYear.Y1, Semester.A));
        offeringRepo.save(new CourseOffering(intro, StudyYear.Y1, Semester.B));
        
        offeringRepo.save(new CourseOffering(dataStructures, StudyYear.Y1, Semester.B));
        
        offeringRepo.save(new CourseOffering(discreteMath, StudyYear.Y1, Semester.A));
        
        offeringRepo.save(new CourseOffering(algorithms, StudyYear.Y2, Semester.A));
        
        offeringRepo.save(new CourseOffering(databases, StudyYear.Y2, Semester.B));
        
        offeringRepo.save(new CourseOffering(webDev, StudyYear.Y2, Semester.A));
        offeringRepo.save(new CourseOffering(webDev, StudyYear.Y3, Semester.B));
        
        offeringRepo.save(new CourseOffering(machineLearning, StudyYear.Y3, Semester.A));

        System.out.println("DataSeeder: Course seeding completed!");
    }
    
    private void seedResources() {
        System.out.println("DataSeeder: Creating sample resources...");
        
        // Get existing courses and users
        List<Course> courses = courseRepo.findAll();
        List<User> students = userRepo.findAll().stream()
            .filter(user -> user.getRole() == UserRole.STUDENT)
            .toList();
        List<User> admins = userRepo.findAll().stream()
            .filter(user -> user.getRole() == UserRole.ADMIN)
            .toList();
        
        if (courses.isEmpty() || students.isEmpty()) {
            System.out.println("DataSeeder: No courses or students found, skipping resource seeding");
            return;
        }
        
        User admin = admins.isEmpty() ? null : admins.get(0);
        
        // Sample resource data
        String[] examTitles = {
            "Final Exam", "Midterm Exam", "Practice Exam", "Sample Questions", "Previous Year Exam"
        };
        String[] homeworkTitles = {
            "Assignment 1", "Assignment 2", "Project Guidelines", "Lab Exercise", "Problem Set"
        };
        String[] summaryTitles = {
            "Course Summary", "Chapter Notes", "Study Guide", "Key Concepts", "Review Material"
        };
        String[] linkTitles = {
            "Useful Tutorial", "Reference Documentation", "Online Course", "Video Lectures", "Practice Platform"
        };
        
        String[] academicYears = {"2024-2025", "2023-2024", "2022-2023"};
        String[] examTerms = {"Moed A", "Moed B", "Moed C"};
        
        int resourceCount = 0;
        
        // Create resources for each course
        for (Course course : courses) {
            User uploader = students.get(random.nextInt(students.size()));
            
            // Create 2-4 resources per course
            int resourcesPerCourse = 2 + random.nextInt(3);
            
            for (int i = 0; i < resourcesPerCourse; i++) {
                ResourceType type = ResourceType.values()[random.nextInt(ResourceType.values().length)];
                String title;
                String academicYear = null;
                String examTerm = null;
                
                // Choose title based on type
                switch (type) {
                    case EXAM -> {
                        title = examTitles[random.nextInt(examTitles.length)];
                        academicYear = academicYears[random.nextInt(academicYears.length)];
                        examTerm = examTerms[random.nextInt(examTerms.length)];
                    }
                    case HOMEWORK -> {
                        title = homeworkTitles[random.nextInt(homeworkTitles.length)];
                        academicYear = academicYears[random.nextInt(academicYears.length)];
                    }
                    case SUMMARY -> {
                        title = summaryTitles[random.nextInt(summaryTitles.length)];
                        if (random.nextBoolean()) {
                            academicYear = academicYears[random.nextInt(academicYears.length)];
                        }
                    }
                    case LINK -> {
                        title = linkTitles[random.nextInt(linkTitles.length)];
                    }
                    default -> {
                        title = "Generic Resource";
                    }
                }
                
                // Create resource
                String url = "https://example.com/" + course.getName().toLowerCase().replace(" ", "-") + 
                           "/" + title.toLowerCase().replace(" ", "-") + ".pdf";
                
                Resource resource = new Resource(course, uploader, title, type, url);
                resource.setAcademicYear(academicYear);
                resource.setExamTerm(examTerm);
                
                // Randomly set status (70% approved, 20% pending, 10% rejected)
                int statusRand = random.nextInt(100);
                if (statusRand < 70 && admin != null) {
                    resource.approve(admin);
                } else if (statusRand < 90) {
                    // Leave as pending
                } else if (admin != null) {
                    String[] rejectionReasons = {
                        "Content not appropriate for academic use",
                        "Link is not accessible",
                        "Resource does not match course content",
                        "Duplicate resource already exists"
                    };
                    resource.reject(admin, rejectionReasons[random.nextInt(rejectionReasons.length)]);
                }
                
                resourceRepo.save(resource);
                resourceCount++;
                
                // Vary uploader occasionally
                if (random.nextInt(3) == 0 && students.size() > 1) {
                    uploader = students.get(random.nextInt(students.size()));
                }
            }
        }
        
        System.out.println("DataSeeder: Created " + resourceCount + " sample resources");
        System.out.println("DataSeeder: Resource seeding completed!");
    }
}