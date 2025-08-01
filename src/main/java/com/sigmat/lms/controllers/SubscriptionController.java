package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.CheckoutSessionDTO;
import com.sigmat.lms.dtos.SubscriptionPlanDTO;
import com.sigmat.lms.dtos.SubscriptionRequestDTO;
import com.sigmat.lms.dtos.UserSubscriptionDTO;
import com.sigmat.lms.models.SubscriptionPlanType;
import com.sigmat.lms.services.StripeService;
import com.sigmat.lms.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserSubscriptionDTO> subscribeUser(
            @PathVariable Long userId,
            @RequestBody SubscriptionRequestDTO request) {
        UserSubscriptionDTO subscription = subscriptionService.subscribeUser(userId, request);
        return ResponseEntity.ok(subscription);
    }



    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserSubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId) {
        List<UserSubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/users/{userId}/current")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserSubscriptionDTO> getCurrentSubscription(@PathVariable Long userId) {
        UserSubscriptionDTO subscription = subscriptionService.getCurrentSubscription(userId);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        }
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/{subscriptionId}/cancel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or @subscriptionService.isSubscriptionOwner(#subscriptionId, authentication.principal.id)")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/expire-subscriptions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> expireSubscriptions() {
        subscriptionService.expireSubscriptions();
        return ResponseEntity.ok().build();
    }

    // Checkout Session Endpoints

    @PostMapping("/users/{userId}/checkout")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
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
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "session_id", required = false) String sessionIdAlt,
            @RequestParam Long userId) {
        try {
            // Handle both sessionId and session_id parameters (Stripe can send either)
            String actualSessionId = sessionId != null ? sessionId : sessionIdAlt;
            
            if (actualSessionId == null) {
                return ResponseEntity.badRequest().body("Missing session ID parameter");
            }
            
            // Clean the session ID - remove any duplicated parts
            actualSessionId = cleanSessionId(actualSessionId);
            
            // Validate session ID format and length
            if (!isValidSessionId(actualSessionId)) {
                return ResponseEntity.badRequest().body("Invalid session ID format");
            }

            // Retrieve session from Stripe to get metadata
            var session = stripeService.getCheckoutSession(actualSessionId);
            
            if (!"complete".equals(session.getPaymentStatus())) {
                return ResponseEntity.badRequest().body("Payment not completed");
            }

            // Extract metadata
            Long planId = Long.valueOf(session.getMetadata().get("plan_id"));
            Integer durationMonths = Integer.valueOf(session.getMetadata().get("duration_months"));

            // Process the checkout success
            UserSubscriptionDTO subscription = subscriptionService.processCheckoutSuccess(
                actualSessionId, userId, planId, durationMonths);

            Map<String, Object> response = new HashMap<>();
            response.put("subscription", subscription);
            response.put("sessionId", actualSessionId);
            response.put("message", "Subscription created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing checkout success: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing subscription: " + e.getMessage());
        }
    }
    
    private String cleanSessionId(String sessionId) {
        if (sessionId == null) return null;
        
        // Remove any query parameters that might have been appended
        if (sessionId.contains("?")) {
            sessionId = sessionId.substring(0, sessionId.indexOf("?"));
        }
        
        // If the session ID appears to be duplicated, take only the first part
        if (sessionId.length() > 66 && sessionId.startsWith("cs_test_")) {
            // Find the second occurrence of "cs_test_" and split there
            int secondOccurrence = sessionId.indexOf("cs_test_", 8);
            if (secondOccurrence > 0) {
                sessionId = sessionId.substring(0, secondOccurrence);
            }
        }
        
        return sessionId.trim();
    }
    
    private boolean isValidSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        
        // Stripe session IDs should start with cs_ and be at most 66 characters
        return sessionId.startsWith("cs_") && sessionId.length() <= 66;
    }
    
    @GetMapping("/debug/session/{sessionId}")
    public ResponseEntity<?> debugSession(@PathVariable String sessionId) {
        try {
            String cleanedSessionId = cleanSessionId(sessionId);
            boolean isValid = isValidSessionId(cleanedSessionId);
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("originalSessionId", sessionId);
            debug.put("cleanedSessionId", cleanedSessionId);
            debug.put("originalLength", sessionId.length());
            debug.put("cleanedLength", cleanedSessionId.length());
            debug.put("isValid", isValid);
            debug.put("startsWithCs", sessionId.startsWith("cs_"));
            
            if (isValid) {
                try {
                    var session = stripeService.getCheckoutSession(cleanedSessionId);
                    debug.put("stripeSessionExists", true);
                    debug.put("paymentStatus", session.getPaymentStatus());
                    debug.put("metadata", session.getMetadata());
                } catch (Exception e) {
                    debug.put("stripeSessionExists", false);
                    debug.put("stripeError", e.getMessage());
                }
            }
            
            return ResponseEntity.ok(debug);
            
        } catch (Exception e) {
            log.error("Error debugging session: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Debug error: " + e.getMessage());
        }
    }


}