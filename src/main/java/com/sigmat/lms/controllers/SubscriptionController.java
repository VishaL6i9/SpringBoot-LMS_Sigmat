package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.SubscriptionPlanDTO;
import com.sigmat.lms.dtos.SubscriptionRequestDTO;
import com.sigmat.lms.dtos.UserSubscriptionDTO;
import com.sigmat.lms.models.SubscriptionPlanType;
import com.sigmat.lms.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/learner")
    public ResponseEntity<List<SubscriptionPlanDTO>> getLearnerPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionService.getPlansByType(SubscriptionPlanType.LEARNER);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/faculty")
    public ResponseEntity<List<SubscriptionPlanDTO>> getFacultyPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionService.getPlansByType(SubscriptionPlanType.FACULTY);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<SubscriptionPlanDTO> getPlanById(@PathVariable Long planId) {
        SubscriptionPlanDTO plan = subscriptionService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/users/{userId}/subscribe")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserSubscriptionDTO> subscribeUser(
            @PathVariable Long userId,
            @RequestBody SubscriptionRequestDTO request) {
        UserSubscriptionDTO subscription = subscriptionService.subscribeUser(userId, request);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserSubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId) {
        List<UserSubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/users/{userId}/current")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserSubscriptionDTO> getCurrentSubscription(@PathVariable Long userId) {
        UserSubscriptionDTO subscription = subscriptionService.getCurrentSubscription(userId);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{subscriptionId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @subscriptionService.isSubscriptionOwner(#subscriptionId, authentication.principal.id)")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/expire-subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> expireSubscriptions() {
        subscriptionService.expireSubscriptions();
        return ResponseEntity.ok().build();
    }
}