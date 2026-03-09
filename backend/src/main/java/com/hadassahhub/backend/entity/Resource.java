package com.hadassahhub.backend.entity;

import com.hadassahhub.backend.enums.ResourceStatus;
import com.hadassahhub.backend.enums.ResourceType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing an educational resource linked to a course.
 * Currently supports URL-based resources with future extensibility for file uploads.
 */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    @Column(nullable = true)
    private String url; // External URL for URL resources, null for file uploads

    // File upload fields
    @Column(name = "is_file_upload", nullable = false)
    private Boolean isFileUpload = false;

    @Column(name = "file_name")
    private String fileName; // Original filename for display

    @Column(name = "file_path")
    private String filePath; // Stored path (UUID-based)

    @Column(name = "file_size")
    private Long fileSize; // Size in bytes

    @Column(name = "mime_type")
    private String mimeType; // Content type

    @Column(name = "academic_year")
    private String academicYear; // e.g., "2024-2025"

    @Column(name = "exam_term")
    private String examTerm; // e.g., "Moed A", "Moed B"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Resource() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ResourceStatus.PENDING;
    }

    public Resource(Course course, User uploadedBy, String title, ResourceType type, String url) {
        this();
        this.course = course;
        this.uploadedBy = uploadedBy;
        this.title = title;
        this.type = type;
        this.url = url;
    }

    // JPA lifecycle methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getUploadedBy() { return uploadedBy; }
    public String getTitle() { return title; }
    public ResourceType getType() { return type; }
    public String getUrl() { return url; }
    public Boolean getIsFileUpload() { return isFileUpload; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public Long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public String getAcademicYear() { return academicYear; }
    public String getExamTerm() { return examTerm; }
    public ResourceStatus getStatus() { return status; }
    public User getApprovedBy() { return approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setCourse(Course course) { this.course = course; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
    public void setTitle(String title) { this.title = title; }
    public void setType(ResourceType type) { this.type = type; }
    public void setUrl(String url) { this.url = url; }
    public void setIsFileUpload(Boolean isFileUpload) { this.isFileUpload = isFileUpload; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public void setExamTerm(String examTerm) { this.examTerm = examTerm; }
    public void setStatus(ResourceStatus status) { this.status = status; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business methods
    public boolean isPending() {
        return this.status == ResourceStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ResourceStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == ResourceStatus.REJECTED;
    }

    public boolean isFileResource() {
        return Boolean.TRUE.equals(this.isFileUpload);
    }

    public boolean isUrlResource() {
        return !Boolean.TRUE.equals(this.isFileUpload);
    }

    public boolean isOwnedBy(User user) {
        if (this.uploadedBy == null || user == null) {
            return false;
        }
        
        // Handle case where entities don't have IDs yet (not persisted)
        if (this.uploadedBy.getId() == null || user.getId() == null) {
            return this.uploadedBy.equals(user);
        }
        
        return this.uploadedBy.getId().equals(user.getId());
    }

    public void approve(User moderator) {
        this.status = ResourceStatus.APPROVED;
        this.approvedBy = moderator;
        this.approvedAt = LocalDateTime.now();
        this.rejectionReason = null; // Clear any previous rejection reason
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(User moderator, String reason) {
        this.status = ResourceStatus.REJECTED;
        this.approvedBy = moderator;
        this.approvedAt = LocalDateTime.now();
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
}