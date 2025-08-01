package com.flux.lms.controllers;

import com.flux.lms.services.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        log.info("Received Stripe webhook");
        
        if (webhookSecret.isEmpty()) {
            log.error("Webhook secret not configured");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Webhook secret not configured"));
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("⚠️ Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Webhook signature verification failed"));
        } catch (Exception e) {
            log.error("Error parsing webhook payload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid payload"));
        }

        try {
            // Handle the event based on its type
            switch (event.getType()) {
                case "checkout.session.completed":
                    stripeWebhookService.handleCheckoutSessionCompleted(event);
                    break;
                
                case "invoice.paid":
                    stripeWebhookService.handleInvoicePaid(event);
                    break;
                
                case "invoice.payment_failed":
                    stripeWebhookService.handleInvoicePaymentFailed(event);
                    break;
                
                case "customer.subscription.updated":
                    stripeWebhookService.handleSubscriptionUpdated(event);
                    break;
                
                case "customer.subscription.deleted":
                    stripeWebhookService.handleSubscriptionDeleted(event);
                    break;
                
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok(Map.of("received", true));
            
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Webhook processing failed"));
        }
    }
}