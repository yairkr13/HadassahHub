package com.hadassahhub.backend.entity;

import com.hadassahhub.backend.enums.Semester;
import com.hadassahhub.backend.enums.StudyYear;
import jakarta.persistence.*;

@Entity
@Table(
        name = "course_offerings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "year", "semester"})
)
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyYear year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    // --- Constructors ---
    public CourseOffering() {}

    public CourseOffering(Course course, StudyYear year, Semester semester) {
        this.course = course;
        this.year = year;
        this.semester = semester;
    }

    // --- Getters/Setters ---
    public Long getId() { return id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public StudyYear getYear() { return year; }
    public void setYear(StudyYear year) { this.year = year; }

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }
}