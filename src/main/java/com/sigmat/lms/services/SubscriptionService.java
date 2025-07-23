package com.sigmat.lms.services;

import com.sigmat.lms.dtos.SubscriptionPlanDTO;
import com.sigmat.lms.dtos.SubscriptionRequestDTO;
import com.sigmat.lms.dtos.UserSubscriptionDTO;
import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.CourseRepo;
import com.sigmat.lms.repository.UserRepo;
import com.sigmat.lms.repository.SubscriptionPlanRepository;
import com.sigmat.lms.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepo userRepository;
    private final CourseRepo courseRepository;

    public List<SubscriptionPlanDTO> getAllPlans(Long courseId) {
        if (courseId != null) {
            return subscriptionPlanRepository.findByCourseIdAndIsActiveTrue(courseId)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return subscriptionPlanRepository.findByIsActiveTrue()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<SubscriptionPlanDTO> getAllPlans() {
        return getAllPlans(null);
    }

    public List<SubscriptionPlanDTO> getPlansByType(SubscriptionPlanType planType) {
        return subscriptionPlanRepository.findByPlanTypeAndIsActiveTrue(planType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SubscriptionPlanDTO getPlanById(Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + planId));
        return convertToDTO(plan);
    }

    @Transactional
    public UserSubscriptionDTO subscribeUser(Long userId, SubscriptionRequestDTO request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + request.getPlanId()));

        // Cancel any existing active subscription
        cancelActiveSubscription(user);

        // Create new subscription
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setSubscriptionPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        
        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));
            subscription.setCourse(course);
        }

        // Calculate end date based on duration
        int durationMonths = request.getDurationMonths() != null ? 
                request.getDurationMonths() : plan.getMinimumDurationMonths();
        subscription.setEndDate(LocalDateTime.now().plusMonths(durationMonths));
        
        subscription.setAutoRenew(request.isAutoRenew());
        subscription.setActualPrice(plan.getPriceInr());
        subscription.setDiscountApplied(request.getDiscountApplied() != null ? 
                request.getDiscountApplied() : BigDecimal.ZERO);
        subscription.setPaymentReference(request.getPaymentReference());

        UserSubscription savedSubscription = userSubscriptionRepository.save(subscription);
        return convertToDTO(savedSubscription);
    }

    public List<UserSubscriptionDTO> getUserSubscriptions(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return userSubscriptionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserSubscriptionDTO getCurrentSubscription(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now())
                .map(this::convertToDTO)
                .orElse(null);
    }

    public UserSubscriptionDTO getCurrentCourseSubscription(Long userId, Long courseId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return userSubscriptionRepository.findActiveSubscriptionByUserAndCourse(user, course, LocalDateTime.now())
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        userSubscriptionRepository.save(subscription);
    }

    @Transactional
    public void expireSubscriptions() {
        List<UserSubscription> expiredSubscriptions = 
                userSubscriptionRepository.findExpiredActiveSubscriptions(LocalDateTime.now());

        for (UserSubscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
        }
    }

    private void cancelActiveSubscription(Users user) {
        userSubscriptionRepository.findActiveSubscriptionByUser(user, LocalDateTime.now())
                .ifPresent(activeSubscription -> {
                    activeSubscription.setStatus(SubscriptionStatus.CANCELLED);
                    userSubscriptionRepository.save(activeSubscription);
                });
    }

    private SubscriptionPlanDTO convertToDTO(SubscriptionPlan plan) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        if (plan.getCourse() != null) {
            dto.setCourseId(plan.getCourse().getCourseId());
        }
        dto.setName(plan.getName());
        dto.setPlanType(plan.getPlanType());
        dto.setLearnerTier(plan.getLearnerTier());
        dto.setFacultyTier(plan.getFacultyTier());
        dto.setPriceInr(plan.getPriceInr());
        dto.setDescription(plan.getDescription());
        dto.setFeatures(plan.getFeatures());
        dto.setBestSuitedFor(plan.getBestSuitedFor());
        dto.setActive(plan.isActive());
        dto.setMinimumDurationMonths(plan.getMinimumDurationMonths());
        dto.setCustomPricing(plan.isCustomPricing());
        return dto;
    }

    public boolean isSubscriptionOwner(Long subscriptionId, Long userId) {
        return userSubscriptionRepository.findById(subscriptionId)
                .map(subscription -> subscription.getUser().getId().equals(userId))
                .orElse(false);
    }

    private UserSubscriptionDTO convertToDTO(UserSubscription subscription) {
        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUser().getId());
        dto.setUsername(subscription.getUser().getUsername());
        dto.setSubscriptionPlan(convertToDTO(subscription.getSubscriptionPlan()));
        if (subscription.getCourse() != null) {
            dto.setCourseId(subscription.getCourse().getCourseId());
        }
        dto.setStatus(subscription.getStatus());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setAutoRenew(subscription.isAutoRenew());
        dto.setActualPrice(subscription.getActualPrice());
        dto.setDiscountApplied(subscription.getDiscountApplied());
        dto.setPaymentReference(subscription.getPaymentReference());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        return dto;
    }
}