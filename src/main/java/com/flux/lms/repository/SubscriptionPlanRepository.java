package com.flux.lms.repository;

import com.flux.lms.models.FacultyPlanTier;
import com.flux.lms.models.LearnerPlanTier;
import com.flux.lms.models.SubscriptionPlan;
import com.flux.lms.models.SubscriptionPlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    List<SubscriptionPlan> findByPlanTypeAndIsActiveTrue(SubscriptionPlanType planType);
    
    List<SubscriptionPlan> findByIsActiveTrue();
    
    Optional<SubscriptionPlan> findByPlanTypeAndLearnerTier(SubscriptionPlanType planType, LearnerPlanTier learnerTier);
    
    Optional<SubscriptionPlan> findByPlanTypeAndFacultyTier(SubscriptionPlanType planType, FacultyPlanTier facultyTier);
    
    List<SubscriptionPlan> findByPlanTypeAndIsCustomPricingFalseAndIsActiveTrue(SubscriptionPlanType planType);
}