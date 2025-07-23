### 14. CheckoutController Tests (`/api/checkout`)

**NOTE: Most endpoints in this controller are DEPRECATED. Use SubscriptionController endpoints instead.**

#### Active Endpoints

*   **Create Checkout Session (Legacy + New)**
    *   **Endpoint:** `/api/checkout/create-checkout-session`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Description:** Supports both legacy tier-based and new plan-based checkout flows
    
    **Legacy Tier-Based Request:**
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
    
    **New Plan-Based Request:**
    ```json
    {
        "planId": 2,
        "durationMonths": 6,
        "successUrl": "http://localhost:3000/success",
        "cancelUrl": "http://localhost:3000/cancel",
        "userId": 1,
        "courseId": null,
        "instructorId": null
    }
    ```
    
    *   **Expected Status (USER):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "sessionUrl": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0"
        }
        ```

#### DEPRECATED Endpoints

*   **Create Platform Subscription Checkout (DEPRECATED)**
    *   **Endpoint:** `/api/checkout/platform-subscription`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Status:** `301 Moved Permanently`
    *   **Response:** Redirects to `/api/subscriptions/users/{userId}/checkout`
    *   **Message:** "This endpoint has been moved to /api/subscriptions/users/{userId}/checkout"

*   **Create Course Subscription Checkout (DEPRECATED)**
    *   **Endpoint:** `/api/checkout/course-subscription`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Status:** `301 Moved Permanently`
    *   **Response:** Redirects to `/api/subscriptions/courses/{courseId}/users/{userId}/checkout`
    *   **Message:** "This endpoint has been moved to /api/subscriptions/courses/{courseId}/users/{userId}/checkout"

*   **Handle Checkout Success (DEPRECATED)**
    *   **Endpoint:** `/api/checkout/success`
    *   **Method:** `POST`
    *   **Status:** `301 Moved Permanently`
    *   **Response:** Redirects to `/api/subscriptions/checkout/success`
    *   **Message:** "This endpoint has been moved to /api/subscriptions/checkout/success"

#### Migration Guide

For new implementations, use the following SubscriptionController endpoints:

1. **Platform Subscriptions:** `POST /api/subscriptions/users/{userId}/checkout`
2. **Course Subscriptions:** `POST /api/subscriptions/courses/{courseId}/users/{userId}/checkout`
3. **Success Handling:** `POST /api/subscriptions/checkout/success`

See `SubscriptionController_Tests.md` for detailed documentation of the new endpoints.
