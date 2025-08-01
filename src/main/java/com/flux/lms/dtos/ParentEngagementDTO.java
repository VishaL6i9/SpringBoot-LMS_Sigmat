package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentEngagementDTO {
    private Long parentId;
    private String parentName;
    private String parentEmail;
    private String phoneNumber;
    private String relationship; // Father, Mother, Guardian
    
    // Student relationship
    private Long studentId;
    private String studentName;
    private String rollNumber;
    
    // Institute relationship
    private Long instituteId;
    private String instituteName;
    
    // Access permissions (view-only)
    private boolean canViewReports;
    private boolean canViewAttendance;
    private boolean canViewGrades;
    private boolean canViewAnnouncements;
    private boolean canReceiveNotifications;
    
    // Communication preferences
    private boolean emailNotifications;
    private boolean smsNotifications;
    private String preferredLanguage;
    
    // Recent activities
    private List<StudentProgressDTO> recentProgress;
    private List<AnnouncementDTO> recentAnnouncements;
    private LocalDateTime lastLoginDate;
    
    // Status
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}