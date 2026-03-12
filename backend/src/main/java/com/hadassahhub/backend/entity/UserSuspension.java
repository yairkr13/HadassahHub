package com.hadassahhub.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a user suspension record.
 * Tracks suspension history with expiration dates and audit trail.
 */
@Entity
@Table(name = "user_suspensions")
public class UserSuspension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "suspended_at", nullable = false)
    private LocalDateTime suspendedAt;

    @Column(name = "suspended_by", nullable = false)
    private Long suspendedBy;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "reason", length = 500, nullable = false)
    private String reason;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "lifted_at")
    private LocalDateTime liftedAt;

    @Column(name = "lifted_by")
    private Long liftedBy;

    // Constructors
    public UserSuspension() {
    }

    public UserSuspension(User user, Long suspendedBy, LocalDateTime expiresAt, String reason) {
        this.user = user;
        this.suspendedBy = suspendedBy;
        this.suspendedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.reason = reason;
        this.isActive = true;
    }

    // Business methods
    
    /**
     * Lift the suspension manually by an admin.
     * @param adminId The ID of the admin lifting the suspension
     */
    public void lift(Long adminId) {
        this.isActive = false;
        this.liftedAt = LocalDateTime.now();
        this.liftedBy = adminId;
    }

    /**
     * Check if the suspension has expired.
     * @return true if the current time is past the expiration date
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getSuspendedAt() {
        return suspendedAt;
    }

    public Long getSuspendedBy() {
        return suspendedBy;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getReason() {
        return reason;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getLiftedAt() {
        return liftedAt;
    }

    public Long getLiftedBy() {
        return liftedBy;
    }

    // Setters
    public void setUser(User user) {
        this.user = user;
    }

    public void setSuspendedAt(LocalDateTime suspendedAt) {
        this.suspendedAt = suspendedAt;
    }

    public void setSuspendedBy(Long suspendedBy) {
        this.suspendedBy = suspendedBy;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setLiftedAt(LocalDateTime liftedAt) {
        this.liftedAt = liftedAt;
    }

    public void setLiftedBy(Long liftedBy) {
        this.liftedBy = liftedBy;
    }
}
