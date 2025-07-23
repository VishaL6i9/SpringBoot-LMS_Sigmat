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

*   **Delete Users in Batch**
    *   **Endpoint:** `/api/admin/delete/users/batch`
    *   **Method:** `POST`
    *   **Request Body (form-data):**
        *   `file`: Select a CSV or Excel file containing usernames to delete.
    *   **Expected Status (ADMIN):** `200 OK`

*   **Change User Role**
    *   **Endpoint:** `/api/admin/user/{userId}/role?newRole={newRole}`
    *   **Method:** `PUT`
    *   **Expected Status (ADMIN):** `200 OK`
