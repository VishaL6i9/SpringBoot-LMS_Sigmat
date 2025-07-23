### 14. CheckoutController Tests (`/api/checkout`)

*   **Create Checkout Session**
    *   **Endpoint:** `/api/checkout/create-checkout-session`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Request Body (JSON):**
        ```json
        {
            "tier": "premium",
            "successUrl": "http://localhost:3000/success",
            "cancelUrl": "http://localhost:3000/cancel",
            "userId": 1,
            "courseId": 1,
            "instructorId": null
        }
        ```
    *   **Expected Status (USER):** `200 OK` (with session URL)
    *   **Sample Response Body (String):**
        ```
        "https://checkout.stripe.com/c/pay/cs_test_..."
        ```
