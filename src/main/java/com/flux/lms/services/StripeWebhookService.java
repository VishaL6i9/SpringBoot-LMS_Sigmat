package com.flux.lms.services;

import com.flux.lms.dtos.NotificationDTO;
import com.flux.lms.models.NotificationCategory;
import com.flux.lms.models.NotificationPriority;
import com.flux.lms.models.NotificationType;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;
    private final CoursePurchaseService coursePurchaseService;
    private final EmailService emailService;
    private final UserService userService;

    @Transactional
    public void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null) {
            log.error("Failed to deserialize checkout session from event");
            return;
        }

        log.info("Checkout session completed: {}", session.getId());

        Map<String, String> metadata = session.getMetadata();
        String purchaseType = metadata.get("purchase_type");
        String userIdStr = metadata.get("user_id");

        if (userIdStr == null) {
            log.error("Missing user_id in checkout session metadata");
            return;
        }

        try {
            Long userId = Long.valueOf(userIdStr);

            if ("course".equals(purchaseType)) {
                // Handle course purchase
                var coursePurchase = coursePurchaseService.completePurchase(session.getId(), "stripe_session_" + session.getId());
                
                // Send course purchase success notification
                sendPurchaseSuccessNotification(userId, "course", Map.of(
                    "sessionId", session.getId(),
                    "amount", session.getAmountTotal() / 100.0, // Convert from cents
                    "currency", session.getCurrency().toUpperCase(),
                    "purchaseType", "Course Purchase",
                    "date", LocalDateTime.now().toString()
                ));
                
                log.info("Course purchase completed successfully for user: {}", userId);
                
            } else {
                // Handle subscription purchase
                String planIdStr = metadata.get("plan_id");
                String durationMonthsStr = metadata.get("duration_months");

                if (planIdStr == null) {
                    log.error("Missing plan_id for subscription purchase");
                    return;
                }

                Long planId = Long.valueOf(planIdStr);
                Integer durationMonths = durationMonthsStr != null ? Integer.valueOf(durationMonthsStr) : null;

                var subscription = subscriptionService.processCheckoutSuccess(session.getId(), userId, planId, durationMonths);
                
                // Send subscription success notification
                sendPurchaseSuccessNotification(userId, "subscription", Map.of(
                    "sessionId", session.getId(),
                    "amount", session.getAmountTotal() / 100.0, // Convert from cents
                    "currency", session.getCurrency().toUpperCase(),
                    "purchaseType", "Subscription",
                    "planName", subscription.getSubscriptionPlan().getName(),
                    "duration", durationMonths != null ? durationMonths + " months" : "1 month",
                    "date", LocalDateTime.now().toString()
                ));
                
                log.info("Subscription created successfully for user: {}", userId);
            }

        } catch (Exception e) {
            log.error("Failed to process checkout session: {}", e.getMessage(), e);
            
            // Send failure notification to user
            try {
                Long userId = Long.valueOf(userIdStr);
                sendPurchaseFailureNotification(userId, Map.of(
                    "sessionId", session.getId(),
                    "error", e.getMessage(),
                    "date", LocalDateTime.now().toString()
                ));
            } catch (Exception notificationError) {
                log.error("Failed to send failure notification: {}", notificationError.getMessage());
            }
            
            throw new RuntimeException("Checkout processing failed", e);
        }
    }

    @Transactional
    public void handleInvoicePaid(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice == null) {
            log.error("Failed to deserialize invoice from event");
            return;
        }

        log.info("Invoice paid: {}", invoice.getId());

        String subscriptionId = invoice.getSubscription();
        if (subscriptionId == null) {
            log.warn("Invoice has no associated subscription: {}", invoice.getId());
            return;
        }

        try {
            // Get subscription and customer details
            Subscription subscription = Subscription.retrieve(subscriptionId);
            Customer customer = Customer.retrieve(subscription.getCustomer());

            if (customer.getDeleted() != null && customer.getDeleted()) {
                log.warn("Customer is deleted: {}", customer.getId());
                return;
            }

            String userIdStr = customer.getMetadata().get("user_id");
            if (userIdStr == null) {
                log.warn("No user_id in customer metadata: {}", customer.getId());
                return;
            }

            Long userId = Long.valueOf(userIdStr);

            // Update subscription status to active if it was suspended
            try {
                subscriptionService.activateSubscriptionByStripeId(subscriptionId);
                log.info("Subscription activated for user: {}", userId);
            } catch (Exception e) {
                log.warn("Could not activate subscription: {}", e.getMessage());
            }

            // Send success notification
            sendSubscriptionNotification(userId, "payment_success", Map.of(
                "invoiceId", invoice.getId(),
                "amount", invoice.getAmountPaid() / 100.0, // Convert from cents
                "currency", invoice.getCurrency().toUpperCase(),
                "date", LocalDateTime.now().toString(),
                "subscriptionId", subscriptionId
            ));

            // Send email confirmation
            sendPaymentSuccessEmail(userId, invoice);

            log.info("Payment success processed for user: {}", userId);

        } catch (Exception e) {
            log.error("Failed to process invoice payment: {}", e.getMessage(), e);
            throw new RuntimeException("Invoice payment processing failed", e);
        }
    }

    @Transactional
    public void handleInvoicePaymentFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
        if (invoice == null) {
            log.error("Failed to deserialize invoice from event");
            return;
        }

        log.info("Invoice payment failed: {}", invoice.getId());

        String subscriptionId = invoice.getSubscription();
        if (subscriptionId == null) {
            log.warn("Invoice has no associated subscription: {}", invoice.getId());
            return;
        }

        try {
            // Get subscription and customer details
            Subscription subscription = Subscription.retrieve(subscriptionId);
            Customer customer = Customer.retrieve(subscription.getCustomer());

            if (customer.getDeleted() != null && customer.getDeleted()) {
                log.warn("Customer is deleted: {}", customer.getId());
                return;
            }

            String userIdStr = customer.getMetadata().get("user_id");
            if (userIdStr == null) {
                log.warn("No user_id in customer metadata: {}", customer.getId());
                return;
            }

            Long userId = Long.valueOf(userIdStr);

            // Get failure reason
            String reason = "Payment method declined";
            if (invoice.getPaymentIntent() != null) {
                try {
                    PaymentIntent paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());
                    if (paymentIntent.getLastPaymentError() != null && paymentIntent.getLastPaymentError().getMessage() != null) {
                        reason = paymentIntent.getLastPaymentError().getMessage();
                    }
                } catch (Exception e) {
                    log.error("Could not retrieve payment intent", e);
                }
            }

            // Send failure notification
            sendSubscriptionNotification(userId, "payment_failed", Map.of(
                "invoiceId", invoice.getId(),
                "amount", invoice.getAmountDue() / 100.0, // Convert from cents
                "date", LocalDateTime.now().toString(),
                "reason", reason
            ));

            log.info("Payment failure notification sent to user: {}", userId);

        } catch (Exception e) {
            log.error("Failed to process invoice payment failure: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void handleSubscriptionUpdated(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
        if (subscription == null) {
            log.error("Failed to deserialize subscription from event");
            return;
        }

        log.info("Subscription updated: {}", subscription.getId());

        try {
            Customer customer = Customer.retrieve(subscription.getCustomer());

            if (customer.getDeleted() != null && customer.getDeleted()) {
                log.warn("Customer is deleted: {}", customer.getId());
                return;
            }

            String userIdStr = customer.getMetadata().get("user_id");
            if (userIdStr == null) {
                log.warn("No user_id in customer metadata: {}", customer.getId());
                return;
            }

            Long userId = Long.valueOf(userIdStr);

            // If subscription was cancelled but still active until period end
            if (subscription.getCancelAtPeriodEnd()) {
                LocalDateTime endDate = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(subscription.getCurrentPeriodEnd()), 
                    ZoneId.systemDefault()
                );

                sendSubscriptionNotification(userId, "subscription_cancelled", Map.of(
                    "subscriptionId", subscription.getId(),
                    "endDate", endDate.toString()
                ));
            }

            // Check if subscription is about to expire (within 7 days)
            long now = Instant.now().getEpochSecond();
            long daysUntilExpiration = (subscription.getCurrentPeriodEnd() - now) / (60 * 60 * 24);

            if (daysUntilExpiration <= 7 && !subscription.getCancelAtPeriodEnd()) {
                LocalDateTime renewalDate = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(subscription.getCurrentPeriodEnd()), 
                    ZoneId.systemDefault()
                );

                sendSubscriptionNotification(userId, "subscription_expiring_soon", Map.of(
                    "subscriptionId", subscription.getId(),
                    "daysRemaining", daysUntilExpiration,
                    "renewalDate", renewalDate.toString()
                ));
            }

            log.info("Subscription update processed for user: {}", userId);

        } catch (Exception e) {
            log.error("Failed to process subscription update: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
        if (subscription == null) {
            log.error("Failed to deserialize subscription from event");
            return;
        }

        log.info("Subscription deleted: {}", subscription.getId());

        try {
            Customer customer = Customer.retrieve(subscription.getCustomer());

            if (customer.getDeleted() != null && customer.getDeleted()) {
                log.warn("Customer is deleted: {}", customer.getId());
                return;
            }

            String userIdStr = customer.getMetadata().get("user_id");
            if (userIdStr == null) {
                log.warn("No user_id in customer metadata: {}", customer.getId());
                return;
            }

            Long userId = Long.valueOf(userIdStr);

            // Send subscription ended notification
            sendSubscriptionNotification(userId, "subscription_ended", Map.of(
                "subscriptionId", subscription.getId(),
                "endDate", LocalDateTime.now().toString()
            ));

            log.info("Subscription deletion processed for user: {}", userId);

        } catch (Exception e) {
            log.error("Failed to process subscription deletion: {}", e.getMessage(), e);
        }
    }

    private void sendSubscriptionNotification(Long userId, String type, Map<String, Object> data) {
        try {
            NotificationDTO notification = NotificationDTO.builder()
                    .title(getNotificationTitle(type))
                    .message(getNotificationMessage(type, data))
                    .type(NotificationType.INFO)
                    .category(NotificationCategory.SYSTEM)
                    .priority(type.contains("failed") ? NotificationPriority.HIGH : NotificationPriority.MEDIUM)
                    .build();

            notificationService.createNotification(notification, userId);
            log.info("Notification sent to user {}: {}", userId, type);

        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    private String getNotificationTitle(String type) {
        return switch (type) {
            case "payment_success" -> "Payment Successful";
            case "payment_failed" -> "Payment Failed";
            case "subscription_cancelled" -> "Subscription Cancelled";
            case "subscription_expiring_soon" -> "Subscription Expiring Soon";
            case "subscription_ended" -> "Subscription Ended";
            default -> "Subscription Update";
        };
    }

    private String getNotificationMessage(String type, Map<String, Object> data) {
        return switch (type) {
            case "payment_success" -> String.format(
                "Your payment of ₹%.2f was successfully processed. Thank you for your subscription!",
                (Double) data.get("amount")
            );
            case "payment_failed" -> String.format(
                "Your payment of ₹%.2f failed. Reason: %s. Please update your payment method.",
                (Double) data.get("amount"),
                data.get("reason")
            );
            case "subscription_cancelled" -> String.format(
                "Your subscription has been cancelled. You'll still have access until %s.",
                data.get("endDate")
            );
            case "subscription_expiring_soon" -> String.format(
                "Your subscription will renew in %d days on %s.",
                (Long) data.get("daysRemaining"),
                data.get("renewalDate")
            );
            case "subscription_ended" -> 
                "Your subscription has ended. To continue accessing premium features, please renew your subscription.";
            default -> "Your subscription status has been updated.";
        };
    }

    private void sendPurchaseSuccessNotification(Long userId, String purchaseType, Map<String, Object> data) {
        try {
            String title = "course".equals(purchaseType) ? "Course Purchase Successful!" : "Subscription Activated!";
            String message = "course".equals(purchaseType) ? 
                String.format("Your course purchase of %s %.2f was successful. You now have access to your course content!", 
                    data.get("currency"), (Double) data.get("amount")) :
                String.format("Your %s subscription of %s %.2f was successful. Your %s subscription is now active!", 
                    data.get("planName"), data.get("currency"), (Double) data.get("amount"), data.get("duration"));

            NotificationDTO notification = NotificationDTO.builder()
                    .title(title)
                    .message(message)
                    .type(NotificationType.SUCCESS)
                    .category(NotificationCategory.PAYMENT)
                    .priority(NotificationPriority.HIGH)
                    .build();

            notificationService.createNotification(notification, userId);
            log.info("Purchase success notification sent to user {}: {}", userId, purchaseType);

        } catch (Exception e) {
            log.error("Failed to send purchase success notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    private void sendPurchaseFailureNotification(Long userId, Map<String, Object> data) {
        try {
            NotificationDTO notification = NotificationDTO.builder()
                    .title("Purchase Failed")
                    .message(String.format("Your purchase could not be completed. Error: %s. Please try again or contact support.", 
                        data.get("error")))
                    .type(NotificationType.ERROR)
                    .category(NotificationCategory.PAYMENT)
                    .priority(NotificationPriority.HIGH)
                    .build();

            notificationService.createNotification(notification, userId);
            log.info("Purchase failure notification sent to user {}", userId);

        } catch (Exception e) {
            log.error("Failed to send purchase failure notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    private void sendPaymentSuccessEmail(Long userId, Invoice invoice) {
        try {
            var user = userService.getUserById(userId);
            if (user != null && user.getEmail() != null) {
                emailService.sendPaymentSuccessEmail(
                    user.getEmail(),
                    user.getFirstName() + " " + user.getLastName(),
                    invoice.getAmountPaid() / 100.0, // Convert from cents
                    invoice.getCurrency().toUpperCase(),
                    invoice.getId()
                );
                log.info("Payment success email sent to user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to send payment success email to user {}: {}", userId, e.getMessage());
        }
    }
}