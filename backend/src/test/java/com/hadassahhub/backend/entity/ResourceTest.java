package com.hadassahhub.backend.entity;

import com.hadassahhub.backend.enums.CourseCategory;
import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import com.hadassahhub.backend.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {

    private Course course;
    private User uploader;
    private User moderator;
    private Resource resource;

    @BeforeEach
    void setUp() {
        course = new Course("Test Course", "Test Description", CourseCategory.CS_CORE, 3, null);
        uploader = new User("student@edu.jmc.ac.il", "hashedPassword", "Test Student");
        moderator = new User("admin@edu.jmc.ac.il", "hashedPassword", "Test Admin", UserRole.ADMIN);
        
        resource = new Resource(course, uploader, "Test Resource", ResourceType.SUMMARY, "https://example.com/resource");
    }

    @Test
    void constructor_ShouldSetDefaultValues() {
        assertNotNull(resource.getCreatedAt());
        assertNotNull(resource.getUpdatedAt());
        assertEquals(ResourceStatus.PENDING, resource.getStatus());
        assertTrue(resource.isPending());
        assertFalse(resource.isApproved());
        assertFalse(resource.isRejected());
    }

    @Test
    void constructor_ShouldSetProvidedValues() {
        assertEquals(course, resource.getCourse());
        assertEquals(uploader, resource.getUploadedBy());
        assertEquals("Test Resource", resource.getTitle());
        assertEquals(ResourceType.SUMMARY, resource.getType());
        assertEquals("https://example.com/resource", resource.getUrl());
    }

    @Test
    void isOwnedBy_ShouldReturnTrueForOwner() {
        assertTrue(resource.isOwnedBy(uploader));
    }

    @Test
    void isOwnedBy_ShouldReturnFalseForNonOwner() {
        assertFalse(resource.isOwnedBy(moderator));
    }

    @Test
    void isOwnedBy_ShouldReturnFalseForNullUser() {
        assertFalse(resource.isOwnedBy(null));
    }

    @Test
    void approve_ShouldSetApprovedStatus() {
        LocalDateTime beforeApproval = LocalDateTime.now();
        
        resource.approve(moderator);
        
        assertEquals(ResourceStatus.APPROVED, resource.getStatus());
        assertEquals(moderator, resource.getApprovedBy());
        assertNotNull(resource.getApprovedAt());
        assertTrue(resource.getApprovedAt().isAfter(beforeApproval) || resource.getApprovedAt().isEqual(beforeApproval));
        assertNull(resource.getRejectionReason());
        assertTrue(resource.isApproved());
        assertFalse(resource.isPending());
        assertFalse(resource.isRejected());
    }

    @Test
    void reject_ShouldSetRejectedStatus() {
        String rejectionReason = "Inappropriate content";
        LocalDateTime beforeRejection = LocalDateTime.now();
        
        resource.reject(moderator, rejectionReason);
        
        assertEquals(ResourceStatus.REJECTED, resource.getStatus());
        assertEquals(moderator, resource.getApprovedBy());
        assertNotNull(resource.getApprovedAt());
        assertTrue(resource.getApprovedAt().isAfter(beforeRejection) || resource.getApprovedAt().isEqual(beforeRejection));
        assertEquals(rejectionReason, resource.getRejectionReason());
        assertTrue(resource.isRejected());
        assertFalse(resource.isPending());
        assertFalse(resource.isApproved());
    }

    @Test
    void approve_AfterRejection_ShouldClearRejectionReason() {
        // First reject
        resource.reject(moderator, "Initial rejection");
        assertEquals("Initial rejection", resource.getRejectionReason());
        
        // Then approve
        resource.approve(moderator);
        
        assertEquals(ResourceStatus.APPROVED, resource.getStatus());
        assertNull(resource.getRejectionReason());
    }

    @Test
    void setAcademicYear_ShouldSetValue() {
        resource.setAcademicYear("2024-2025");
        assertEquals("2024-2025", resource.getAcademicYear());
    }

    @Test
    void setExamTerm_ShouldSetValue() {
        resource.setExamTerm("Moed A");
        assertEquals("Moed A", resource.getExamTerm());
    }

    @Test
    void preUpdate_ShouldUpdateTimestamp() throws InterruptedException {
        LocalDateTime originalUpdatedAt = resource.getUpdatedAt();
        
        // Small delay to ensure timestamp difference
        Thread.sleep(1);
        
        resource.preUpdate();
        
        assertTrue(resource.getUpdatedAt().isAfter(originalUpdatedAt));
    }
}