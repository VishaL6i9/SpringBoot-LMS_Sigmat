### Stripe Checkout Integration Tests

This document provides comprehensive testing scenarios for the new Stripe checkout integration with the subscription system.

## Prerequisites

1. **Stripe Configuration:** Ensure `stripe.api.key` is configured in `application.properties`
2. **Test Mode:** Use Stripe test keys for development/testing
3. **Authentication:** Most endpoints require JWT authentication
4. **User Setup:** Create test users with appropriate roles

## Test Flow Overview

```
1. Get Available Plans → 2. Create Checkout Session → 3. Redirect to Stripe → 4. Handle Success → 5. Verify Subscription
```

## 1. Platform Subscription Checkout Flow

### Step 1: Get Available Platform Plans
*   **Endpoint:** `/api/subscriptions/plans`
*   **Method:** `GET`
*   **Required Role:** None (public)
*   **Expected Status:** `200 OK`

### Step 2: Create Platform Subscription Checkout Session
*   **Endpoint:** `/api/subscriptions/users/{userId}/checkout`
*   **Method:** `POST`
*   **Required Role:** `ADMIN` or Owner
*   **Headers:**
    ```
    Authorization: Bearer {jwt_token}
    Content-Type: application/json
    ```
*   **Request Body (JSON):**
    ```json
    {
        "planId": 2,
        "durationMonths": 6,
        "successUrl": "http://localhost:3000/subscription/success",
        "cancelUrl": "http://localhost:3000/subscription/cancel"
    }
    ```
*   **Expected Status:** `200 OK`
*   **Sample Response:**
    ```json
    {
        "sessionUrl": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
        "planId": 2,
        "userId": 123
    }
    ```

### Step 3: Simulate Stripe Checkout Success
*   **Action:** In a real scenario, user would be redirected to `sessionUrl` and complete payment
*   **For Testing:** Use Stripe test card numbers (e.g., `4242424242424242`)
*   **Success Redirect:** User returns to success URL with `session_id` parameter

### Step 4: Process Checkout Success
*   **Endpoint:** `/api/subscriptions/checkout/success?sessionId={sessionId}&userId={userId}`
*   **Method:** `POST`
*   **Required Role:** None (validates session internally)
*   **Expected Status:** `200 OK`
*   **Sample Response:**
    ```json
    {
        "subscription": {
            "id": 789,
            "userId": 123,
            "username": "john_doe",
            "subscriptionPlan": {
                "id": 2,
                "name": "Essential",
                "planType": "LEARNER",
                "priceInr": 849.00
            },
            "courseId": null,
            "status": "ACTIVE",
            "startDate": "2025-01-15T10:00:00",
            "endDate": "2025-07-15T10:00:00",
            "autoRenew": true,
            "actualPrice": 849.00,
            "discountApplied": 0.00,
            "paymentReference": "stripe_session_cs_test_...",
            "createdAt": "2025-01-15T10:00:00",
            "updatedAt": "2025-01-15T10:00:00"
        },
        "sessionId": "cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
        "message": "Subscription created successfully"
    }
    ```

### Step 5: Verify Subscription Creation
*   **Endpoint:** `/api/subscriptions/users/{userId}/current`
*   **Method:** `GET`
*   **Required Role:** `ADMIN` or Owner
*   **Expected Status:** `200 OK`

## 2. Course Subscription Checkout Flow

### Step 1: Get Course-Specific Plans
*   **Endpoint:** `/api/subscriptions/plans?courseId={courseId}`
*   **Method:** `GET`
*   **Required Role:** None (public)
*   **Expected Status:** `200 OK`

### Step 2: Create Course Subscription Checkout Session
*   **Endpoint:** `/api/subscriptions/courses/{courseId}/users/{userId}/checkout`
*   **Method:** `POST`
*   **Required Role:** `ADMIN` or Owner
*   **Request Body (JSON):**
    ```json
    {
        "planId": 10,
        "durationMonths": 3,
        "successUrl": "http://localhost:3000/subscription/success",
        "cancelUrl": "http://localhost:3000/subscription/cancel"
    }
    ```
*   **Expected Status:** `200 OK`
*   **Sample Response:**
    ```json
    {
        "sessionUrl": "https://checkout.stripe.com/pay/cs_test_b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0a1",
        "planId": 10,
        "courseId": 1,
        "userId": 123
    }
    ```

### Step 3: Process Course Subscription Success
*   **Endpoint:** `/api/subscriptions/checkout/success?sessionId={sessionId}&userId={userId}`
*   **Method:** `POST`
*   **Expected Response:** Similar to platform subscription but includes `courseId`

### Step 4: Verify Course Subscription
*   **Endpoint:** `/api/subscriptions/courses/{courseId}/users/{userId}/current`
*   **Method:** `GET`
*   **Required Role:** `ADMIN` or Owner
*   **Expected Status:** `200 OK`

## 3. Legacy Checkout Session Tests

### Legacy Tier-Based Checkout
*   **Endpoint:** `/api/checkout/create-checkout-session`
*   **Method:** `POST`
*   **Required Role:** `USER`
*   **Request Body (JSON):**
    ```json
    {
        "tier": "professional",
        "successUrl": "http://localhost:3000/success",
        "cancelUrl": "http://localhost:3000/cancel",
        "userId": 1,
        "courseId": 1,
        "instructorId": null
    }
    ```
*   **Expected Status:** `200 OK`
*   **Note:** This creates a legacy Stripe subscription, not a platform subscription

### New Plan-Based Checkout (via legacy endpoint)
*   **Endpoint:** `/api/checkout/create-checkout-session`
*   **Method:** `POST`
*   **Required Role:** `USER`
*   **Request Body (JSON):**
    ```json
    {
        "planId": 2,
        "durationMonths": 6,
        "successUrl": "http://localhost:3000/success",
        "cancelUrl": "http://localhost:3000/cancel"
    }
    ```
*   **Expected Status:** `200 OK`

## 4. Error Scenarios Testing

### Invalid Plan ID
*   **Request:** Use non-existent `planId`
*   **Expected Status:** `400 Bad Request`
*   **Expected Message:** "Subscription plan not found with id: {planId}"

### Inactive Plan
*   **Request:** Use `planId` of inactive plan
*   **Expected Status:** `400 Bad Request`
*   **Expected Message:** "Subscription plan is not active"

### Course Plan Mismatch
*   **Request:** Use course plan ID for platform checkout or vice versa
*   **Expected Status:** `400 Bad Request`
*   **Expected Message:** "Use course-specific checkout endpoint for course plans"

### Unauthorized Access
*   **Request:** Access another user's checkout without admin role
*   **Expected Status:** `403 Forbidden`

### Invalid Session ID
*   **Request:** Use invalid `sessionId` in success handler
*   **Expected Status:** `500 Internal Server Error`

### Incomplete Payment
*   **Request:** Use session ID with incomplete payment status
*   **Expected Status:** `400 Bad Request`
*   **Expected Message:** "Payment not completed"

## 5. Stripe Test Cards

Use these test card numbers for different scenarios:

### Successful Payments
- **Visa:** `4242424242424242`
- **Visa (debit):** `4000056655665556`
- **Mastercard:** `5555555555554444`

### Failed Payments
- **Generic decline:** `4000000000000002`
- **Insufficient funds:** `4000000000009995`
- **Lost card:** `4000000000009987`

### 3D Secure Authentication
- **Requires authentication:** `4000002500003155`
- **Authentication fails:** `4000008400001629`

## 6. Webhook Testing

### Setup Stripe CLI for Local Testing
```bash
stripe listen --forward-to localhost:8080/api/subscriptions/checkout/webhook
```

### Test Webhook Events
*   **Endpoint:** `/api/subscriptions/checkout/webhook`
*   **Method:** `POST`
*   **Headers:**
    ```
    Stripe-Signature: {webhook_signature}
    Content-Type: application/json
    ```
*   **Body:** Raw Stripe webhook payload
*   **Expected Status:** `200 OK`

## 7. Integration Test Checklist

- [ ] Platform subscription checkout creates valid Stripe session
- [ ] Course subscription checkout creates valid Stripe session
- [ ] Session metadata includes all required fields (plan_id, user_id, duration_months, course_id)
- [ ] Success handler validates payment completion
- [ ] Success handler creates subscription in database
- [ ] Success handler handles both platform and course subscriptions
- [ ] Error handling works for all failure scenarios
- [ ] Legacy endpoints return proper deprecation responses
- [ ] Webhook endpoint accepts Stripe events (placeholder implementation)
- [ ] Currency conversion works correctly (INR to USD)
- [ ] Session URLs are properly formatted and accessible

## 8. Performance Testing

### Load Testing Scenarios
1. **Concurrent Checkout Sessions:** Multiple users creating checkout sessions simultaneously
2. **Success Handler Load:** Multiple success callbacks processing simultaneously
3. **Plan Lookup Performance:** High-frequency plan retrieval requests

### Monitoring Points
- Stripe API response times
- Database transaction performance
- Session creation success rate
- Payment processing completion rate

## 9. Security Testing

### Authentication Tests
- [ ] Unauthenticated requests are rejected
- [ ] Users can only access their own checkout sessions
- [ ] Admin users can access any user's checkout sessions

### Session Validation
- [ ] Session ID validation prevents tampering
- [ ] Payment status verification prevents fraud
- [ ] Metadata validation ensures data integrity

### Input Validation
- [ ] Plan ID validation prevents injection attacks
- [ ] Duration validation prevents negative values
- [ ] URL validation prevents malicious redirects

## 10. Troubleshooting Common Issues

### "Plan not found" Error
- Verify plan ID exists in database
- Check if plan is active
- Ensure correct courseId for course-specific plans

### "Payment not completed" Error
- Check Stripe dashboard for payment status
- Verify session ID is correct
- Ensure sufficient time between payment and success callback

### Session Creation Failures
- Verify Stripe API key configuration
- Check network connectivity to Stripe
- Validate request parameters

### Subscription Creation Failures
- Check database connectivity
- Verify user exists
- Ensure no duplicate active subscriptions