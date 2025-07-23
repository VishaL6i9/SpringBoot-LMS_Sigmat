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
