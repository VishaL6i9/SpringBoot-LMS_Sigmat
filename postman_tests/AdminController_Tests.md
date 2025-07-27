### 2. AdminController Tests (`/api/admin`)

**All endpoints in this controller require `SUPER_ADMIN` or `ADMIN` role.**

*   **Get All Users**
    *   **Endpoint:** `/api/admin/users`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `200 OK`

*   **Delete User by Username**
    *   **Endpoint:** `/api/admin/delete/user/{username}`
    *   **Method:** `DELETE`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `200 OK`

*   **Delete Users in Batch**
    *   **Endpoint:** `/api/admin/delete/users/batch`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Request Body (form-data):**
        *   `file`: Select a CSV or Excel file containing usernames to delete.
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `200 OK`

*   **Change User Role**
    *   **Endpoint:** `/api/admin/user/{userId}/role?newRole={newRole}`
    *   **Method:** `PUT`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`
    *   **Query Parameters:** `newRole` - Can be `USER`, `ADMIN`, `INSTRUCTOR`, or `SUPER_ADMIN` (SuperAdmin promotion requires SUPER_ADMIN role)
    *   **Expected Status (SUPER_ADMIN/ADMIN):** `200 OK`
    *   **Note:** Only SuperAdmins can promote users to SUPER_ADMIN role

---

#### Enhanced Authorization in v2.2.0

**SuperAdmin Access:**
- All admin endpoints now support `SUPER_ADMIN` role
- SuperAdmins have the same access as Admins plus additional privileges
- SuperAdmins can promote users to SUPER_ADMIN role (Admins cannot)

**Role Management:**
- Admins can assign: `USER`, `ADMIN`, `INSTRUCTOR` roles
- SuperAdmins can assign: `USER`, `ADMIN`, `INSTRUCTOR`, `SUPER_ADMIN` roles
- Role validation prevents invalid role assignments

**Security Enhancements:**
- SuperAdmin-specific operations are logged
- Role hierarchy is enforced (SuperAdmin > Admin > Instructor > User)
- Additional validation for sensitive operations
