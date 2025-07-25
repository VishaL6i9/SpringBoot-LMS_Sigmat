package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.CheckoutSessionDTO;
import com.sigmat.lms.services.EnrollmentService;
import com.sigmat.lms.services.StripeService;
import com.sigmat.lms.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final StripeService stripeService;
    private final EnrollmentService enrollmentService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public CheckoutController(StripeService stripeService, EnrollmentService enrollmentService, SubscriptionService subscriptionService) {
        this.stripeService = stripeService;
        this.enrollmentService = enrollmentService;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create-checkout-session")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutSessionDTO request) {
        try {
            String sessionUrl;
            
            // New subscription plan flow
            if (request.getPlanId() != null) {
                sessionUrl = stripeService.createCheckoutSessionForPlan(
                    request.getPlanId(), 
                    request.getDurationMonths(), 
                    request.getSuccessUrl(), 
                    request.getCancelUrl()
                );
            } 
            // Legacy tier-based flow
            else if (request.getTier() != null) {
                sessionUrl = stripeService.createCheckoutSession(
                    request.getTier(), 
                    request.getSuccessUrl(), 
                    request.getCancelUrl()
                );
                

            } 
            else {
                return ResponseEntity.badRequest().body("Either planId or tier must be provided");
            }

            Map<String, String> response = new HashMap<>();
            response.put("sessionUrl", sessionUrl);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating checkout session: " + e.getMessage());
        }
    }

    // Deprecated: Use /api/subscriptions/users/{userId}/checkout instead
    @PostMapping("/platform-subscription")
    @PreAuthorize("hasRole('USER')")
    @Deprecated
    public ResponseEntity<?> createPlatformSubscriptionCheckout(@RequestBody CheckoutSessionDTO request) {
        return ResponseEntity.status(301)
                .header("Location", "/api/subscriptions/users/{userId}/checkout")
                .body("This endpoint has been moved to /api/subscriptions/users/{userId}/checkout");
    }

    // Deprecated: Use /api/subscriptions/courses/{courseId}/users/{userId}/checkout instead
    @PostMapping("/course-subscription")
    @PreAuthorize("hasRole('USER')")
    @Deprecated
    public ResponseEntity<?> createCourseSubscriptionCheckout(@RequestBody CheckoutSessionDTO request) {
        return ResponseEntity.status(301)
                .header("Location", "/api/subscriptions/courses/{courseId}/users/{userId}/checkout")
                .body("This endpoint has been moved to /api/subscriptions/courses/{courseId}/users/{userId}/checkout");
    }

    // Deprecated: Use /api/subscriptions/checkout/success instead
    @PostMapping("/success")
    @Deprecated
    public ResponseEntity<?> handleCheckoutSuccess(@RequestParam String sessionId, @RequestParam Long userId) {
        return ResponseEntity.status(301)
                .header("Location", "/api/subscriptions/checkout/success")
                .body("This endpoint has been moved to /api/subscriptions/checkout/success");
    }
}