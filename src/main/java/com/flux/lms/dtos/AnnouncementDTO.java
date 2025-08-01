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
public class AnnouncementDTO {
    private Long announcementId;
    private String title;
    private String content;
    private String type; // GENERAL, ACADEMIC, ADMINISTRATIVE, URGENT
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    
    // Author information
    private Long authorId;
    private String authorName;
    private String authorRole;
    
    // Institute relationship
    private Long instituteId;
    private String instituteName;
    
    // Target audience
    private List<String> targetRoles; // STUDENT, INSTRUCTOR, PARENT, ALL
    private List<Long> targetBatches;
    private List<Long> targetCourses;
    private List<Long> specificUsers;
    
    // Visibility and scheduling
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private boolean isPublished;
    private boolean isPinned;
    private boolean sendNotification;
    private boolean sendEmail;
    
    // Engagement metrics
    private Integer viewCount;
    private Integer acknowledgeCount;
    private List<Long> viewedByUsers;
    private List<Long> acknowledgedByUsers;
    
    // Attachments
    private List<String> attachmentUrls;
    private List<String> attachmentNames;
    
    // Status
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}