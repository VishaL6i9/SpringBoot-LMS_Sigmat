### 15. InstructorController Tests (`/api/instructors`)

**Note:** This controller now supports `SUPER_ADMIN` role in addition to `ADMIN` for all administrative operations.

*   **Create Instructor**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Request Body (JSON):**
        ```json
        {
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11",
            "facebookHandle": "newinstructor",
            "linkedinHandle": "new-instructor-123",
            "youtubeHandle": "newinstructortech"
        }
        ```
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11",
            "facebookHandle": "newinstructor",
            "linkedinHandle": "new-instructor-123",
            "youtubeHandle": "newinstructortech"
        }
        ```

*   **Get All Instructors**
    *   **Endpoint:** `/api/instructors`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, `INSTRUCTOR`, `USER`
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
                "dateOfJoining": "2025-07-11",
                "facebookHandle": "newinstructor",
                "linkedinHandle": "new-instructor-123",
                "youtubeHandle": "newinstructortech"
            }
        ]
        ```

*   **Get Instructor by ID**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, `INSTRUCTOR`, `USER`
    *   **Expected Status (All Roles):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "New",
            "lastName": "Instructor",
            "email": "new.instructor@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-07-11",
            "facebookHandle": "newinstructor",
            "linkedinHandle": "new-instructor-123",
            "youtubeHandle": "newinstructortech"
        }
        ```

*   **Update Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `PUT`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Request Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "Updated",
            "lastName": "Instructor",
            "email": "updated.instructor@example.com",
            "phoneNo": "098-765-4321",
            "dateOfJoining": "2025-07-11",
            "facebookHandle": "updatedinstructor",
            "linkedinHandle": "updated-instructor-456",
            "youtubeHandle": "updatedinstructortech"
        }
        ```
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "instructorId": 1,
            "firstName": "Updated",
            "lastName": "Instructor",
            "email": "updated.instructor@example.com",
            "phoneNo": "098-765-4321",
            "dateOfJoining": "2025-07-11",
            "facebookHandle": "updatedinstructor",
            "linkedinHandle": "updated-instructor-456",
            "youtubeHandle": "updatedinstructortech"
        }
        ```

*   **Delete Instructor**
    *   **Endpoint:** `/api/instructors/{instructorId}`
    *   **Method:** `DELETE`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `204 No Content`

---

#### New Features in v2.2.0

**Social Media Integration:**
- Added support for Facebook, LinkedIn, and YouTube handles
- Social media fields are optional and can be null
- Handles are synchronized between Instructor and InstructorProfile entities

**Enhanced Authorization:**
- All endpoints now support `SUPER_ADMIN` role
- SuperAdmins have the same access as Admins for instructor management
- Maintains backward compatibility with existing Admin access

**Related Endpoints:**
- Use `/api/instructor/profile/*` endpoints for comprehensive instructor profile management
- Banking information and detailed profiles are managed through InstructorProfile system
- Profile images and extended information available through dedicated profile endpoints
