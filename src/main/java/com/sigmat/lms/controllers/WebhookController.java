package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.WebhookEventDTO;
import com.sigmat.lms.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/invoice-paid")
    public ResponseEntity<Map<String, Object>> handleInvoicePaid(@RequestBody WebhookEventDTO request) {
        try {
            log.info("Processing invoice paid webhook for user: {}", request.getUserId());
            
            // Update subscription status or handle payment success logic
            // This is called from the StripeWebhookService after processing the Stripe event
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Invoice payment processed successfully"
            ));
            
        } catch (Exception e) {
            log.error("Error processing invoice paid webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to process invoice payment"
            ));
        }
    }

    @PostMapping("/invoice-failed")
    public ResponseEntity<Map<String, Object>> handleInvoiceFailed(@RequestBody WebhookEventDTO request) {
        try {
            log.info("Processing invoice failed webhook for user: {}", request.getUserId());
            
            // Handle payment failure logic
            // This is called from the StripeWebhookService after processing the Stripe event
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Invoice payment failure processed successfully"
            ));
            
        } catch (Exception e) {
            log.error("Error processing invoice failed webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to process invoice payment failure"
            ));
        }
    }

    @PostMapping("/subscription-updated")
    public ResponseEntity<Map<String, Object>> handleSubscriptionUpdated(@RequestBody WebhookEventDTO request) {
        try {
            log.info("Processing subscription updated webhook for user: {}", request.getUserId());
            
            // Handle subscription update logic
            // This is called from the StripeWebhookService after processing the Stripe event
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Subscription update processed successfully"
            ));
            
        } catch (Exception e) {
            log.error("Error processing subscription updated webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to process subscription update"
            ));
        }
    }

    @PostMapping("/subscription-deleted")
    public ResponseEntity<Map<String, Object>> handleSubscriptionDeleted(@RequestBody WebhookEventDTO request) {
        try {
            log.info("Processing subscription deleted webhook for user: {}", request.getUserId());
            
            // Handle subscription deletion logic
            // This could involve updating the subscription status to cancelled
            if (request.getUserId() != null) {
                // Find and cancel active subscriptions for the user
                var subscriptions = subscriptionService.getUserSubscriptions(request.getUserId());
                subscriptions.stream()
                    .filter(sub -> "ACTIVE".equals(sub.getStatus().toString()))
                    .forEach(sub -> subscriptionService.cancelSubscription(sub.getId()));
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Subscription deletion processed successfully"
            ));
            
        } catch (Exception e) {
            log.error("Error processing subscription deleted webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to process subscription deletion"
            ));
        }
    }
}