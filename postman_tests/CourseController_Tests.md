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
