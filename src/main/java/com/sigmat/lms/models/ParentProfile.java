package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parent_profiles")
public class ParentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Long parentId;

    // Parent details
    @Column(nullable = false)
    private String parentName;

    @Column(unique = true, nullable = false)
    private String parentEmail;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String relationship; // Father, Mother, Guardian

    // Student relationship (one parent can have multiple children)
    @JsonIgnore
    @OneToMany(mappedBy = "parentProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Users> children;

    // Institute relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    // Access permissions (view-only)
    @Builder.Default
    @Column(name = "can_view_reports")
    private boolean canViewReports = true;

    @Builder.Default
    @Column(name = "can_view_attendance")
    private boolean canViewAttendance = true;

    @Builder.Default
    @Column(name = "can_view_grades")
    private boolean canViewGrades = true;

    @Builder.Default
    @Column(name = "can_view_announcements")
    private boolean canViewAnnouncements = true;

    @Builder.Default
    @Column(name = "can_receive_notifications")
    private boolean canReceiveNotifications = true;

    // Communication preferences
    @Builder.Default
    @Column(name = "email_notifications")
    private boolean emailNotifications = true;

    @Builder.Default
    @Column(name = "sms_notifications")
    private boolean smsNotifications = false;

    @Builder.Default
    @Column(name = "preferred_language")
    private String preferredLanguage = "en";

    // Login credentials (optional - parents can access via unique link)
    private String accessToken;
    private LocalDateTime tokenExpiry;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    // Status
    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentProfile that = (ParentProfile) o;
        return parentId != null && parentId.equals(that.parentId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}