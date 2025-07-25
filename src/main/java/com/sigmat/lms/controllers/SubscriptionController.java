package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.CheckoutSessionDTO;
import com.sigmat.lms.dtos.SubscriptionPlanDTO;
import com.sigmat.lms.dtos.SubscriptionRequestDTO;
import com.sigmat.lms.dtos.UserSubscriptionDTO;
import com.sigmat.lms.models.SubscriptionPlanType;
import com.sigmat.lms.services.StripeService;
import com.sigmat.lms.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final StripeService stripeService;

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

    // Checkout Session Endpoints

    @PostMapping("/users/{userId}/checkout")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<?> createPlatformSubscriptionCheckout(
            @PathVariable Long userId,
            @RequestBody CheckoutSessionDTO request) {
        try {
            if (request.getPlanId() == null) {
                return ResponseEntity.badRequest().body("Plan ID is required for platform subscription");
            }

            // Verify the plan exists and is active
            var plan = subscriptionService.getPlanById(request.getPlanId());
            if (!plan.isActive()) {
                return ResponseEntity.badRequest().body("Subscription plan is not active");
            }



            String sessionUrl = stripeService.createCheckoutSessionForPlan(
                request.getPlanId(), 
                request.getDurationMonths(), 
                request.getSuccessUrl(), 
                request.getCancelUrl(),
                userId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("sessionUrl", sessionUrl);
            response.put("planId", request.getPlanId());
            response.put("userId", userId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating platform subscription checkout: " + e.getMessage());
        }
    }



    @PostMapping("/checkout/success")
    public ResponseEntity<?> handleCheckoutSuccess(
            @RequestParam String sessionId, 
            @RequestParam Long userId) {
        try {
            // Retrieve session from Stripe to get metadata
            var session = stripeService.getCheckoutSession(sessionId);
            
            if (!"complete".equals(session.getPaymentStatus())) {
                return ResponseEntity.badRequest().body("Payment not completed");
            }

            // Extract metadata
            Long planId = Long.valueOf(session.getMetadata().get("plan_id"));
            Integer durationMonths = Integer.valueOf(session.getMetadata().get("duration_months"));

            // Process the checkout success
            UserSubscriptionDTO subscription = subscriptionService.processCheckoutSuccess(
                sessionId, userId, planId, durationMonths);

            Map<String, Object> response = new HashMap<>();
            response.put("subscription", subscription);
            response.put("sessionId", sessionId);
            response.put("message", "Subscription created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing subscription: " + e.getMessage());
        }
    }


}