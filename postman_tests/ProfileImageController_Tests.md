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
