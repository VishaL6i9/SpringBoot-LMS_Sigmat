package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long announcementId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Builder.Default
    private AnnouncementType type = AnnouncementType.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    @Builder.Default
    private AnnouncementPriority priority = AnnouncementPriority.MEDIUM;

    // Author information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    // Institute relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    // Target audience
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "announcement_target_roles", joinColumns = @JoinColumn(name = "announcement_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> targetRoles;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_target_batches", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "batch_id")
    private Set<Long> targetBatches;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_target_courses", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "course_id")
    private Set<Long> targetCourses;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_specific_users", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "user_id")
    private Set<Long> specificUsers;

    // Visibility and scheduling
    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Builder.Default
    @Column(name = "is_published")
    private boolean isPublished = false;

    @Builder.Default
    @Column(name = "is_pinned")
    private boolean isPinned = false;

    @Builder.Default
    @Column(name = "send_notification")
    private boolean sendNotification = true;

    @Builder.Default
    @Column(name = "send_email")
    private boolean sendEmail = false;

    // Engagement metrics
    @Builder.Default
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "acknowledge_count")
    private Integer acknowledgeCount = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_views", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "user_id")
    private Set<Long> viewedByUsers;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_acknowledgments", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "user_id")
    private Set<Long> acknowledgedByUsers;

    // Attachments
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_attachments", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "attachment_url")
    private List<String> attachmentUrls;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "announcement_attachment_names", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "attachment_name")
    private List<String> attachmentNames;

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
        if (publishDate == null) {
            publishDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AnnouncementType {
        GENERAL,
        ACADEMIC,
        ADMINISTRATIVE,
        URGENT,
        EVENT,
        HOLIDAY,
        EXAM,
        ASSIGNMENT
    }

    public enum AnnouncementPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return announcementId != null && announcementId.equals(that.announcementId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}