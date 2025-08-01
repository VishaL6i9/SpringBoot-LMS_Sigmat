package com.flux.lms.repository;

import com.flux.lms.models.Announcement;
import com.flux.lms.models.Institute;
import com.flux.lms.models.Role;
import com.flux.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    List<Announcement> findByInstitute(Institute institute);
    
    List<Announcement> findByInstituteAndIsActiveTrue(Institute institute);
    
    List<Announcement> findByAuthor(Users author);
    
    List<Announcement> findByType(Announcement.AnnouncementType type);
    
    List<Announcement> findByPriority(Announcement.AnnouncementPriority priority);
    
    List<Announcement> findByIsPublishedTrueAndIsActiveTrue();
    
    List<Announcement> findByIsPinnedTrueAndIsActiveTrue();
    
    @Query("SELECT a FROM Announcement a WHERE a.institute = :institute AND a.isPublished = true AND a.isActive = true AND (a.expiryDate IS NULL OR a.expiryDate > :now) ORDER BY a.isPinned DESC, a.priority DESC, a.publishDate DESC")
    List<Announcement> findActiveAnnouncementsByInstitute(@Param("institute") Institute institute, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.institute.instituteId = :instituteId AND a.isPublished = true AND a.isActive = true AND :role MEMBER OF a.targetRoles ORDER BY a.isPinned DESC, a.priority DESC, a.publishDate DESC")
    List<Announcement> findAnnouncementsForRole(@Param("instituteId") Long instituteId, @Param("role") Role role);
    
    @Query("SELECT a FROM Announcement a WHERE a.institute.instituteId = :instituteId AND a.isPublished = true AND a.isActive = true AND :batchId MEMBER OF a.targetBatches ORDER BY a.isPinned DESC, a.priority DESC, a.publishDate DESC")
    List<Announcement> findAnnouncementsForBatch(@Param("instituteId") Long instituteId, @Param("batchId") Long batchId);
    
    @Query("SELECT a FROM Announcement a WHERE a.institute.instituteId = :instituteId AND a.isPublished = true AND a.isActive = true AND :courseId MEMBER OF a.targetCourses ORDER BY a.isPinned DESC, a.priority DESC, a.publishDate DESC")
    List<Announcement> findAnnouncementsForCourse(@Param("instituteId") Long instituteId, @Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Announcement a WHERE a.institute.instituteId = :instituteId AND a.isPublished = true AND a.isActive = true AND :userId MEMBER OF a.specificUsers ORDER BY a.isPinned DESC, a.priority DESC, a.publishDate DESC")
    List<Announcement> findAnnouncementsForUser(@Param("instituteId") Long instituteId, @Param("userId") Long userId);
    
    @Query("SELECT a FROM Announcement a WHERE a.institute.instituteId = :instituteId AND a.isPublished = true AND a.isActive = true AND (a.expiryDate IS NULL OR a.expiryDate > :now) AND (:role MEMBER OF a.targetRoles OR :batchId MEMBER OF a.targetBatches OR :userId MEMBER OF a.specificUsers OR SIZE(a.targetRoles) = 0) ORDER BY a.isPinned DESC, a.priority DESC, a.publishDate DESC")
    List<Announcement> findRelevantAnnouncementsForUser(@Param("instituteId") Long instituteId, @Param("userId") Long userId, @Param("role") Role role, @Param("batchId") Long batchId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.publishDate <= :now AND a.isPublished = false")
    List<Announcement> findAnnouncementsToPublish(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.expiryDate <= :now AND a.isActive = true")
    List<Announcement> findExpiredAnnouncements(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.institute.instituteId = :instituteId AND a.isActive = true")
    Long countByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.author.id = :authorId AND a.isActive = true")
    Long countByAuthorId(@Param("authorId") Long authorId);
}