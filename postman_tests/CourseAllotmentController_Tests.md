### 5. CourseAllotmentController Tests (`/api/courses/{courseId}/modules`)

*   **Add a Course Module**
    *   **Endpoint:** `/api/courses/{courseId}/modules`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "title": "Module 1: Getting Started",
            "description": "Introduction to the course and basic concepts.",
            "moduleOrder": 1
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "Module 1: Getting Started",
            "description": "Introduction to the course and basic concepts.",
            "moduleOrder": 1
        }
        ```
    *   **Note:** Save the `id` from the response to use in other tests (e.g., `moduleId=1`).

*   **Get All Modules for a Course**
    *   **Endpoint:** `/api/courses/{courseId}/modules`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "title": "Module 1: Getting Started",
                "description": "Introduction to the course and basic concepts.",
                "moduleOrder": 1
            }
        ]
        ```
