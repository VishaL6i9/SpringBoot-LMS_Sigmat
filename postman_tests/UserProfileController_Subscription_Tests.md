### 19. UserProfileController Subscription Tests (`/api/user`)

*   **Get User's Current Subscription**
    *   **Endpoint:** `/api/user/subscription/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK` or `204 No Content`
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
                "priceInr": 849.00
            },
            "status": "ACTIVE",
            "startDate": "2025-01-15T10:00:00",
            "endDate": "2025-04-15T10:00:00",
            "autoRenew": true
        }
        ```

*   **Get All User's Subscriptions**
    *   **Endpoint:** `/api/user/subscriptions/{userId}`
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
                "endDate": "2025-04-15T10:00:00"
            },
            {
                "id": 2,
                "userId": 123,
                "username": "john_doe",
                "subscriptionPlan": {
                    "id": 1,
                    "name": "Foundation",
                    "planType": "LEARNER"
                },
                "status": "EXPIRED",
                "startDate": "2024-10-15T10:00:00",
                "endDate": "2025-01-15T10:00:00"
            }
        ]
        ```
