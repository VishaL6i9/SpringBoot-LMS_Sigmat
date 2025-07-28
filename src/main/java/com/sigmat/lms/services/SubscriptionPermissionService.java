package com.sigmat.lms.services;

import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.UserRepo;
import com.sigmat.lms.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionPermissionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepo userRepository;

    public boolean hasActiveSubscription(Long userId) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        return userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now())
                .isPresent();
    }

    public boolean canAccessFeature(Long userId, String feature) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<UserSubscription> activeSubscription = 
                userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now());

        if (activeSubscription.isEmpty()) {
            // Check if it's a free feature available to all users
            return isFreeTierFeature(feature);
        }

        SubscriptionPlan plan = activeSubscription.get().getSubscriptionPlan();
        return plan.getFeatures().contains(feature);
    }

    public boolean canCreateCourses(Long userId, int courseCount) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<UserSubscription> activeSubscription = 
                userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now());

        if (activeSubscription.isEmpty()) {
            // Free tier allows 1 course for faculty
            return courseCount <= 1;
        }

        SubscriptionPlan plan = activeSubscription.get().getSubscriptionPlan();
        
        // Check based on faculty tier
        if (plan.getPlanType() == SubscriptionPlanType.FACULTY) {
            switch (plan.getFacultyTier()) {
                case STARTER:
                    return courseCount <= 1;
                case EDUCATOR:
                case MENTOR:
                case INSTITUTIONAL:
                    return true; // Unlimited courses
                default:
                    return false;
            }
        }
        
        return false;
    }

    public boolean canAccessLiveFeatures(Long userId) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<UserSubscription> activeSubscription = 
                userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now());

        if (activeSubscription.isEmpty()) {
            return false; // Live features not available in free tier
        }

        SubscriptionPlan plan = activeSubscription.get().getSubscriptionPlan();
        
        if (plan.getPlanType() == SubscriptionPlanType.LEARNER) {
            return plan.getLearnerTier() == LearnerPlanTier.MASTERY || 
                   plan.getLearnerTier() == LearnerPlanTier.INSTITUTIONAL;
        }
        
        if (plan.getPlanType() == SubscriptionPlanType.FACULTY) {
            return plan.getFacultyTier() == FacultyPlanTier.MENTOR || 
                   plan.getFacultyTier() == FacultyPlanTier.INSTITUTIONAL;
        }
        
        return false;
    }

    public boolean canDownloadContent(Long userId) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<UserSubscription> activeSubscription = 
                userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now());

        if (activeSubscription.isEmpty()) {
            return false; // Download not available in free tier
        }

        SubscriptionPlan plan = activeSubscription.get().getSubscriptionPlan();
        
        if (plan.getPlanType() == SubscriptionPlanType.LEARNER) {
            return plan.getLearnerTier() == LearnerPlanTier.PROFESSIONAL || 
                   plan.getLearnerTier() == LearnerPlanTier.MASTERY ||
                   plan.getLearnerTier() == LearnerPlanTier.INSTITUTIONAL;
        }
        
        return false;
    }

    public UserSubscription getCurrentSubscription(Long userId) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        return userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now())
                .orElse(null);
    }

    private boolean isFreeTierFeature(String feature) {
        // Define features available to free tier users
        return feature.equals("Access public course catalog") ||
               feature.equals("Forum participation") ||
               feature.equals("Create one course") ||
               feature.equals("Upload static content");
    }
}