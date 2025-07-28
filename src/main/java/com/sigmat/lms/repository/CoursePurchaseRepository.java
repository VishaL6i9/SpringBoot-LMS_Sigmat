package com.sigmat.lms.repository;

import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.CoursePurchase;
import com.sigmat.lms.models.PurchaseStatus;
import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoursePurchaseRepository extends JpaRepository<CoursePurchase, Long> {
    
    List<CoursePurchase> findByUserOrderByPurchaseDateDesc(Users user);
    
    List<CoursePurchase> findByCourseOrderByPurchaseDateDesc(Course course);
    
    Optional<CoursePurchase> findByUserAndCourse(Users user, Course course);
    
    Optional<CoursePurchase> findByUserAndCourseAndStatus(Users user, Course course, PurchaseStatus status);
    
    Optional<CoursePurchase> findByStripeSessionId(String stripeSessionId);
    
    List<CoursePurchase> findByStatus(PurchaseStatus status);
    
    @Query("SELECT cp FROM CoursePurchase cp WHERE cp.user = :user AND cp.status = 'COMPLETED'")
    List<CoursePurchase> findCompletedPurchasesByUser(@Param("user") Users user);
    
    @Query("SELECT cp FROM CoursePurchase cp WHERE cp.course = :course AND cp.status = 'COMPLETED'")
    List<CoursePurchase> findCompletedPurchasesByCourse(@Param("course") Course course);
    
    @Query("SELECT COUNT(cp) FROM CoursePurchase cp WHERE cp.course = :course AND cp.status = 'COMPLETED'")
    Long countCompletedPurchasesByCourse(@Param("course") Course course);
    
    @Query("SELECT cp FROM CoursePurchase cp WHERE cp.purchaseDate BETWEEN :startDate AND :endDate AND cp.status = 'COMPLETED'")
    List<CoursePurchase> findCompletedPurchasesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    boolean existsByUserAndCourseAndStatus(Users user, Course course, PurchaseStatus status);
}