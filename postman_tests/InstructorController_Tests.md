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
