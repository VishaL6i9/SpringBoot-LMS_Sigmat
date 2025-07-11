package com.sigmat.lms.controllers;

import com.sigmat.lms.models.CheckoutSessionDTO;
import com.sigmat.lms.services.EnrollmentService;
import com.sigmat.lms.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final StripeService stripeService;
    private final EnrollmentService enrollmentService; // Inject EnrollmentService

    @Autowired
    public CheckoutController(StripeService stripeService, EnrollmentService enrollmentService) {
        this.stripeService = stripeService;
        this.enrollmentService = enrollmentService; // Initialize EnrollmentService
    }

    @PostMapping("/create-checkout-session")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createCheckoutSession(@RequestBody CheckoutSessionDTO request) {
        try {
            String sessionUrl = stripeService.createCheckoutSession(request.getTier(), request.getSuccessUrl(), request.getCancelUrl());
            
            // After successful checkout, enroll the user in the course
            if (request.getUserId() != null && request.getCourseId() != null) {
                enrollmentService.enrollUserInCourse(request.getUserId(), request.getCourseId(), request.getInstructorId());
            }

            return ResponseEntity.ok(sessionUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating checkout session: " + e.getMessage());
        }
    }
}