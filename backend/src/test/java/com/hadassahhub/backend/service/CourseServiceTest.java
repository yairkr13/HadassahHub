package com.hadassahhub.backend.service;

import com.hadassahhub.backend.dto.CourseDTO;
import com.hadassahhub.backend.entity.Course;
import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.StudyYear;
import com.hadassahhub.backend.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course csCoreCourse;
    private Course csElectiveCourse;
    private Course generalElectiveCourse;

    @BeforeEach
    void setUp() {
        // Create test courses
        csCoreCourse = new Course(
                "Data Structures",
                "Arrays, linked lists, stacks, queues, trees, and graphs",
                CourseCategory.CS_CORE,
                4,
                StudyYear.Y1
        );
        ReflectionTestUtils.setField(csCoreCourse, "id", 1L);

        csElectiveCourse = new Course(
                "Web Development",
                "Modern web technologies and frameworks",
                CourseCategory.CS_ELECTIVE,
                3,
                null
        );
        ReflectionTestUtils.setField(csElectiveCourse, "id", 2L);

        generalElectiveCourse = new Course(
                "Psychology",
                "Introduction to psychological principles",
                CourseCategory.GENERAL_ELECTIVE,
                2,
                null
        );
        ReflectionTestUtils.setField(generalElectiveCourse, "id", 3L);
    }

    @Test
    @DisplayName("Should return all courses when no filters provided")
    void findCourses_NoFilters_ReturnsAllCourses() {
        // Given
        List<Course> allCourses = Arrays.asList(csCoreCourse, csElectiveCourse, generalElectiveCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(allCourses);

        // When
        List<CourseDTO> result = courseService.findCourses(null, null, null);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).name()).isEqualTo("Data Structures");
        assertThat(result.get(1).name()).isEqualTo("Web Development");
        assertThat(result.get(2).name()).isEqualTo("Psychology");
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should filter courses by search term")
    void findCourses_WithSearchTerm_ReturnsFilteredCourses() {
        // Given
        List<Course> filteredCourses = Arrays.asList(csCoreCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(filteredCourses);

        // When
        List<CourseDTO> result = courseService.findCourses("data", null, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Data Structures");
        assertThat(result.get(0).category()).isEqualTo(CourseCategory.CS_CORE);
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should filter courses by category")
    void findCourses_WithCategory_ReturnsFilteredCourses() {
        // Given
        List<Course> csCourses = Arrays.asList(csCoreCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(csCourses);

        // When
        List<CourseDTO> result = courseService.findCourses(null, CourseCategory.CS_CORE, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo(CourseCategory.CS_CORE);
        assertThat(result.get(0).recommendedYear()).isEqualTo(StudyYear.Y1);
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should filter courses by recommended year")
    void findCourses_WithYear_ReturnsFilteredCourses() {
        // Given
        List<Course> y1Courses = Arrays.asList(csCoreCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(y1Courses);

        // When
        List<CourseDTO> result = courseService.findCourses(null, null, StudyYear.Y1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).recommendedYear()).isEqualTo(StudyYear.Y1);
        assertThat(result.get(0).category()).isEqualTo(CourseCategory.CS_CORE);
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should apply multiple filters with AND logic")
    void findCourses_WithMultipleFilters_ReturnsFilteredCourses() {
        // Given
        List<Course> filteredCourses = Arrays.asList(csCoreCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(filteredCourses);

        // When
        List<CourseDTO> result = courseService.findCourses("data", CourseCategory.CS_CORE, StudyYear.Y1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).contains("Data");
        assertThat(result.get(0).category()).isEqualTo(CourseCategory.CS_CORE);
        assertThat(result.get(0).recommendedYear()).isEqualTo(StudyYear.Y1);
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should return empty list when no courses match filters")
    void findCourses_NoMatches_ReturnsEmptyList() {
        // Given
        when(courseRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        // When
        List<CourseDTO> result = courseService.findCourses("nonexistent", null, null);

        // Then
        assertThat(result).isEmpty();
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should find course by valid ID")
    void findCourseById_ValidId_ReturnsCourseDTO() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(csCoreCourse));

        // When
        Optional<CourseDTO> result = courseService.findCourseById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Data Structures");
        assertThat(result.get().category()).isEqualTo(CourseCategory.CS_CORE);
        assertThat(result.get().credits()).isEqualTo(4);
        assertThat(result.get().recommendedYear()).isEqualTo(StudyYear.Y1);
        
        verify(courseRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when course ID not found")
    void findCourseById_InvalidId_ReturnsEmpty() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<CourseDTO> result = courseService.findCourseById(999L);

        // Then
        assertThat(result).isEmpty();
        
        verify(courseRepository).findById(999L);
    }

    @Test
    @DisplayName("Should correctly map Course entity to CourseDTO")
    void toDTO_ValidCourse_ReturnsMappedDTO() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(csCoreCourse));

        // When
        Optional<CourseDTO> result = courseService.findCourseById(1L);

        // Then
        assertThat(result).isPresent();
        CourseDTO dto = result.get();
        assertThat(dto.id()).isEqualTo(csCoreCourse.getId());
        assertThat(dto.name()).isEqualTo(csCoreCourse.getName());
        assertThat(dto.description()).isEqualTo(csCoreCourse.getDescription());
        assertThat(dto.category()).isEqualTo(csCoreCourse.getCategory());
        assertThat(dto.credits()).isEqualTo(csCoreCourse.getCredits());
        assertThat(dto.recommendedYear()).isEqualTo(csCoreCourse.getRecommendedYear());
    }

    @Test
    @DisplayName("Should handle elective courses with null recommended year")
    void toDTO_ElectiveCourse_HandlesNullRecommendedYear() {
        // Given
        when(courseRepository.findById(2L)).thenReturn(Optional.of(csElectiveCourse));

        // When
        Optional<CourseDTO> result = courseService.findCourseById(2L);

        // Then
        assertThat(result).isPresent();
        CourseDTO dto = result.get();
        assertThat(dto.category()).isEqualTo(CourseCategory.CS_ELECTIVE);
        assertThat(dto.recommendedYear()).isNull();
        assertThat(dto.credits()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should handle empty search string as no filter")
    void findCourses_EmptySearch_TreatedAsNoFilter() {
        // Given
        List<Course> allCourses = Arrays.asList(csCoreCourse, csElectiveCourse, generalElectiveCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(allCourses);

        // When
        List<CourseDTO> result = courseService.findCourses("", null, null);

        // Then
        assertThat(result).hasSize(3);
        
        verify(courseRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should handle whitespace-only search string as no filter")
    void findCourses_WhitespaceSearch_TreatedAsNoFilter() {
        // Given
        List<Course> allCourses = Arrays.asList(csCoreCourse, csElectiveCourse, generalElectiveCourse);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(allCourses);

        // When
        List<CourseDTO> result = courseService.findCourses("   ", null, null);

        // Then
        assertThat(result).hasSize(3);
        
        verify(courseRepository).findAll(any(Specification.class));
    }
}