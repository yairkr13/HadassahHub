package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateResourceRequestDTOTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testValidResourceRequest() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Final Exam 2024", ResourceType.EXAM, "https://example.com/exam.pdf",
            "2024-2025", "Moed A"
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        
        assertEquals(1L, dto.courseId());
        assertEquals("Final Exam 2024", dto.title());
        assertEquals(ResourceType.EXAM, dto.type());
        assertEquals("https://example.com/exam.pdf", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
    }
    
    @Test
    void testNullCourseId() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            null, "Test", ResourceType.EXAM, "https://example.com", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Course ID is required")));
    }
    
    @Test
    void testBlankTitle() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "", ResourceType.EXAM, "https://example.com", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
    }
    
    @Test
    void testNullTitle() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, null, ResourceType.EXAM, "https://example.com", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
    }
    
    @Test
    void testTitleTooLong() {
        String longTitle = "a".repeat(256);
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, longTitle, ResourceType.EXAM, "https://example.com", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title must not exceed 255 characters")));
    }
    
    @Test
    void testNullResourceType() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", null, "https://example.com", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Resource type is required")));
    }
    
    @Test
    void testBlankUrl() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("URL is required") || v.getMessage().contains("URL must be a valid HTTP or HTTPS URL")));
    }
    
    @Test
    void testNullUrl() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, null, null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("URL is required")));
    }
    
    @Test
    void testInvalidUrlFormat() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "not-a-url", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("URL must be a valid HTTP or HTTPS URL")));
    }
    
    @Test
    void testValidHttpsUrl() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com/file.pdf", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testValidHttpUrl() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "http://example.com/file.pdf", null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testUrlTooLong() {
        String longUrl = "https://example.com/" + "a".repeat(2048);
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, longUrl, null, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("URL must not exceed 2048 characters")));
    }
    
    @Test
    void testInvalidAcademicYearFormat() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", "2024", null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Academic year must be in format YYYY-YYYY")));
    }
    
    @Test
    void testValidAcademicYearFormat() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", "2024-2025", null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void testEmptyAcademicYearIsValid() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", "", null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertNull(dto.academicYear()); // Should be normalized to null
    }
    
    @Test
    void testAcademicYearTooLong() {
        String longYear = "a".repeat(51);
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", longYear, null
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Academic year must not exceed 50 characters") || v.getMessage().contains("Academic year must be in format YYYY-YYYY")));
    }
    
    @Test
    void testExamTermTooLong() {
        String longTerm = "a".repeat(51);
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", null, longTerm
        );
        
        Set<ConstraintViolation<CreateResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Exam term must not exceed 50 characters")));
    }
    
    @Test
    void testWhitespaceNormalization() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "  Test Title  ", ResourceType.EXAM, "  https://example.com  ",
            "  2024-2025  ", "  Moed A  "
        );
        
        assertEquals("Test Title", dto.title());
        assertEquals("https://example.com", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
    }
    
    @Test
    void testEmptyStringNormalization() {
        CreateResourceRequestDTO dto = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", "   ", "   "
        );
        
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
    }
    
    @Test
    void testIsValidExamResource() {
        // Valid exam resource with academic year
        CreateResourceRequestDTO validExam = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", "2024-2025", "Moed A"
        );
        assertTrue(validExam.isValidExamResource());
        
        // Invalid exam resource without academic year
        CreateResourceRequestDTO invalidExam = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.EXAM, "https://example.com", null, "Moed A"
        );
        assertFalse(invalidExam.isValidExamResource());
        
        // Non-exam resource (always valid)
        CreateResourceRequestDTO homework = new CreateResourceRequestDTO(
            1L, "Test", ResourceType.HOMEWORK, "https://example.com", null, null
        );
        assertTrue(homework.isValidExamResource());
    }
    
    @Test
    void testSimpleFactory() {
        CreateResourceRequestDTO dto = CreateResourceRequestDTO.simple(
            1L, "Test", ResourceType.HOMEWORK, "https://example.com"
        );
        
        assertEquals(1L, dto.courseId());
        assertEquals("Test", dto.title());
        assertEquals(ResourceType.HOMEWORK, dto.type());
        assertEquals("https://example.com", dto.url());
        assertNull(dto.academicYear());
        assertNull(dto.examTerm());
    }
    
    @Test
    void testExamFactory() {
        CreateResourceRequestDTO dto = CreateResourceRequestDTO.exam(
            1L, "Final Exam", "https://example.com/exam.pdf", "2024-2025", "Moed A"
        );
        
        assertEquals(1L, dto.courseId());
        assertEquals("Final Exam", dto.title());
        assertEquals(ResourceType.EXAM, dto.type());
        assertEquals("https://example.com/exam.pdf", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
    }
}