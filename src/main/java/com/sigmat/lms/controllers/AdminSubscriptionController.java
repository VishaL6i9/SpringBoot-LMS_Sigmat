package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.SubscriptionPlanDTO;
import com.sigmat.lms.dtos.UserSubscriptionDTO;
import com.sigmat.lms.models.SubscriptionPlan;
import com.sigmat.lms.models.SubscriptionStatus;
import com.sigmat.lms.repository.SubscriptionPlanRepository;
import com.sigmat.lms.repository.UserSubscriptionRepository;
import com.sigmat.lms.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @GetMapping("/plans/all")
    public ResponseEntity<List<SubscriptionPlan>> getAllPlansIncludingInactive() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/plans")
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlan plan) {
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return ResponseEntity.ok(savedPlan);
    }

    @PutMapping("/plans/{planId}")
    public ResponseEntity<SubscriptionPlan> updatePlan(
            @PathVariable Long planId, 
            @RequestBody SubscriptionPlan plan) {
        plan.setId(planId);
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<Void> deactivatePlan(@PathVariable Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        plan.setActive(false);
        subscriptionPlanRepository.save(plan);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserSubscriptionDTO>> getAllUserSubscriptions() {
        List<UserSubscriptionDTO> subscriptions = userSubscriptionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<UserSubscriptionDTO>> getActiveSubscriptions() {
        List<UserSubscriptionDTO> subscriptions = userSubscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE)
                .stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/expire-all")
    public ResponseEntity<Void> expireAllSubscriptions() {
        subscriptionService.expireSubscriptions();
        return ResponseEntity.ok().build();
    }

    private UserSubscriptionDTO convertToDTO(com.sigmat.lms.models.UserSubscription subscription) {
        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUser().getId());
        dto.setUsername(subscription.getUser().getUsername());
        
        SubscriptionPlanDTO planDTO = new SubscriptionPlanDTO();
        planDTO.setId(subscription.getSubscriptionPlan().getId());
        planDTO.setName(subscription.getSubscriptionPlan().getName());
        planDTO.setPlanType(subscription.getSubscriptionPlan().getPlanType());
        planDTO.setLearnerTier(subscription.getSubscriptionPlan().getLearnerTier());
        planDTO.setFacultyTier(subscription.getSubscriptionPlan().getFacultyTier());
        planDTO.setPriceInr(subscription.getSubscriptionPlan().getPriceInr());
        
        dto.setSubscriptionPlan(planDTO);
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