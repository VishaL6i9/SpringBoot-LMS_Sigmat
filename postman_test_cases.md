Here are sample test cases for Postman to verify the role-based access control and new LMS features.

**Before you start:**

*   **Ensure you have users with `ADMIN`, `INSTRUCTOR`, and `USER` roles in your database.** If not, you'll need to create them first.
*   **Authentication:** All protected endpoints require a JWT token in the `Authorization` header (e.g., `Bearer <your_jwt_token>`). You'll need to obtain this token by authenticating a user.

---

### 1. AuthController Tests (`/api/public`)

*   **Get All Users (Public)**
    *   **Endpoint:** `/api/public/users`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "username": "user1",
                "email": "user1@example.com",
                "firstName": "John",
                "lastName": "Doe",
                "roles": ["USER"],
                "userProfile": null
            }
        ]
        ```

*   **Login**
    *   **Endpoint:** `/api/public/login`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (JSON):**
        ```json
        {
            "username": "your_username",
            "password": "your_password"
        }
        ```
    *   **Expected Response:** A JWT token in the response body.

*   **Register User**
    *   **Endpoint:** `/api/public/register/user`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (JSON):**
        ```json
        {
            "username": "newuser",
            "password": "password123",
            "email": "newuser@example.com",
            "firstName": "New",
            "lastName": "User",
            "roles": ["USER"]
        }
        ```
    *   **Expected Status:** `200 OK`

*   **Register Batch Users**
    *   **Endpoint:** `/api/public/register/batch`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (form-data):**
        *   `file`: Select a CSV or Excel file with user data.
    *   **Expected Status:** `200 OK`

*   **Logout**
    *   **Endpoint:** `/api/public/logout`
    *   **Method:** `POST`
    *   **Required Role:** Authenticated (any role)
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status:** `200 OK`

*   **Get User Role**
    *   **Endpoint:** `/api/public/role`
    *   **Method:** `GET`
    *   **Required Role:** Authenticated (any role)
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            "USER"
        ]
        ```

*   **Test Public Endpoint**
    *   **Endpoint:** `/api/public/test`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Verify Email**
    *   **Endpoint:** `/api/public/verify-email?token={token}`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

---

### 2. AdminController Tests (`/api/admin`)

**All endpoints in this controller require `ADMIN` role.**

*   **Get All Users**
    *   **Endpoint:** `/api/admin/users`
    *   **Method:** `GET`
    *   **Expected Status (ADMIN):** `200 OK`

*   **Delete User by Username**
    *   **Endpoint:** `/api/admin/delete/user/{username}`
    *   **Method:** `DELETE`
    *   **Expected Status (ADMIN):** `200 OK`

*   **Delete Users in Batch**
    *   **Endpoint:** `/api/admin/delete/users/batch`
    *   **Method:** `POST`
    *   **Request Body (form-data):**
        *   `file`: Select a CSV or Excel file containing usernames to delete.
    *   **Expected Status (ADMIN):** `200 OK`

*   **Change User Role**
    *   **Endpoint:** `/api/admin/user/{userId}/role?newRole={newRole}`
    *   **Method:** `PUT`
    *   **Expected Status (ADMIN):** `200 OK`

---

### 3. UserProfileController Tests (`/api/user`)

**All endpoints in this controller require a valid JWT token in the `Authorization` header.** The server will programmatically check if the user is an `ADMIN` or is accessing their own resource.

*   **Get User Profile by ID**
    *   **Endpoint:** `/api/user/profile/{userID}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "phone": "123-456-7890",
            "timezone": "UTC",
            "language": "en",
            "address": "123 Main St",
            "profileImage": null,
            "users": {
                "id": 1
            }
        }
        ```

*   **Update User Profile**
    *   **Endpoint:** `/api/user/profile/{userId}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "firstName": "UpdatedFirstName",
            "lastName": "UpdatedLastName",
            "email": "updated.email@example.com",
            "phone": "123-456-7890",
            "timezone": "UTC",
            "language": "en",
            "address": "456 New St"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Request Password Reset**
    *   **Endpoint:** `/api/public/password-reset/request`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (JSON):**
        ```json
        {
            "email": "user@example.com"
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Expected Response Body:** `"Password reset email sent."`

*   **Reset Password**
    *   **Endpoint:** `/api/public/password-reset/reset`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (JSON):**
        ```json
        {
            "email": "user@example.com",
            "token": "the-token-from-the-email",
            "newPassword": "yourNewSecurePassword"
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Expected Response Body:** `"Password reset successful."`

*   **Update User Password**
    *   **Endpoint:** `/api/user/profile/password?userID={userID}&newPassword={newPassword}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Get User ID from JWT**
    *   **Endpoint:** `/api/user/profile/getuserID`
    *   **Method:** `GET`
    *   **Required Role:** Authenticated (any role)
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body:** `1` (the user's ID)

*   **Get Profile Image ID by User ID**
    *   **Endpoint:** `/api/user/profile/getProfileImageID/{userID}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body:** `1` (the profile image ID, or null if not set)

*   **Upload Profile Image**
    *   **Endpoint:** `/api/user/profile/pic/upload/{userId}`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (form-data):**
        *   `file`: Select an image file.
    *   **Expected Status:** `200 OK`

*   **Get Profile Picture**
    *   **Endpoint:** `/api/user/profile/pic/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status:** `200 OK` (with image data)

*   **Enroll User in Course**
    *   **Endpoint:** `/api/user/enroll?userId={userId}&courseId={courseId}&instructorId={instructorId}`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "userId": 1,
            "username": "testuser",
            "courseId": 1,
            "courseName": "Sample Course",
            "instructorId": 1,
            "instructorName": "John Doe",
            "enrollmentDate": "2025-07-20"
        }
        ```

*   **Get User Enrollments**
    *   **Endpoint:** `/api/user/enrollments/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "userId": 1,
                "username": "testuser",
                "courseId": 1,
                "courseName": "Sample Course",
                "instructorId": 1,
                "instructorName": "John Doe",
                "enrollmentDate": "2025-07-20"
            }
        ]
        ```

---

### 4. CourseController Tests (`/api/courses`)

*   **Create Course**
    *   **Endpoint:** `/api/courses`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "courseName": "New Course Title",
            "courseCode": "NC001",
            "courseDescription": "A detailed description of the new course.",
            "courseDuration": 60,
            "courseMode": "Online",
            "maxEnrollments": 100,
            "courseFee": 99.99,
            "language": "English",
            "courseCategory": "Software Development",
            "instructors": [
                { "instructorId": 1 }
            ]
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "courseId": 1,
            "courseName": "New Course Title",
            "courseCode": "NC001",
            "courseDescription": "A detailed description of the new course.",
            "courseDuration": 60,
            "courseMode": "Online",
            "maxEnrollments": 100,
            "courseFee": 99.99,
            "language": "English",
            "courseCategory": "Software Development"
        }
        ```

*   **Update Course**
    *   **Endpoint:** `/api/courses/{courseId}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "courseId": 1,
            "courseName": "Updated Course Title",
            "courseCode": "UC001",
            "courseDescription": "An updated description of the course.",
            "courseDuration": 75,
            "courseMode": "Blended",
            "maxEnrollments": 120,
            "courseFee": 129.99,
            "language": "Spanish",
            "courseCategory": "Web Development",
            "instructors": [
                { "instructorId": 1 },
                { "instructorId": 2 }
            ]
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "courseId": 1,
            "courseName": "Updated Course Title",
            "courseCode": "UC001",
            "courseDescription": "An updated description of the course.",
            "courseDuration": 75,
            "courseMode": "Blended",
            "maxEnrollments": 120,
            "courseFee": 129.99,
            "language": "Spanish",
            "courseCategory": "Web Development"
        }
        ```

*   **Get All Courses**
    *   **Endpoint:** `/api/courses`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "courseId": 1,
                "courseName": "Updated Course Title",
                "courseCode": "UC001",
                "courseDescription": "An updated description of the course.",
                "courseDuration": 75,
                "courseMode": "Blended",
                "maxEnrollments": 120,
                "courseFee": 129.99,
                "language": "Spanish",
                "courseCategory": "Web Development"
            },
            {
                "courseId": 2,
                "courseName": "Another Course",
                "courseCode": "AC002",
                "courseDescription": "Description for another course.",
                "courseDuration": 90,
                "courseMode": "Online",
                "maxEnrollments": 50,
                "courseFee": 149.99,
                "language": "English",
                "courseCategory": "Data Science"
            }
        ]
        ```

*   **Get Course by ID (Hierarchical)**
    *   **Endpoint:** `/api/courses/{courseId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "courseId": 1,
            "courseName": "Updated Course Title",
            "courseCode": "UC001",
            "courseDescription": "An updated description of the course.",
            "courseDuration": 75,
            "courseMode": "Blended",
            "maxEnrollments": 120,
            "courseFee": 129.99,
            "language": "Spanish",
            "courseCategory": "Web Development",
            "modules": [
                {
                    "id": 1,
                    "title": "Module 1: Getting Started",
                    "description": "Introduction to the course and basic concepts.",
                    "moduleOrder": 1,
                    "lessons": [
                        {
                            "type": "video",
                            "id": 101,
                            "title": "Lesson 1.1: Your First Application",
                            "lessonOrder": 1
                        },
                        {
                            "type": "article",
                            "id": 102,
                            "title": "Lesson 1.2: Reading Material",
                            "lessonOrder": 2,
                            "content": "<h1>Welcome</h1><p>This is the core reading for this section.</p>"
                        }
                    ]
                }
            ]
        }
        ```

*   **Get Course ID by Code**
    *   **Endpoint:** `/api/courses/{courseCode}/id`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body:** `1` (the course ID)

*   **Delete Course**
    *   **Endpoint:** `/api/courses/{courseId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

---

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

---

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

---

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

---

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

---

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

---

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

---

### 11. ProfileImageController Tests (`/api/public`)

*   **Upload Image**
    *   **Endpoint:** `/api/public/image-upload`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (form-data):**
        *   `file`: Select an image file.
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "imageName": "my_image.jpg",
            "contentType": "image/jpeg",
            "imageData": "base64encodedstring..."
        }
        ```

*   **Get Image by ID**
    *   **Endpoint:** `/api/public/get-image/{id}`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK` (with image data)

*   **Get All Images**
    *   **Endpoint:** `/api/public/images`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "imageName": "my_image.jpg",
                "contentType": "image/jpeg",
                "imageData": "base64encodedstring..."
            }
        ]
        ```

---

### 12. VideoController Tests (`/api/videos`)

*   **Upload Video**
    *   **Endpoint:** `/api/videos/upload`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):**
        *   `file`: Select a video file.
        *   `title`: "My New Video"
        *   `description`: "A description of the video."
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "My New Video",
            "description": "A description of the video.",
            "fileName": "my_video.mp4",
            "fileSize": 1024000,
            "contentType": "video/mp4"
        }
        ```

*   **Get All Videos**
    *   **Endpoint:** `/api/videos`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "title": "My New Video",
                "description": "A description of the video.",
                "fileName": "my_video.mp4",
                "fileSize": 1024000,
                "contentType": "video/mp4"
            }
        ]
        ```

*   **Get Video by ID**
    *   **Endpoint:** `/api/videos/{id}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "My New Video",
            "description": "A description of the video.",
            "fileName": "my_video.mp4",
            "fileSize": 1024000,
            "contentType": "video/mp4"
        }
        ```

*   **Delete Video**
    *   **Endpoint:** `/api/videos/{id}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

*   **Update Video**
    *   **Endpoint:** `/api/videos/{id}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):**
        *   `file`: (Optional) Select a new video file.
        *   `title`: "Updated Video Title"
        *   `description`: "Updated description of the video."
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "Updated Video Title",
            "description": "Updated description of the video.",
            "fileName": "updated_video.mp4",
            "fileSize": 1500000,
            "contentType": "video/mp4"
        }
        ```

*   **Search Video by Title**
    *   **Endpoint:** `/api/videos/search?title={title}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "title": "My New Video",
            "description": "A description of the video.",
            "fileName": "my_video.mp4",
            "fileSize": 1024000,
            "contentType": "video/mp4"
        }
        ```

*   **Stream Video**
    *   **Endpoint:** `/api/videos/{id}/stream`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK` (with video stream)

---

### 13. CertificateController Tests (`/api/certificates`)

*   **Create Certificate**
    *   **Endpoint:** `/api/certificates`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):**
        *   `userProfileId`: (ID of an existing user profile)
        *   `courseId`: (ID of an existing course)
        *   `instructorId`: (ID of an existing instructor)
        *   `dateOfCertificate`: "2025-07-10"
        *   `file`: (Optional) Select a certificate file (e.g., PDF).
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "certificateId": 1,
            "userProfileId": 1,
            "userProfileFirstName": "John",
            "courseId": 1,
            "courseName": "Sample Course",
            "instructorId": 1,
            "instructorFirstName": "Jane",
            "dateOfCertificate": "2025-07-10"
        }
        ```

*   **Get Certificate by ID**
    *   **Endpoint:** `/api/certificates/{id}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR` (any certificate), `USER` (their own certificate)
    *   **Expected Status (ADMIN/INSTRUCTOR/Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "certificateId": 1,
            "userProfileId": 1,
            "userProfileFirstName": "John",
            "courseId": 1,
            "courseName": "Sample Course",
            "instructorId": 1,
            "instructorFirstName": "Jane",
            "dateOfCertificate": "2025-07-10"
        }
        ```

*   **Get All Certificates**
    *   **Endpoint:** `/api/certificates`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "certificateId": 1,
                "userProfileId": 1,
                "userProfileFirstName": "John",
                "courseId": 1,
                "courseName": "Sample Course",
                "instructorId": 1,
                "instructorFirstName": "Jane",
                "dateOfCertificate": "2025-07-10"
            }
        ]
        ```

*   **Update Certificate**
    *   **Endpoint:** `/api/certificates/{id}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):**
        *   `certificate`: JSON part for `Certificate` object (e.g., `{"userProfile":{"id":1},"course":{"courseId":1},"instructor":{"instructorId":1},"dateOfCertificate":"2025-07-15"}`)
        *   `file`: (Optional) Select a new certificate file.
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "certificateId": 1,
            "userProfileId": 1,
            "userProfileFirstName": "John",
            "courseId": 1,
            "courseName": "Sample Course",
            "instructorId": 1,
            "instructorFirstName": "Jane",
            "dateOfCertificate": "2025-07-15"
        }
        ```

*   **Delete Certificate**
    *   **Endpoint:** `/api/certificates/{id}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

---

### 14. CheckoutController Tests (`/api/checkout`)

*   **Create Checkout Session**
    *   **Endpoint:** `/api/checkout/create-checkout-session`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Request Body (JSON):**
        ```json
        {
            "tier": "premium",
            "successUrl": "http://localhost:3000/success",
            "cancelUrl": "http://localhost:3000/cancel",
            "userId": 1,
            "courseId": 1,
            "instructorId": null
        }
        ```
    *   **Expected Status (USER):** `200 OK` (with session URL)
    *   **Sample Response Body (String):**
        ```
        "https://checkout.stripe.com/c/pay/cs_test_..."
        ```

---

### 15. InstructorController Tests (`/api/instructors`)

*   **Create Instructor**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`
    *   **Request Body (JSON):**
        ```json
        {
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11"
        }
        ```
    *   **Expected Status (ADMIN):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11"
        }
        ```

*   **Get All Instructors**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "instructorId": 1,
                "firstName": "New",
                "lastName": "Instructor",
                "email": "new.instructor@example.com",
                "phoneNo": "123-456-7890",
                "dateOfJoining": "2025-07-11"
            }
        ]
        ```

*   **Get Instructor by ID**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11"
        }
        ```

*   **Update Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`
    *   **Request Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "Updated",
            "lastName": "Instructor",
            "email": "updated.instructor@example.com",
            "phoneNo": "098-765-4321",
            "dateOfJoining": "2025-07-11"
        }
        ```
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "Updated",
            "lastName": "Instructor",
            "email": "updated.instructor@example.com",
            "phoneNo": "098-765-4321",
            "dateOfJoining": "2025-07-11"
        }
        ```

*   **Delete Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

---

### 16. InvoiceController Tests (`/api/invoices`)

*   **Create Invoice**
    *   **Endpoint:** `/api/invoices`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "invoiceNumber": "INV-2025-001",
            "date": "2025-07-14",
            "dueDate": "2025-08-14",
            "user": {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "phone": "111-222-3333",
                "address": "123 Main St, Anytown, USA"
            },
            "items": [
                {
                    "description": "Course Enrollment: Advanced Java",
                    "quantity": 1,
                    "unitPrice": 500.00,
                    "total": 500.00
                }
            ],
            "subtotal": 500.00,
            "taxRate": 0.08,
            "taxAmount": 40.00,
            "discount": 0.00,
            "total": 540.00,
            "status": "DRAFT",
            "notes": "Thank you for your business!"
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "invoice": {
                "id": 1,
                "invoiceNumber": "INV-2025-001",
                "date": "2025-07-14",
                "dueDate": "2025-08-14",
                "subtotal": 500.00,
                "taxRate": 0.08,
                "taxAmount": 40.00,
                "discount": 0.00,
                "total": 540.00,
                "status": "DRAFT",
                "notes": "Thank you for your business!",
                "user": {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com"
                },
                "items": [
                    {
                        "id": 1,
                        "description": "Course Enrollment: Advanced Java",
                        "quantity": 1,
                        "unitPrice": 500.00,
                        "total": 500.00
                    }
                ]
            },
            "stripeInvoiceUrl": "https://invoice.stripe.com/i/..."
        }
        ```

---

### 17. SubscriptionController Tests (`/api/subscriptions`)

*   **Get All Subscription Plans**
    *   **Endpoint:** `/api/subscriptions/plans`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "name": "Foundation",
                "planType": "LEARNER",
                "learnerTier": "FOUNDATION",
                "facultyTier": null,
                "priceInr": 0.00,
                "description": "Access public course catalog, register, attend one free course, forum participation",
                "features": [
                    "Access public course catalog",
                    "Register for courses",
                    "Attend one free course",
                    "Forum participation"
                ],
                "bestSuitedFor": "New users exploring the platform",
                "active": true,
                "minimumDurationMonths": 1,
                "customPricing": false
            }
        ]
        ```

*   **Get Learner Plans**
    *   **Endpoint:** `/api/subscriptions/plans/learner`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Get Faculty Plans**
    *   **Endpoint:** `/api/subscriptions/plans/faculty`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Get Plan by ID**
    *   **Endpoint:** `/api/subscriptions/plans/{planId}`
    *   **Method:** `GET`
    *   **Required Role:** None (public)
    *   **Expected Status:** `200 OK`

*   **Subscribe User to Plan**
    *   **Endpoint:** `/api/subscriptions/users/{userId}/subscribe`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "planId": 2,
            "autoRenew": true,
            "durationMonths": 3,
            "discountApplied": 0.00,
            "paymentReference": "stripe_payment_123"
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`
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
                "learnerTier": "ESSENTIAL",
                "priceInr": 849.00
            },
            "status": "ACTIVE",
            "startDate": "2025-01-15T10:00:00",
            "endDate": "2025-04-15T10:00:00",
            "autoRenew": true,
            "actualPrice": 849.00,
            "discountApplied": 0.00,
            "paymentReference": "stripe_payment_123",
            "createdAt": "2025-01-15T10:00:00",
            "updatedAt": "2025-01-15T10:00:00"
        }
        ```

*   **Get User Subscriptions**
    *   **Endpoint:** `/api/subscriptions/users/{userId}`
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
                "endDate": "2025-04-15T10:00:00",
                "autoRenew": true
            }
        ]
        ```

*   **Get Current User Subscription**
    *   **Endpoint:** `/api/subscriptions/users/{userId}/current`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK` or `204 No Content`

*   **Cancel Subscription**
    *   **Endpoint:** `/api/subscriptions/{subscriptionId}/cancel`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Subscription Owner
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Expire Subscriptions (Manual Trigger)**
    *   **Endpoint:** `/api/subscriptions/expire-subscriptions`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `200 OK`

---

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
            "customPricing": false
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
            "customPricing": false
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

---

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

---

### Testing Notes for Subscription System

1. **Plan IDs:** After the application starts, the subscription plans will be automatically loaded. Use the following approximate IDs:
   - Foundation (Learner): ID 1
   - Essential (Learner): ID 2
   - Professional (Learner): ID 3
   - Mastery (Learner): ID 4
   - Institutional (Learner): ID 5
   - Starter (Faculty): ID 6
   - Educator (Faculty): ID 7
   - Mentor (Faculty): ID 8
   - Institutional (Faculty): ID 9

2. **Subscription Status:** Valid values are `ACTIVE`, `INACTIVE`, `EXPIRED`, `CANCELLED`, `PENDING`

3. **Plan Types:** Valid values are `LEARNER`, `FACULTY`

4. **Testing Workflow:**
   - First, get available plans using `/api/subscriptions/plans/learner` or `/api/subscriptions/plans/faculty`
   - Subscribe a user using `/api/subscriptions/users/{userId}/subscribe`
   - Check subscription status using `/api/user/subscription/{userId}`
   - Test cancellation using `/api/subscriptions/{subscriptionId}/cancel`

5. **Automatic Expiration:** The system runs a scheduled task daily at 2 AM to expire subscriptions. You can manually trigger this using the admin endpoint.

---

Remember to replace placeholders like `{username}`, `{userID}`, `{courseId}`, `{id}`, `{userId}`, `{planId}`, `{subscriptionId}` with actual values from your database. Good luck with your testing!