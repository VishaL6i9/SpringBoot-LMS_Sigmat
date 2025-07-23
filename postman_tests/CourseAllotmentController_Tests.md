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

*   **Get All Modules for a Course (Enhanced with DTOs)**
    *   **Endpoint:** `/api/courses/{courseId}/modules`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status:** `200 OK`
    *   **Description:** Returns CourseModuleDTO objects with safe lesson serialization
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "title": "Module 1: Getting Started",
                "description": "Introduction to the course and basic concepts.",
                "moduleOrder": 1,
                "lessons": [
                    {
                        "id": 101,
                        "title": "Lesson 1.1: Introduction",
                        "lessonOrder": 1,
                        "type": "video",
                        "content": null,
                        "videoId": 1
                    },
                    {
                        "id": 102,
                        "title": "Lesson 1.2: Reading Material",
                        "lessonOrder": 2,
                        "type": "article",
                        "content": "Welcome to the course! This lesson covers...",
                        "videoId": null
                    },
                    {
                        "id": 103,
                        "title": "Quiz 1: Check Your Knowledge",
                        "lessonOrder": 3,
                        "type": "quiz",
                        "content": null,
                        "videoId": null
                    },
                    {
                        "id": 104,
                        "title": "Assignment 1: Setup Environment",
                        "lessonOrder": 4,
                        "type": "assignment",
                        "content": null,
                        "videoId": null
                    }
                ]
            }
        ]
        ```
    *   **Note:** The response now includes lessons with proper type discrimination and safe content handling.
