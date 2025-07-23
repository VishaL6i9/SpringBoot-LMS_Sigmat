### 7. LessonController Tests (`/api/lessons`)

*   **Add a Lesson to a Module (Polymorphic)**
    *   **Endpoint:** `/api/lessons/module/{moduleId}`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Description:** This single endpoint handles all lesson types. The `type` field in the JSON body determines which kind of lesson is created.
    *   **Expected Status:** `200 OK`

    *   **Request Body Example (Video Lesson):**
        ```json
        {
          "type": "video",
          "title": "Lesson 1.1: Your First Application",
          "lessonOrder": 1,
          "video": { "id": 1 }
        }
        ```
        **Sample Response Body (Video Lesson):**
        ```json
        {
            "type": "video",
            "id": 101,
            "title": "Lesson 1.1: Your First Application",
            "lessonOrder": 1
        }
        ```

    *   **Request Body Example (Article Lesson):**
        ```json
        {
          "type": "article",
          "title": "Lesson 1.2: Reading Material",
          "lessonOrder": 2,
          "content": "<h1>Welcome</h1><p>This is the core reading for this section.</p>"
        }
        ```
        **Sample Response Body (Article Lesson):**
        ```json
        {
            "type": "article",
            "id": 102,
            "title": "Lesson 1.2: Reading Material",
            "lessonOrder": 2,
            "content": "<h1>Welcome</h1><p>This is the core reading for this section.</p>"
        }
        ```

    *   **Request Body Example (Assignment Lesson):**
        ```json
        {
            "type": "assignment",
            "title": "Homework 1: Setup Your Environment",
            "lessonOrder": 3,
            "description": "Follow the guide to install all necessary tools.",
            "dueDate": "2025-08-01T23:59:59",
            "maxPoints": 100
        }
        ```
        **Sample Response Body (Assignment Lesson):**
        ```json
        {
            "type": "assignment",
            "id": 103,
            "title": "Homework 1: Setup Your Environment",
            "lessonOrder": 3,
            "description": "Follow the guide to install all necessary tools.",
            "dueDate": "2025-08-01T23:59:59",
            "maxPoints": 100
        }
        ```
    *   **Note:** Save the `id` from the assignment response for submission tests (e.g., `assignmentId=103`).

    *   **Request Body Example (Quiz Lesson):**
        ```json
        {
            "type": "quiz",
            "title": "Quiz 1: Check Your Knowledge",
            "lessonOrder": 4
        }
        ```
        **Sample Response Body (Quiz Lesson):**
        ```json
        {
            "type": "quiz",
            "id": 104,
            "title": "Quiz 1: Check Your Knowledge",
            "lessonOrder": 4
        }
        ```
    *   **Note:** Save the `id` from the quiz response for adding questions (e.g., `quizId=104`).

*   **Get Lesson by ID**
    *   **Endpoint:** `/api/lessons/{lessonId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (Video Lesson):**
        ```json
        {
            "type": "video",
            "id": 101,
            "title": "Lesson 1.1: Your First Application",
            "lessonOrder": 1
        }
        ```

*   **Delete Lesson**
    *   **Endpoint:** `/api/lessons/{lessonId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status:** `204 No Content`
