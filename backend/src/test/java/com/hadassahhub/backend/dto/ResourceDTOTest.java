package com.hadassahhub.backend.dto;

import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDTOTest {
    
    @Test
    void testFullResourceDTO() {
        LocalDateTime now = LocalDateTime.now();
        
        ResourceDTO dto = new ResourceDTO(
            1L, "Final Exam 2024", ResourceType.EXAM, "https://example.com/exam.pdf",
            "2024-2025", "Moed A", ResourceStatus.APPROVED, null,
            100L, "Computer Science", 200L, "John Doe",
            300L, "Admin User", now, now.minusDays(1), now
        );
        
        assertEquals(1L, dto.id());
        assertEquals("Final Exam 2024", dto.title());
        assertEquals(ResourceType.EXAM, dto.type());
        assertEquals("https://example.com/exam.pdf", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
        assertEquals(ResourceStatus.APPROVED, dto.status());
        assertNull(dto.rejectionReason());
        assertEquals(100L, dto.courseId());
        assertEquals("Computer Science", dto.courseName());
        assertEquals(200L, dto.uploadedById());
        assertEquals("John Doe", dto.uploaderName());
        assertEquals(300L, dto.approvedById());
        assertEquals("Admin User", dto.approverName());
        assertEquals(now, dto.approvedAt());
        assertEquals(now.minusDays(1), dto.createdAt());
        assertEquals(now, dto.updatedAt());
    }
    
    @Test
    void testForPublicView() {
        LocalDateTime now = LocalDateTime.now();
        
        ResourceDTO dto = ResourceDTO.forPublicView(
            1L, "Final Exam 2024", ResourceType.EXAM, "https://example.com/exam.pdf",
            "2024-2025", "Moed A", "John Doe", now
        );
        
        assertEquals(1L, dto.id());
        assertEquals("Final Exam 2024", dto.title());
        assertEquals(ResourceType.EXAM, dto.type());
        assertEquals("https://example.com/exam.pdf", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertEquals("Moed A", dto.examTerm());
        assertEquals(ResourceStatus.APPROVED, dto.status());
        assertNull(dto.rejectionReason());
        assertNull(dto.courseId());
        assertNull(dto.courseName());
        assertNull(dto.uploadedById());
        assertEquals("John Doe", dto.uploaderName());
        assertNull(dto.approvedById());
        assertNull(dto.approverName());
        assertNull(dto.approvedAt());
        assertEquals(now, dto.createdAt());
        assertNull(dto.updatedAt());
    }
    
    @Test
    void testForOwnerView() {
        LocalDateTime now = LocalDateTime.now();
        
        ResourceDTO dto = ResourceDTO.forOwnerView(
            1L, "Assignment 1", ResourceType.HOMEWORK, "https://example.com/hw1.pdf",
            "2024-2025", null, ResourceStatus.REJECTED, "Content not appropriate",
            "Computer Science", now.minusDays(1), now
        );
        
        assertEquals(1L, dto.id());
        assertEquals("Assignment 1", dto.title());
        assertEquals(ResourceType.HOMEWORK, dto.type());
        assertEquals("https://example.com/hw1.pdf", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertNull(dto.examTerm());
        assertEquals(ResourceStatus.REJECTED, dto.status());
        assertEquals("Content not appropriate", dto.rejectionReason());
        assertNull(dto.courseId());
        assertEquals("Computer Science", dto.courseName());
        assertNull(dto.uploadedById());
        assertNull(dto.uploaderName());
        assertNull(dto.approvedById());
        assertNull(dto.approverName());
        assertNull(dto.approvedAt());
        assertEquals(now.minusDays(1), dto.createdAt());
        assertEquals(now, dto.updatedAt());
    }
    
    @Test
    void testForAdminView() {
        LocalDateTime now = LocalDateTime.now();
        
        ResourceDTO dto = ResourceDTO.forAdminView(
            1L, "Course Summary", ResourceType.SUMMARY, "https://example.com/summary.pdf",
            "2024-2025", null, ResourceStatus.PENDING, null,
            100L, "Computer Science", 200L, "Jane Smith",
            null, null, null, now.minusDays(1), now
        );
        
        assertEquals(1L, dto.id());
        assertEquals("Course Summary", dto.title());
        assertEquals(ResourceType.SUMMARY, dto.type());
        assertEquals("https://example.com/summary.pdf", dto.url());
        assertEquals("2024-2025", dto.academicYear());
        assertNull(dto.examTerm());
        assertEquals(ResourceStatus.PENDING, dto.status());
        assertNull(dto.rejectionReason());
        assertEquals(100L, dto.courseId());
        assertEquals("Computer Science", dto.courseName());
        assertEquals(200L, dto.uploadedById());
        assertEquals("Jane Smith", dto.uploaderName());
        assertNull(dto.approvedById());
        assertNull(dto.approverName());
        assertNull(dto.approvedAt());
        assertEquals(now.minusDays(1), dto.createdAt());
        assertEquals(now, dto.updatedAt());
    }
    
    @Test
    void testStatusCheckers() {
        ResourceDTO pendingDto = ResourceDTO.forOwnerView(
            1L, "Test", ResourceType.EXAM, "https://example.com", null, null,
            ResourceStatus.PENDING, null, "Course", LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertTrue(pendingDto.isPending());
        assertFalse(pendingDto.isApproved());
        assertFalse(pendingDto.isRejected());
        
        ResourceDTO approvedDto = ResourceDTO.forOwnerView(
            1L, "Test", ResourceType.EXAM, "https://example.com", null, null,
            ResourceStatus.APPROVED, null, "Course", LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertFalse(approvedDto.isPending());
        assertTrue(approvedDto.isApproved());
        assertFalse(approvedDto.isRejected());
        
        ResourceDTO rejectedDto = ResourceDTO.forOwnerView(
            1L, "Test", ResourceType.EXAM, "https://example.com", null, null,
            ResourceStatus.REJECTED, "Bad content", "Course", LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertFalse(rejectedDto.isPending());
        assertFalse(rejectedDto.isApproved());
        assertTrue(rejectedDto.isRejected());
    }
}