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
