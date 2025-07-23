### 10. NotificationController Tests (`/api/notifications`)

*   **Create Notification**
    *   **Endpoint:** `/api/notifications?userId={userId}`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "title": "New Notification",
            "message": "This is a test notification.",
            "type": "SYSTEM",
            "category": "ANNOUNCEMENT",
            "priority": "HIGH"
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "New Notification",
            "message": "This is a test notification.",
            "type": "SYSTEM",
            "category": "ANNOUNCEMENT",
            "priority": "HIGH",
            "read": false,
            "createdAt": "2025-07-20T12:00:00"
        }
        ```

*   **Send Bulk Notifications**
    *   **Endpoint:** `/api/notifications/bulk?userIds={userId1},{userId2}`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "title": "Bulk Notification",
            "message": "This is a notification for multiple users.",
            "type": "COURSE",
            "category": "ALERT",
            "priority": "MEDIUM"
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 2,
                "title": "Bulk Notification",
                "message": "This is a notification for multiple users.",
                "type": "COURSE",
                "category": "ALERT",
                "priority": "MEDIUM",
                "read": false,
                "createdAt": "2025-07-20T12:01:00"
            }
        ]
        ```

*   **Get User Notifications**
    *   **Endpoint:** `/api/notifications/user/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "title": "New Notification",
                "message": "This is a test notification.",
                "type": "SYSTEM",
                "category": "ANNOUNCEMENT",
                "priority": "HIGH",
                "read": false,
                "createdAt": "2025-07-20T12:00:00"
            }
        ]
        ```

*   **Get Unread Notifications**
    *   **Endpoint:** `/api/notifications/user/{userId}/unread`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`

*   **Mark Notification as Read**
    *   **Endpoint:** `/api/notifications/{id}/read`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`

*   **Mark All Notifications as Read**
    *   **Endpoint:** `/api/notifications/user/{userId}/read-all`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`

*   **Get Notification Stats**
    *   **Endpoint:** `/api/notifications/user/{userId}/stats`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "totalNotifications": 5,
            "unreadNotifications": 2
        }
        ```
