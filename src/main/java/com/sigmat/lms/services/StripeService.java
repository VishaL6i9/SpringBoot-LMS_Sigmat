package com.sigmat.lms.services;

import com.sigmat.lms.models.Invoice;
import com.sigmat.lms.models.InvoiceItem;
import com.sigmat.lms.models.Users;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    public StripeService(@Value("${stripe.api.key}") String stripeApiKey) {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Creates a Stripe Checkout Session for a subscription tier.
     */
    public String createCheckoutSession(String tier, String successUrl, String cancelUrl) throws Exception {
        long amount = switch (tier.toLowerCase()) {
            case "starter" -> 3500L;      // $35.00
            case "professional" -> 6500L; // $65.00
            case "enterprise" -> 12500L;  // $125.00
            default -> throw new IllegalArgumentException("Invalid subscription tier: " + tier);
        };

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(capitalize(tier) + " Subscription")
                                                                .build()
                                                )
                                                .setUnitAmount(amount)
                                                .setRecurring(
                                                        SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                                                .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * Creates a Stripe customer for a Student.
     */
    public String createStripeCustomer(Users user) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getFirstName() + " " + user.getLastName())
                .setEmail(user.getEmail())
                .putMetadata("user_id", String.valueOf(user.getId()))
                .build();
        Customer customer = Customer.create(params);
        return customer.getId();
    }

    /**
     * Creates and returns a Stripe Invoice object.
     */
    public String createStripeInvoice(Invoice invoice) throws StripeException {
        // 1. Ensure customer exists
        String stripeCustomerId = findOrCreateCustomer(invoice.getUser());

        // 2. Create invoice items on Stripe
        for (InvoiceItem item : invoice.getItems()) {
            com.stripe.param.InvoiceItemCreateParams invoiceItemParams =
                    com.stripe.param.InvoiceItemCreateParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setQuantity((long) item.getQuantity())
                            .setUnitAmount((long) (item.getUnitPrice() * 100)) // Amount in cents
                            .setCurrency("usd")
                            .setDescription(item.getDescription())
                            .build();
            com.stripe.model.InvoiceItem.create(invoiceItemParams);
        }

        // 3. Create the invoice
        InvoiceCreateParams invoiceParams = InvoiceCreateParams.builder()
                .setCustomer(stripeCustomerId)
                .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
                .setDueDate(invoice.getDueDate() == null ? null : invoice.getDueDate().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC))
                .setDescription(invoice.getNotes())
                .build();
        com.stripe.model.Invoice stripeInvoice = com.stripe.model.Invoice.create(invoiceParams);

        // 4. Finalize the invoice to get the hosted URL
        stripeInvoice = stripeInvoice.finalizeInvoice();

        // 5. Return the hosted invoice URL
        return stripeInvoice.getHostedInvoiceUrl();
    }

    /* ---------- helpers ---------- */

    private String findOrCreateCustomer(Users user) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        List<Customer> existing = Customer.list(params).getData();
        if (!existing.isEmpty()) {
            return existing.get(0).getId();
        }
        return createStripeCustomer(user);
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}