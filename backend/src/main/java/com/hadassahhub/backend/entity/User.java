package com.hadassahhub.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hadassahhub.backend.enums.UserRole;
import com.hadassahhub.backend.enums.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @Column(nullable = false)
    private Integer pointsBalance = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // User status management fields
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    
    @JsonIgnore
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;
    
    @JsonIgnore
    @Column(name = "blocked_by")
    private Long blockedBy;
    
    @JsonIgnore
    @Column(name = "block_reason", length = 500)
    private String blockReason;
    
    @JsonIgnore
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
    }

    public User(String email, String passwordHash, String displayName) {
        this();
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = UserRole.STUDENT;
        this.pointsBalance = 0;
    }

    public User(String email, String passwordHash, String displayName, UserRole role) {
        this(email, passwordHash, displayName);
        this.role = role;
    }
    
    // Business methods for status management
    public void block(Long adminId, String reason) {
        this.status = UserStatus.BLOCKED;
        this.blockedAt = LocalDateTime.now();
        this.blockedBy = adminId;
        this.blockReason = reason;
    }
    
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }
    
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.blockedAt = null;
        this.blockedBy = null;
        this.blockReason = null;
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
    
    public boolean isBlocked() {
        return this.status == UserStatus.BLOCKED;
    }
    
    public boolean isSuspended() {
        return this.status == UserStatus.SUSPENDED;
    }


    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public UserRole getRole() { return role; }
    public Integer getPointsBalance() { return pointsBalance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getBlockedAt() { return blockedAt; }
    public Long getBlockedBy() { return blockedBy; }
    public String getBlockReason() { return blockReason; }
    public LocalDateTime getLastLogin() { return lastLogin; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setRole(UserRole role) { this.role = role; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }
    public void setBlockedBy(Long blockedBy) { this.blockedBy = blockedBy; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}