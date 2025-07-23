### 8. AssignmentController Tests (`/api/assignments`)

*   **Create Assignment (Standalone)**
    *   **Endpoint:** `/api/assignments`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "title": "Standalone Assignment",
            "description": "This is a standalone assignment.",
            "dueDate": "2025-08-15T17:00:00",
            "maxPoints": 50
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 201,
            "title": "Standalone Assignment",
            "description": "This is a standalone assignment.",
            "dueDate": "2025-08-15T17:00:00",
            "maxPoints": 50
        }
        ```

*   **Submit an Assignment**
    *   **Endpoint:** `/api/assignments/{assignmentId}/submit`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Query Parameters:** `userId`
    *   **Request Body (form-data):**
        *   `content`: "Here is my text-based submission."
        *   (Optional) `filePath`: `/uploads/submission1.zip`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 301,
            "submissionDate": "2025-07-20T12:30:00",
            "content": "Here is my text-based submission.",
            "filePath": null,
            "grade": null,
            "feedback": null
        }
        ```
    *   **Note:** Save the `id` from the response for grading (e.g., `submissionId=301`).

*   **Get Submissions for an Assignment (Instructor View)**
    *   **Endpoint:** `/api/assignments/{assignmentId}/submissions`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 301,
                "submissionDate": "2025-07-20T12:30:00",
                "content": "Here is my text-based submission.",
                "filePath": null,
                "grade": null,
                "feedback": null
            }
        ]
        ```

*   **Grade a Submission (Instructor View)**
    *   **Endpoint:** `/api/submissions/{submissionId}/grade`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Query Parameters:** `grade`, `feedback`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 301,
            "submissionDate": "2025-07-20T12:30:00",
            "content": "Here is my text-based submission.",
            "filePath": null,
            "grade": 95.5,
            "feedback": "Excellent work! Great job on the setup."
        }
        ```
