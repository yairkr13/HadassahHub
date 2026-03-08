package com.hadassahhub.backend.entity;

import com.hadassahhub.backend.enums.UserRole;
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

    public User() {
        this.createdAt = LocalDateTime.now();
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

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public UserRole getRole() { return role; }
    public Integer getPointsBalance() { return pointsBalance; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setRole(UserRole role) { this.role = role; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}