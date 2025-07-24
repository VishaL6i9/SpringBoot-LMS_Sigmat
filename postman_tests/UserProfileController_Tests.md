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
            "address": "123 Main St",
            "language": "en",
            "timezone": "UTC",
            "profileImage": {
                "id": 1,
                "imageName": "profile.jpg",
                "contentType": "image/jpeg",
                "imageData": "..." // Base64 encoded image data
            },
            "user": {
                "id": 1,
                "username": "testuser",
                "email": "test@example.com",
                "firstName": "Test",
                "lastName": "User"
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
            "email": "user@example.example.com",
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
