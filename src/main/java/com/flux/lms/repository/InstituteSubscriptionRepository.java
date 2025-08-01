package com.flux.lms.repository;

import com.flux.lms.models.Course;
import com.flux.lms.models.Institute;
import com.flux.lms.models.InstituteSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstituteSubscriptionRepository extends JpaRepository<InstituteSubscription, Long> {
    
    Optional<InstituteSubscription> findByInstituteAndCourse(Institute institute, Course course);
    
    List<InstituteSubscription> findByInstitute(Institute institute);
    
    List<InstituteSubscription> findByCourse(Course course);
    
    @Query("SELECT s FROM InstituteSubscription s WHERE s.institute = :institute AND s.isActive = true")
    List<InstituteSubscription> findActiveByInstitute(@Param("institute") Institute institute);
    
    @Query("SELECT s FROM InstituteSubscription s WHERE s.course = :course AND s.isActive = true")
    List<InstituteSubscription> findActiveByCourse(@Param("course") Course course);
    
    @Query("SELECT s FROM InstituteSubscription s WHERE s.institute.instituteId = :instituteId AND s.isActive = true")
    List<InstituteSubscription> findActiveByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT s FROM InstituteSubscription s WHERE s.course.courseId = :courseId AND s.isActive = true")
    List<InstituteSubscription> findActiveByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT s FROM InstituteSubscription s WHERE s.expiryDate < :currentDate AND s.isActive = true")
    List<InstituteSubscription> findExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT s FROM InstituteSubscription s WHERE s.institute = :institute AND s.course = :course AND s.isActive = true")
    Optional<InstituteSubscription> findActiveByInstituteAndCourse(@Param("institute") Institute institute, @Param("course") Course course);
    
    boolean existsByInstituteAndCourseAndIsActiveTrue(Institute institute, Course course);
}