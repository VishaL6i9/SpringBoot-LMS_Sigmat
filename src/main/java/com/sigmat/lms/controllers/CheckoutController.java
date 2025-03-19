package com.sigmat.lms.controllers;

import com.sigmat.lms.models.CheckoutSessionDTO;
import com.sigmat.lms.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class CheckoutController {

    private final StripeService stripeService;

    @Autowired
    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<String> createCheckoutSession(@RequestBody CheckoutSessionDTO request) {
        try {
            String sessionUrl = stripeService.createCheckoutSession(request.getTier(), request.getSuccessUrl(), request.getCancelUrl());
            return ResponseEntity.ok(sessionUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating checkout session: " + e.getMessage());
        }
    }
}