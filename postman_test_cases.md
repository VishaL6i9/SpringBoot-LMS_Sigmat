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
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

*   **Delete User by Username**
    *   **Endpoint:** `/api/admin/delete/user/{username}` (replace `{username}` with an actual username)
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

*   **Change User Role**
    *   **Endpoint:** `/api/admin/user/{userId}/role?newRole={newRole}` (replace `{userId}` with an actual user ID and `{newRole}` with ADMIN, INSTRUCTOR, or USER)
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

---

### 3. UserProfileController Tests (`/api/user`)

**All endpoints in this controller require a valid JWT token in the `Authorization` header.** The server will programmatically check if the user is an `ADMIN` or is accessing their own resource.

*   **Get User Profile by ID**
    *   **Endpoint:** `/api/user/profile/{userID}` (replace `{userID}` with an actual user ID)
    *   **Method:** `GET`
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Expected Status (Non-Owner):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Update User Profile**
    *   **Endpoint:** `/api/user/profile`
    *   **Method:** `PUT`
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Request Body (JSON):** A `UserProfile` object. The `users.id` in the body must match the ID of the authenticated user (from the JWT), or the user must be an `ADMIN`.
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
    *   **Expected Status (Non-Owner):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Update User Password**
    *   **Endpoint:** `/api/user/profile/password?userID={userID}&newPassword={newPassword}`
    *   **Method:** `PUT`
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Expected Status (Non-Owner):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Enroll User in Course**
    *   **Endpoint:** `/api/user/enroll?userId={userId}&courseId={courseId}`
    *   **Method:** `POST`
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status (Admin or Owner):** `201 Created`
    *   **Expected Status (Non-Owner):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Get User Enrollments**
    *   **Endpoint:** `/api/user/enrollments/{userId}`
    *   **Method:** `GET`
    *   **Headers:** `Authorization: Bearer <your_jwt_token>`
    *   **Expected Status (Admin or Owner):** `200 OK`
    *   **Expected Status (Non-Owner):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

---

### 4. CourseController Tests (`/api/courses`)

*   **Create Course**
    *   **Endpoint:** `/api/courses`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):** A `Course` object. All fields are included. To associate instructors, include an array of `Instructor` objects with at least their `instructorId`.
        ```json
        {
            "courseId": null, // Auto-generated on creation
            "courseName": "New Course Title",
            "courseCode": "NC001",
            "courseDescription": "A detailed description of the new course.",
            "courseDuration": 60, // Duration in minutes/hours/etc. (adjust as per your model's unit)
            "courseMode": "Online",
            "maxEnrollments": 100,
            "courseFee": 99.99,
            "language": "English",
            "courseCategory": "Software Development",
            "instructors": [
                {
                    "instructorId": 1 // Existing instructor ID
                },
                {
                    "instructorId": 2 // Another existing instructor ID
                }
            ]
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden`

*   **Update Course**
    *   **Endpoint:** `/api/courses/{courseId}` (replace `{courseId}` with an existing course ID)
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):** A `Course` object with updated details. All fields are included. To update instructors, include the full array of `Instructor` objects with their `instructorId`s.
        ```json
        {
            "courseId": 1, // Required for update, must match path variable
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
                {
                    "instructorId": 1 // Existing instructor ID
                },
                {
                    "instructorId": 3 // Another existing instructor ID (e.g., changed from 2 to 3)
                }
            ]
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden`

*   **Get All Courses**
    *   **Endpoint:** `/api/courses`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Get Course by ID**
    *   **Endpoint:** `/api/courses/{courseId}` (replace `{courseId}` with an existing course ID)
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Delete Course**
    *   **Endpoint:** `/api/courses/{courseId}` (replace `{courseId}` with an existing course ID)
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

---

### 5. NotificationController Tests (`/api/notifications`)

*   **Create Notification**
    *   **Endpoint:** `/api/notifications?userId={userId}` (replace `{userId}` with a target user ID)
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):** A `NotificationDTO` object. All fields are included.
        ```json
        {
            "id": null, // Auto-generated on creation
            "title": "New Notification",
            "message": "This is a test notification.",
            "type": "SYSTEM", // Enum: SYSTEM, COURSE, ANNOUNCEMENT, etc.
            "category": "ANNOUNCEMENT", // Enum: ANNOUNCEMENT, ALERT, REMINDER, etc.
            "priority": "HIGH", // Enum: LOW, MEDIUM, HIGH
            "read": false, // Default to false for new notifications
            "createdAt": null, // Auto-generated by backend
            "user": null // User object, typically set by backend based on userId parameter
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden`

*   **Get User Notifications**
    *   **Endpoint:** `/api/notifications/user/{userId}` (replace `{userId}` with an actual user ID)
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` (any user's notifications), `USER`/`INSTRUCTOR` (their own notifications)
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Expected Status (USER/INSTRUCTOR - own ID):** `200 OK`
    *   **Expected Status (USER/INSTRUCTOR - other ID):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Mark Notification as Read**
    *   **Endpoint:** `/api/notifications/{id}/read` (replace `{id}` with an actual notification ID)
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN` (any notification), `USER`/`INSTRUCTOR` (their own notification)
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Expected Status (USER/INSTRUCTOR - own notification):** `200 OK`
    *   **Expected Status (USER/INSTRUCTOR - other notification):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

---

### 6. VideoController Tests (`/api/videos`)

*   **Upload Video**
    *   **Endpoint:** `/api/videos/upload`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):**
        *   `file`: Select a video file.
        *   `title`: "My New Video"
        *   `description`: "A description of the video."
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden`

*   **Get All Videos**
    *   **Endpoint:** `/api/videos`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Delete Video**
    *   **Endpoint:** `/api/videos/{id}` (replace `{id}` with an existing video ID)
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

---

### 7. CertificateController Tests (`/api/certificates`)

*   **Create Certificate**
    *   **Endpoint:** `/api/certificates`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (form-data):**
        *   `learnerId`: (ID of an existing learner)
        *   `courseId`: (ID of an existing course)
        *   `instructorId`: (ID of an existing instructor)
        *   `dateOfCertificate`: "2025-07-10"
        *   `file`: (Optional) Select a certificate file (e.g., PDF).
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden`

*   **Get Certificate by ID**
    *   **Endpoint:** `/api/certificates/{id}` (replace `{id}` with an existing certificate ID)
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR` (any certificate), `USER` (their own certificate)
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Expected Status (USER - own certificate):** `200 OK`
    *   **Expected Status (USER - other certificate):** `403 Forbidden`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Get All Certificates**
    *   **Endpoint:** `/api/certificates`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden`

*   **Delete Certificate**
    *   **Endpoint:** `/api/certificates/{id}` (replace `{id}` with an existing certificate ID)
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

---

### 8. CheckoutController Tests (`/api/checkout`)

*   **Create Checkout Session**
    *   **Endpoint:** `/api/checkout/create-checkout-session`
    *   **Method:** `POST`
    *   **Required Role:** `USER`
    *   **Request Body (JSON):** All fields are included.
        ```json
        {
            "tier": "premium",
            "successUrl": "http://localhost:3000/success",
            "cancelUrl": "http://localhost:3000/cancel",
            "userId": 1, // Required: Replace with actual user ID
            "courseId": 1, // Required: Replace with actual course ID
            "instructorId": null // Optional: Replace with actual instructor ID if specific instructor is desired
        }
        ```
    *   **Expected Status (USER):** `200 OK` (with session URL)
    *   **Expected Status (ADMIN/INSTRUCTOR/Unauthorized):** `403 Forbidden`

---

### 9. InstructorController Tests (`/api/instructors`)

*   **Create Instructor**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`
    *   **Request Body (JSON):** An `Instructor` object. All fields are included.
        ```json
        {
            "instructorId": null, // Auto-generated on creation
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11" // Format: YYYY-MM-DD
        }
        ```
    *   **Expected Status (ADMIN):** `201 Created`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

*   **Get All Instructors**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Get Instructor by ID**
    *   **Endpoint:** `/api/instructors/{instructorId}` (replace `{instructorId}` with an existing instructor ID)
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Expected Status (Unauthorized):** `403 Forbidden`

*   **Update Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}` (replace `{instructorId}` with an existing instructor ID)
    *   **Method:** `PUT`
    *   **Required Role:** `ADMIN`
    *   **Request Body (JSON):** An `Instructor` object with updated details. All fields are included.
        ```json
        {
            "instructorId": 1, // Required for update, must match path variable
            "firstName": "Updated",
            "lastName": "Instructor",
            "email": "updated.instructor@example.com",
            "phoneNo": "098-765-4321",
            "dateOfJoining": "2025-07-11" // Format: YYYY-MM-DD
        }
        ```
    *   **Expected Status (ADMIN):** `200 OK`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

*   **Delete Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}` (replace `{instructorId}` with an existing instructor ID)
    *   **Method:** `DELETE`
    *   **Required Role:** `ADMIN`
    *   **Expected Status (ADMIN):** `204 No Content`
    *   **Expected Status (INSTRUCTOR/USER/Unauthorized):** `403 Forbidden`

---

### 10. InvoiceController Tests (`/api/invoices`)

*   **Create Invoice**
    *   **Endpoint:** `/api/invoices`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR` (or potentially `USER` if they can generate invoices for themselves)
    *   **Request Body (JSON):** A `InvoiceRequest` object.
        ```json
        {
            "invoiceNumber": "INV-2025-001",
            "date": "2025-07-14",
            "dueDate": "2025-08-14",
            "user": {
                "id": null, // Optional: If existing user, provide ID. Otherwise, leave null for new user.
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
                },
                {
                    "description": "E-book: Spring Boot Microservices",
                    "quantity": 1,
                    "unitPrice": 50.00,
                    "total": 50.00
                }
            ],
            "subtotal": 550.00,
            "taxRate": 0.085,
            "taxAmount": 46.75,
            "discount": 0.00,
            "total": 596.75,
            "status": "DRAFT",
            "notes": "Thank you for your business!"
        }
        ```
    *   **Expected Response Body (JSON):** A JSON object containing the saved `invoice` and the `stripeInvoiceUrl`.
        ```json
        {
            "invoice": {
                "id": 1,
                "invoiceNumber": "INV-2025-001",
                // ... other invoice fields
            },
            "stripeInvoiceUrl": "https://invoice.stripe.com/i/..."
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Expected Status (USER/Unauthorized):** `403 Forbidden` (assuming only ADMIN/INSTRUCTOR can create invoices)

---

Remember to replace placeholders like `{username}`, `{userID}`, `{courseId}`, `{id}` with actual values from your database. Good luck with your testing!