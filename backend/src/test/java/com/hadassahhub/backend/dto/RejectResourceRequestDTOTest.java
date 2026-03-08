package com.hadassahhub.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RejectResourceRequestDTOTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testValidRejectRequest() {
        RejectResourceRequestDTO dto = new RejectResourceRequestDTO("Content is not appropriate");
        
        Set<ConstraintViolation<RejectResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        
        assertEquals("Content is not appropriate", dto.reason());
    }
    
    @Test
    void testBlankReason() {
        RejectResourceRequestDTO dto = new RejectResourceRequestDTO("");
        
        Set<ConstraintViolation<RejectResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rejection reason is required")));
    }
    
    @Test
    void testNullReason() {
        RejectResourceRequestDTO dto = new RejectResourceRequestDTO(null);
        
        Set<ConstraintViolation<RejectResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rejection reason is required")));
    }
    
    @Test
    void testReasonTooLong() {
        String longReason = "a".repeat(501);
        RejectResourceRequestDTO dto = new RejectResourceRequestDTO(longReason);
        
        Set<ConstraintViolation<RejectResourceRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rejection reason must not exceed 500 characters")));
    }
    
    @Test
    void testMaxLengthReason() {
        String maxReason = "a".repeat(500);
        RejectResourceRequestDTO dto = new RejectResourceRequestDTO(maxReason);
        
        Set<ConstraintViolation<RejectResourceRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals(maxReason, dto.reason());
    }
    
    @Test
    void testWhitespaceNormalization() {
        RejectResourceRequestDTO dto = new RejectResourceRequestDTO("  Content is inappropriate  ");
        
        assertEquals("Content is inappropriate", dto.reason());
    }
    
    @Test
    void testWithReasonFactory() {
        RejectResourceRequestDTO dto = RejectResourceRequestDTO.withReason("Test reason");
        
        assertEquals("Test reason", dto.reason());
    }
    
    @Test
    void testCommonReasons() {
        // Test that common reasons are defined and not empty
        assertNotNull(RejectResourceRequestDTO.CommonReasons.INAPPROPRIATE_CONTENT);
        assertFalse(RejectResourceRequestDTO.CommonReasons.INAPPROPRIATE_CONTENT.isEmpty());
        
        assertNotNull(RejectResourceRequestDTO.CommonReasons.BROKEN_LINK);
        assertFalse(RejectResourceRequestDTO.CommonReasons.BROKEN_LINK.isEmpty());
        
        assertNotNull(RejectResourceRequestDTO.CommonReasons.WRONG_COURSE);
        assertFalse(RejectResourceRequestDTO.CommonReasons.WRONG_COURSE.isEmpty());
        
        assertNotNull(RejectResourceRequestDTO.CommonReasons.DUPLICATE_RESOURCE);
        assertFalse(RejectResourceRequestDTO.CommonReasons.DUPLICATE_RESOURCE.isEmpty());
        
        assertNotNull(RejectResourceRequestDTO.CommonReasons.POOR_QUALITY);
        assertFalse(RejectResourceRequestDTO.CommonReasons.POOR_QUALITY.isEmpty());
        
        assertNotNull(RejectResourceRequestDTO.CommonReasons.COPYRIGHT_VIOLATION);
        assertFalse(RejectResourceRequestDTO.CommonReasons.COPYRIGHT_VIOLATION.isEmpty());
        
        assertNotNull(RejectResourceRequestDTO.CommonReasons.SPAM_OR_IRRELEVANT);
        assertFalse(RejectResourceRequestDTO.CommonReasons.SPAM_OR_IRRELEVANT.isEmpty());
    }
    
    @Test
    void testCommonReasonsAreValid() {
        // Test that all common reasons pass validation
        String[] commonReasons = {
            RejectResourceRequestDTO.CommonReasons.INAPPROPRIATE_CONTENT,
            RejectResourceRequestDTO.CommonReasons.BROKEN_LINK,
            RejectResourceRequestDTO.CommonReasons.WRONG_COURSE,
            RejectResourceRequestDTO.CommonReasons.DUPLICATE_RESOURCE,
            RejectResourceRequestDTO.CommonReasons.POOR_QUALITY,
            RejectResourceRequestDTO.CommonReasons.COPYRIGHT_VIOLATION,
            RejectResourceRequestDTO.CommonReasons.SPAM_OR_IRRELEVANT
        };
        
        for (String reason : commonReasons) {
            RejectResourceRequestDTO dto = new RejectResourceRequestDTO(reason);
            Set<ConstraintViolation<RejectResourceRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Common reason should be valid: " + reason);
            assertTrue(reason.length() <= 500, "Common reason should not exceed max length: " + reason);
        }
    }
}