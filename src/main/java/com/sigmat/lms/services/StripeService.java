package com.sigmat.lms.services;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public StripeService(@Value("${stripe.api.key}") String stripeApiKey) {
        Stripe.apiKey = stripeApiKey;
    }

    public String createCheckoutSession(String tier, String successUrl, String cancelUrl) throws Exception {
        long amount = 0L;

        switch (tier.toLowerCase()) {
            case "starter":
                amount = 3500L; // $35.00
                break;
            case "professional":
                amount = 6500L; // $65.00
                break;
            case "enterprise":
                amount = 12500L; // $125.00
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription tier");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Use addPaymentMethodType instead of setPaymentMethodTypes
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L) 
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd") 
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(tier.substring(0, 1).toUpperCase() + tier.substring(1) + " Subscription") 
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
}