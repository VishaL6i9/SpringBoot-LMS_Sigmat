### 1. AttendanceController Tests (`/api/user/attendance`)

*   **Get All Attendance Records for Authenticated User**
    *   **Endpoint:** `/api/user/attendance`
    *   **Method:** `GET`
    *   **Required Role:** Authenticated (any role)
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "userId": 101,
                "timestamp": "2025-07-26T10:30:00"
            },
            {
                "id": 2,
                "userId": 101,
                "timestamp": "2025-07-27T09:00:00"
            }
        ]
        ```

*   **Get Attendance Records for Authenticated User within a Date Range**
    *   **Endpoint:** `/api/user/attendance/range`
    *   **Method:** `GET`
    *   **Required Role:** Authenticated (any role)
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Query Parameters:**
        *   `startDate`: `YYYY-MM-DDTHH:MM:SS` (e.g., `2025-07-20T00:00:00`)
        *   `endDate`: `YYYY-MM-DDTHH:MM:SS` (e.g., `2025-07-27T23:59:59`)
    *   **Example Request URL:**
        `http://localhost:8080/api/user/attendance/range?startDate=2025-07-20T00:00:00&endDate=2025-07-27T23:59:59`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "userId": 101,
                "timestamp": "2025-07-26T10:30:00"
            }
        ]
        ```