### 17. SubscriptionController Tests (`/api/subscriptions`)

#### Subscription Plan Endpoints (Public)

*   **Get All Subscription Plans (Platform-wide)**
    *   **Endpoint:** `/api/subscriptions/plans`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "courseId": null,
                "name": "Foundation",
                "planType": "LEARNER",
                "learnerTier": "FOUNDATION",
                "facultyTier": null,
                "priceInr": 0.00,
                "description": "Access public course catalog, register, attend one free course, forum participation",
                "features": [
                    "Access public course catalog",
                    "Register for courses",
                    "Attend one free course",
                    "Forum participation"
                ],
                "bestSuitedFor": "New users exploring the platform",
                "active": true,
                "minimumDurationMonths": 1,
                "customPricing": false
            },
            {
                "id": 2,
                "courseId": null,
                "name": "Essential",
                "planType": "LEARNER",
                "learnerTier": "ESSENTIAL",
                "facultyTier": null,
                "priceInr": 849.00,
                "description": "Full course access, assignment submission, discussion forums, certificate download",
                "features": [
                    "Full course access",
                    "Assignment submission",
                    "Discussion forums",
                    "Certificate download"
                ],
                "bestSuitedFor": "Learners pursuing self-paced study",
                "active": true,
                "minimumDurationMonths": 3,
                "customPricing": false
            }
        ]
        ```

*   **Get All Subscription Plans (Course-specific)**
    *   **Endpoint:** `/api/subscriptions/plans?courseId={courseId}`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 10,
                "courseId": 1,
                "name": "Course A Premium",
                "planType": "LEARNER",
                "learnerTier": "ESSENTIAL",
                "facultyTier": null,
                "priceInr": 500.00,
                "description": "Full access to Course A content",
                "features": [
                    "All lessons in Course A",
                    "Assignments and quizzes for Course A"
                ],
                "bestSuitedFor": "Learners interested in Course A",
                "active": true,
                "minimumDurationMonths": 1,
                "customPricing": false
            }
        ]
        ```

*   **Get Learner Plans**
    *   **Endpoint:** `/api/subscriptions/plans/learner`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Get Faculty Plans**
    *   **Endpoint:** `/api/subscriptions/plans/faculty`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Get Plan by ID**
    *   **Endpoint:** `/api/subscriptions/plans/{planId}`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Subscribe User to Platform Plan**
    *   **Endpoint:** `/api/subscriptions/users/{userId}/subscribe`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 2,
            "autoRenew": true,
            "durationMonths": 3,
            "discountApplied": 0.00,
            "paymentReference": "stripe_payment_123"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "userId": 123,
            "username": "john_doe",
            "subscriptionPlan": {
                "id": 2,
                "name": "Essential",
                "planType": "LEARNER",
                "learnerTier": "ESSENTIAL",
                "priceInr": 849.00
            },
            "status": "ACTIVE",
            "startDate": "2025-01-15T10:00:00",
            "endDate": "2025-04-15T10:00:00",
            "autoRenew": true,
            "actualPrice": 849.00,
            "discountApplied": 0.00,
            "paymentReference": "stripe_payment_123",
            "createdAt": "2025-01-15T10:00:00",
            "updatedAt": "2025-01-15T10:00:00"
        }
        ```

*   **Subscribe User to Course Plan**
    *   **Endpoint:** `/api/subscriptions/courses/{courseId}/users/{userId}/subscribe`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 10, // ID of a course-specific plan
            "autoRenew": true,
            "durationMonths": 1,
            "discountApplied": 0.00,
            "paymentReference": "stripe_course_payment_456"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 2,
            "userId": 123,
            "username": "john_doe",
            "subscriptionPlan": {
                "id": 10,
                "courseId": 1,
                "name": "Course A Premium",
                "planType": "LEARNER",
                "priceInr": 500.00
            },
            "courseId": 1,
            "status": "ACTIVE",
            "startDate": "2025-07-23T10:00:00",
            "endDate": "2025-08-23T10:00:00",
            "autoRenew": true,
            "actualPrice": 500.00,
            "discountApplied": 0.00,
            "paymentReference": "stripe_course_payment_456",
            "createdAt": "2025-07-23T10:00:00",
            "updatedAt": "2025-07-23T10:00:00"
        }
        ```

*   **Get User Subscriptions**
    *   **Endpoint:** `/api/subscriptions/users/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "userId": 123,
                "username": "john_doe",
                "subscriptionPlan": {
                    "id": 2,
                    "name": "Essential",
                    "planType": "LEARNER"
                },
                "status": "ACTIVE",
                "startDate": "2025-01-15T10:00:00",
                "endDate": "2025-04-15T10:00:00",
                "autoRenew": true
            },
            {
                "id": 2,
                "userId": 123,
                "username": "john_doe",
                "subscriptionPlan": {
                    "id": 10,
                    "courseId": 1,
                    "name": "Course A Premium",
                    "planType": "LEARNER"
                },
                "courseId": 1,
                "status": "ACTIVE",
                "startDate": "2025-07-23T10:00:00",
                "endDate": "2025-08-23T10:00:00",
                "autoRenew": true
            }
        ]
        ```

*   **Get Current User Platform Subscription**
    *   **Endpoint:** `/api/subscriptions/users/{userId}/current`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK` or `204 No Content`

*   **Get Current User Course Subscription**
    *   **Endpoint:** `/api/subscriptions/courses/{courseId}/users/{userId}/current`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK` or `204 No Content`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 2,
            "userId": 123,
            "username": "john_doe",
            "subscriptionPlan": {
                "id": 10,
                "courseId": 1,
                "name": "Course A Premium",
                "planType": "LEARNER",
                "priceInr": 500.00
            },
            "courseId": 1,
            "status": "ACTIVE",
            "startDate": "2025-07-23T10:00:00",
            "endDate": "2025-08-23T10:00:00",
            "autoRenew": true,
            "actualPrice": 500.00,
            "discountApplied": 0.00,
            "paymentReference": "stripe_course_payment_456",
            "createdAt": "2025-07-23T10:00:00",
            "updatedAt": "2025-07-23T10:00:00"
        }
        ```

*   **Get Learner Plans**
    *   **Endpoint:** `/api/subscriptions/plans/learner`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Get Faculty Plans**
    *   **Endpoint:** `/api/subscriptions/plans/faculty`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Get Plan by ID**
    *   **Endpoint:** `/api/subscriptions/plans/{planId}`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

#### Subscription Management Endpoints

*   **Subscribe User to Platform Plan**
    *   **Endpoint:** `/api/subscriptions/users/{userId}/subscribe`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 2,
            "autoRenew": true,
            "durationMonths": 6,
            "discountApplied": 0.00,
            "paymentReference": "stripe_payment_123"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Subscribe User to Course Plan**
    *   **Endpoint:** `/api/subscriptions/courses/{courseId}/users/{userId}/subscribe`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 10,
            "autoRenew": true,
            "durationMonths": 3,
            "discountApplied": 0.00,
            "paymentReference": "stripe_course_payment_456"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`

#### NEW: Stripe Checkout Integration Endpoints

*   **Create Platform Subscription Checkout Session**
    *   **Endpoint:** `/api/subscriptions/users/{userId}/checkout`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 2,
            "durationMonths": 6,
            "successUrl": "https://yourapp.com/subscription/success",
            "cancelUrl": "https://yourapp.com/subscription/cancel"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "sessionUrl": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
            "planId": 2,
            "userId": 123
        }
        ```

*   **Create Course Subscription Checkout Session**
    *   **Endpoint:** `/api/subscriptions/courses/{courseId}/users/{userId}/checkout`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 10,
            "durationMonths": 3,
            "successUrl": "https://yourapp.com/subscription/success",
            "cancelUrl": "https://yourapp.com/subscription/cancel"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "sessionUrl": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
            "planId": 10,
            "courseId": 1,
            "userId": 123
        }
        ```

*   **Handle Checkout Success**
    *   **Endpoint:** `/api/subscriptions/checkout/success?sessionId={sessionId}&userId={userId}`
    *   **Method:** `POST`
    *   **Required Role:** None (public - but validates session)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
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

*   **Stripe Webhook Handler**
    *   **Endpoint:** `/api/subscriptions/checkout/webhook`
    *   **Method:** `POST`
    *   **Required Role:** None (webhook)
    *   **Headers:** `Stripe-Signature: {signature}`
    *   **Request Body:** Raw Stripe webhook payload
    *   **Expected Status:** `200 OK`

*   **Cancel Subscription**
    *   **Endpoint:** `/api/subscriptions/{subscriptionId}/cancel`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Subscription Owner
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Expire Subscriptions (Manual Trigger)**
    *   **Endpoint:** `/api/subscriptions/expire-subscriptions`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `200 OK`
