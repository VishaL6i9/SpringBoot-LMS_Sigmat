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

*   **Register Instructor**
    *   **Endpoint:** `/api/public/register/instructor`
    *   **Method:** `POST`
    *   **Required Role:** None (public)
    *   **Request Body (JSON):**
        ```json
        {
            "username": "newinstructor",
            "password": "password123",
            "email": "instructor@example.com",
            "firstName": "New",
            "lastName": "Instructor",
            "phoneNo": "123-456-7890",
            "bankName": "Example Bank",
            "accountNumber": "1234567890",
            "routingNumber": "123456789",
            "accountHolderName": "New Instructor"
        }
        ```
    *   **Expected Status:** `200 OK`
    *   **Expected Response:** `"Instructor registered successfully!"`
    *   **Note:** Social media handles are NOT required for registration and will be null initially. They can be added later through the instructor profile system.

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
