### 18. AdminSubscriptionController Tests (`/api/admin/subscriptions`)

**All endpoints in this controller require `ADMIN` role.**

*   **Get All Plans (Including Inactive)**
    *   **Endpoint:** `/api/admin/subscriptions/plans/all`
    *   **Method:** `GET`
    *   **Expected Status (ADMIN):** `200 OK`

*   **Create Subscription Plan**
    *   **Endpoint:** `/api/admin/subscriptions/plans`
    *   **Method:** `POST`
    *   **Request Body (JSON):**
        ```json
        {
            "name": "Custom Plan",
            "planType": "LEARNER",
            "learnerTier": "PROFESSIONAL",
            "priceInr": 2500.00,
            "description": "Custom professional plan",
            "features": [
                "All professional features",
                "Custom support"
            ],
            "bestSuitedFor": "Custom users",
            "active": true,
            "minimumDurationMonths": 6,
            "customPricing": false,
            "courseId": null // Optional: Include for course-specific plans
        }
        ```
    *   **Expected Status (ADMIN):** `200 OK`

*   **Update Subscription Plan**
    *   **Endpoint:** `/api/admin/subscriptions/plans/{planId}`
    *   **Method:** `PUT`
    *   **Request Body (JSON):**
        ```json
        {
            "name": "Updated Plan Name",
            "planType": "LEARNER",
            "learnerTier": "PROFESSIONAL",
            "priceInr": 2800.00,
            "description": "Updated description",
            "features": [
                "Updated features"
            ],
            "bestSuitedFor": "Updated target audience",
            "active": true,
            "minimumDurationMonths": 6,
            "customPricing": false,
            "courseId": null // Optional: Include for course-specific plans
        }
        ```
    *   **Expected Status (ADMIN):** `200 OK`

*   **Deactivate Subscription Plan**
    *   **Endpoint:** `/api/admin/subscriptions/plans/{planId}`
    *   **Method:** `DELETE`
    *   **Expected Status (ADMIN):** `200 OK`

*   **Get All User Subscriptions**
    *   **Endpoint:** `/api/admin/subscriptions/users/all`
    *   **Method:** `GET`
    *   **Expected Status (ADMIN):** `200 OK`

*   **Get Active User Subscriptions**
    *   **Endpoint:** `/api/admin/subscriptions/users/active`
    *   **Method:** `GET`
    *   **Expected Status (ADMIN):** `200 OK`

*   **Expire All Subscriptions**
    *   **Endpoint:** `/api/admin/subscriptions/expire-all`
    *   **Method:** `POST`
    *   **Expected Status (ADMIN):** `200 OK`
