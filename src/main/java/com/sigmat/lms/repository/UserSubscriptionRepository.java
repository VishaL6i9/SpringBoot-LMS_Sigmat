package com.sigmat.lms.repository;

import com.sigmat.lms.models.SubscriptionStatus;
import com.sigmat.lms.models.UserSubscription;
import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    
    List<UserSubscription> findByUserOrderByCreatedAtDesc(Users user);
    
    Optional<UserSubscription> findByUserAndStatus(Users user, SubscriptionStatus status);
    
    List<UserSubscription> findByUserAndStatusOrderByCreatedAtDesc(Users user, SubscriptionStatus status);
    
    @Query("SELECT us FROM UserSubscription us WHERE us.user = :user AND us.status = 'ACTIVE' AND us.endDate > :currentDate")
    Optional<UserSubscription> findActiveSubscriptionByUser(@Param("user") Users user, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT us FROM UserSubscription us WHERE us.endDate < :currentDate AND us.status = 'ACTIVE'")
    List<UserSubscription> findExpiredActiveSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    List<UserSubscription> findByStatus(SubscriptionStatus status);
}