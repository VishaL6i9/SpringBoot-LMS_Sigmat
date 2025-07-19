Here are sample test cases for Postman to verify the role-based access control.

**Before you start:**

*   **Ensure you have users with `ADMIN`, `INSTRUCTOR`, and `USER` roles in your database.** If not, you'll need to create them first.
*   **Authentication:** All protected endpoints require a JWT token in the `Authorization` header (e.g., `Bearer <your_jwt_token>`). You'll need to obtain this token by authenticating a user.

---

### 1. Authentication (Public Endpoint)

*   **Endpoint:** `/api/public/authenticate`
*   **Method:** `POST`
*   **Required Role:** None (public)
*   **Request Body (JSON):**
    ```json
    {
        "username": "your_username",
        "password": "your_password"
    }
    ```
*   **Expected Response:** A JWT token in the response body. Use this token for subsequent requests.

*   **Get User Role**
    *   **Endpoint:** `/api/public/role`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

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

*   **Change User Role**
    *   **Endpoint:** `/api/admin/user/{userId}/role?newRole={newRole}`
    *   **Method:** `PUT`
    *   **Expected Status (ADMIN):** `200 OK`

---

### 3. UserProfileController Tests (`/api/user`)

*   **Get User Profile by ID**
    *   **Endpoint:** `/api/user/profile/{userID}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Update User Profile**
    *   **Endpoint:** `/api/user/profile`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Request Body (JSON):**
        ```json
        {
            "id": 1, 
            "firstName": "UpdatedFirstName",
            "lastName": "UpdatedLastName",
            "email": "updated.email@example.com",
            "phone": "123-456-7890",
            "timezone": "UTC",
            "language": "en",
            "profileImage": null,
            "users": {
                "id": 1 
            }
        }
        ```
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Update User Password**
    *   **Endpoint:** `/api/user/profile/password?userID={userID}&newPassword={newPassword}`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`

*   **Enroll User in Course**
    *   **Endpoint:** `/api/user/enroll?userId={userId}&courseId={courseId}`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `201 Created`

*   **Get User Enrollments**
    *   **Endpoint:** `/api/user/enrollments/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Admin or Owner):** `200 OK`

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

*   **Get All Courses**
    *   **Endpoint:** `/api/courses`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`

*   **Get Course by ID (Hierarchical)**
    *   **Endpoint:** `/api/courses/{courseId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Note:** The response is now hierarchical, including `modules` and `lessons`.

*   **Delete Course**
    *   **Endpoint:** `/api/courses/{courseId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

*   **Create a Course Module**
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
    *   **Note:** Save the `id` from the response to use in other tests (e.g., `moduleId=1`).

*   **Get All Modules for a Course**
    *   **Endpoint:** `/api/courses/{courseId}/modules`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status:** `200 OK`

---

### 5. CourseModuleController Tests (`/api/modules`)

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

*   **Delete Module**
    *   **Endpoint:** `/api/modules/{moduleId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status:** `204 No Content`

---

### 6. LessonController Tests (`/api/lessons`)

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

    *   **Request Body Example (Article Lesson):**
        ```json
        {
          "type": "article",
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
    *   **Note:** Save the `id` from the assignment response for submission tests (e.g., `assignmentId=1`).

    *   **Request Body Example (Quiz Lesson):**
        ```json
        {
            "type": "quiz",
            "title": "Quiz 1: Check Your Knowledge",
            "lessonOrder": 4
        }
        ```
    *   **Note:** Save the `id` from the quiz response for adding questions (e.g., `quizId=1`).

---

### 7. AssignmentController Tests (`/api/assignments`)

*   **Submit an Assignment**
    *   **Endpoint:** `/api/assignments/{assignmentId}/submit`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Query Parameters:** `userId`
    *   **Request Body (form-data):** `content` (text), `filePath` (optional file path)
    *   **Expected Status:** `200 OK`
    *   **Note:** Save the `id` from the response for grading (e.g., `submissionId=1`).

*   **Get Submissions for an Assignment (Instructor View)**
    *   **Endpoint:** `/api/assignments/{assignmentId}/submissions`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status:** `200 OK`

*   **Grade a Submission (Instructor View)**
    *   **Endpoint:** `/api/submissions/{submissionId}/grade`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Query Parameters:** `grade`, `feedback`
    *   **Expected Status:** `200 OK`

---

### 8. QuizController Tests (`/api/quizzes`)

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

*   **Get a Quiz**
    *   **Endpoint:** `/api/quizzes/{quizId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status:** `200 OK`
    *   **Expected Response:** The quiz object, populated with its `questions` and their `answerChoices`.

---

### 9. NotificationController Tests (`/api/notifications`)

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

*   **Get User Notifications**
    *   **Endpoint:** `/api/notifications/user/{userId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`

*   **Mark Notification as Read**
    *   **Endpoint:** `/api/notifications/{id}/read`
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` or Owner
    *   **Expected Status (Owner or Admin):** `200 OK`

---

### 10. VideoController Tests (`/api/videos`)

*   **Upload Video**
    *   **Endpoint:** `/api/videos/upload`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):** `file`, `title`, `description`
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`

*   **Get All Videos**
    *   **Endpoint:** `/api/videos`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`

*   **Delete Video**
    *   **Endpoint:** `/api/videos/{id}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

---

### 11. CertificateController Tests (`/api/certificates`)

*   **Create Certificate**
    *   **Endpoint:** `/api/certificates`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):** `learnerId`, `courseId`, `instructorId`, `dateOfCertificate`, `file` (optional)
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`

*   **Get Certificate by ID**
    *   **Endpoint:** `/api/certificates/{id}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, or Owner
    *   **Expected Status (Owner or Admin/Instructor):** `200 OK`

*   **Get All Certificates**
    *   **Endpoint:** `/api/certificates`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`

*   **Delete Certificate**
    *   **Endpoint:** `/api/certificates/{id}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

---

### 12. CheckoutController Tests (`/api/checkout`)

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
    *   **Expected Status (USER):** `200 OK`

---

### 13. InstructorController Tests (`/api/instructors`)

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

*   **Get All Instructors**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`

*   **Get Instructor by ID**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`

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

*   **Delete Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`

---

### 14. InvoiceController Tests (`/api/invoices`)

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

---

Remember to replace placeholders like `{username}`, `{userID}`, `{courseId}`, `{id}` with actual values from your database. Good luck with your testing!
