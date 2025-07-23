### 6. CourseModuleController Tests (`/api/modules`)

*   **Update Module**
    *   **Endpoint:** `/api/modules/{moduleId}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "title": "Module 1: Updated Title",
            "description": "Updated description.",
            "moduleOrder": 1
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "Module 1: Updated Title",
            "description": "Updated description.",
            "moduleOrder": 1
        }
        ```

*   **Delete Module**
    *   **Endpoint:** `/api/modules/{moduleId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status:** `204 No Content`
