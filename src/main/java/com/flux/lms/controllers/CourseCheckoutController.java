package com.flux.lms.controllers;

import com.flux.lms.dtos.CourseCheckoutDTO;
import com.flux.lms.dtos.CoursePurchaseDTO;
import com.flux.lms.services.CoursePurchaseService;
import com.flux.lms.services.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseCheckoutController {

    private final CoursePurchaseService coursePurchaseService;
    private final StripeService stripeService;

    @PostMapping("/{courseId}/users/{userId}/checkout")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<?> createCourseCheckout(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @RequestBody CourseCheckoutDTO request) {
        try {
            // Check if user already purchased this course
            if (coursePurchaseService.hasUserPurchasedCourse(userId, courseId)) {
                return ResponseEntity.badRequest().body("User has already purchased this course");
            }

            // Create purchase record
            CoursePurchaseDTO purchase = coursePurchaseService.createPurchase(
                    userId, courseId, request.getDiscountApplied());

            // Create Stripe checkout session
            String sessionUrl = stripeService.createCourseCheckoutSession(
                    courseId,
                    userId,
                    request.getDiscountApplied(),
                    request.getSuccessUrl(),
                    request.getCancelUrl()
            );

            // Update purchase with session ID
            coursePurchaseService.updatePurchaseSessionId(purchase.getId(), extractSessionId(sessionUrl));

            Map<String, Object> response = new HashMap<>();
            response.put("sessionUrl", sessionUrl);
            response.put("purchaseId", purchase.getId());
            response.put("courseId", courseId);
            response.put("userId", userId);
            response.put("finalAmount", purchase.getFinalAmount());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating course checkout for course {} and user {}: {}", courseId, userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error creating course checkout: " + e.getMessage());
        }
    }

    @PostMapping("/checkout/success")
    public ResponseEntity<?> handleCourseCheckoutSuccess(
            @RequestParam String sessionId,
            @RequestParam Long userId) {
        try {
            // Retrieve session from Stripe to verify payment
            var session = stripeService.getCheckoutSession(sessionId);
            
            if (!"complete".equals(session.getPaymentStatus())) {
                return ResponseEntity.badRequest().body("Payment not completed");
            }

            // Complete the purchase
            CoursePurchaseDTO purchase = coursePurchaseService.completePurchase(
                    sessionId, "stripe_session_" + sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("purchase", purchase);
            response.put("sessionId", sessionId);
            response.put("message", "Course purchased successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing course checkout success for session {}: {}", sessionId, e.getMessage(), e);
            coursePurchaseService.failPurchase(sessionId, e.getMessage());
            return ResponseEntity.internalServerError().body("Error processing course purchase: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}/users/{userId}/purchase")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<CoursePurchaseDTO> getUserCoursePurchase(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        return coursePurchaseService.getUserCoursePurchase(userId, courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{courseId}/users/{userId}/has-purchased")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Boolean>> hasUserPurchasedCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        boolean hasPurchased = coursePurchaseService.hasUserPurchasedCourse(userId, courseId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasPurchased", hasPurchased);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/purchases")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<CoursePurchaseDTO>> getUserPurchases(@PathVariable Long userId) {
        List<CoursePurchaseDTO> purchases = coursePurchaseService.getUserPurchases(userId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{courseId}/purchases")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<CoursePurchaseDTO>> getCoursePurchases(@PathVariable Long courseId) {
        List<CoursePurchaseDTO> purchases = coursePurchaseService.getCoursePurchases(courseId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{courseId}/revenue")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Map<String, Object>> getCourseRevenue(@PathVariable Long courseId) {
        Long revenue = coursePurchaseService.getCourseRevenue(courseId);
        Long enrollmentCount = coursePurchaseService.getCourseEnrollmentCount(courseId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("courseId", courseId);
        response.put("totalRevenue", revenue);
        response.put("totalEnrollments", enrollmentCount);
        
        return ResponseEntity.ok(response);
    }

    private String extractSessionId(String sessionUrl) {
        // Extract session ID from Stripe checkout URL
        // URL format: https://checkout.stripe.com/c/pay/cs_test_...
        String[] parts = sessionUrl.split("/");
        return parts[parts.length - 1].split("#")[0]; // Remove any fragment
    }
}