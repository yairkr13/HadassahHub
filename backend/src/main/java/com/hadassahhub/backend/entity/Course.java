package com.hadassahhub.backend.entity;

import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;
import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CourseCategory category;

    @Column(nullable = false)
    private Integer credits;

    @Enumerated(EnumType.STRING)
    @Column(name = "year")
    private StudyYear recommendedYear; // nullable for electives

    public Course() {}

    public Course(String name, String description, CourseCategory category, Integer credits, StudyYear recommendedYear) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.credits = credits;
        this.recommendedYear = recommendedYear;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public CourseCategory getCategory() { return category; }
    public Integer getCredits() { return credits; }
    public StudyYear getRecommendedYear() { return recommendedYear; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(CourseCategory category) { this.category = category; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public void setRecommendedYear(StudyYear recommendedYear) { this.recommendedYear = recommendedYear; }
}