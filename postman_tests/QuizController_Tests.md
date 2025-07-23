### 9. QuizController Tests (`/api/quizzes`)

*   **Create Quiz (Standalone)**
    *   **Endpoint:** `/api/quizzes`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "title": "Standalone Quiz",
            "lessonOrder": 1
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "type": "quiz",
            "id": 202,
            "title": "Standalone Quiz",
            "lessonOrder": 1
        }
        ```

*   **Get a Quiz**
    *   **Endpoint:** `/api/quizzes/{quizId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "type": "quiz",
            "id": 104,
            "title": "Quiz 1: Check Your Knowledge",
            "lessonOrder": 4,
            "questions": [
                {
                    "id": 3,
                    "questionText": "What is the capital of France?",
                    "answerChoices": [
                        { "id": 1, "choiceText": "Berlin", "isCorrect": false },
                        { "id": 2, "choiceText": "Madrid", "isCorrect": false },
                        { "id": 3, "choiceText": "Paris", "isCorrect": true },
                        { "id": 4, "choiceText": "Rome", "isCorrect": false }
                    ]
                }
            ]
        }
        ```

*   **Add a Question to a Quiz**
    *   **Endpoint:** `/api/quizzes/{quizId}/questions`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "questionText": "What is the capital of France?",
            "answerChoices": [
                { "choiceText": "Berlin", "isCorrect": false },
                { "choiceText": "Madrid", "isCorrect": false },
                { "choiceText": "Paris", "isCorrect": true },
                { "choiceText": "Rome", "isCorrect": false }
            ]
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 3,
            "questionText": "What is the capital of France?",
            "answerChoices": [
                { "id": 1, "choiceText": "Berlin", "isCorrect": false },
                { "id": 2, "choiceText": "Madrid", "isCorrect": false },
                { "id": 3, "choiceText": "Paris", "isCorrect": true },
                { "id": 4, "choiceText": "Rome", "isCorrect": false }
            ]
        }
        ```

*   **Delete a Question from a Quiz**
    *   **Endpoint:** `/api/quizzes/questions/{questionId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status:** `204 No Content`
