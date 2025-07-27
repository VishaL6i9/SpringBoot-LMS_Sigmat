### SuperAdminController Tests (`/api/super-admin`)

**All endpoints in this controller require `SUPER_ADMIN` role.**

**Before you start:**
- Ensure you have a user with `SUPER_ADMIN` role in the database
- Obtain a JWT token by logging in as a SuperAdmin user
- Use `Authorization: Bearer {token}` header for all requests

---

#### User Management Endpoints

*   **Get All Users**
    *   **Endpoint:** `/api/super-admin/users`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 1,
                "username": "testuser",
                "email": "test@example.com",
                "firstName": "Test",
                "lastName": "User",
                "roles": ["USER"]
            },
            {
                "id": 2,
                "username": "admin",
                "email": "admin@example.com",
                "firstName": "Admin",
                "lastName": "User",
                "roles": ["ADMIN"]
            }
        ]
        ```

*   **Get All Admin Users**
    *   **Endpoint:** `/api/super-admin/users/admins`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 2,
                "username": "admin",
                "email": "admin@example.com",
                "firstName": "Admin",
                "lastName": "User",
                "roles": ["ADMIN"]
            }
        ]
        ```

*   **Get All SuperAdmin Users**
    *   **Endpoint:** `/api/super-admin/users/super-admins`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 3,
                "username": "superadmin",
                "email": "superadmin@example.com",
                "firstName": "Super",
                "lastName": "Admin",
                "roles": ["SUPER_ADMIN"]
            }
        ]
        ```

---

#### User Role Management Endpoints

*   **Promote User to Admin**
    *   **Endpoint:** `/api/super-admin/users/{userId}/promote-to-admin`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Path Parameters:** `userId` - ID of the user to promote
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body:** `"User promoted to ADMIN successfully!"`

*   **Promote User to SuperAdmin**
    *   **Endpoint:** `/api/super-admin/users/{userId}/promote-to-super-admin`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Path Parameters:** `userId` - ID of the user to promote
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body:** `"User promoted to SUPER_ADMIN successfully!"`

*   **Demote User to Regular User**
    *   **Endpoint:** `/api/super-admin/users/{userId}/demote-to-user`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Path Parameters:** `userId` - ID of the user to demote
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body:** `"User demoted to USER successfully!"`

*   **Force Delete User**
    *   **Endpoint:** `/api/super-admin/users/{userId}/force-delete`
    *   **Method:** `DELETE`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Path Parameters:** `userId` - ID of the user to delete
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body:** `"User force deleted successfully!"`
    *   **Note:** SuperAdmins cannot delete other SuperAdmins

---

#### System Administration Endpoints

*   **Get System Statistics**
    *   **Endpoint:** `/api/super-admin/system/stats`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "totalUsers": 150,
            "adminCount": 3,
            "superAdminCount": 1,
            "instructorCount": 25,
            "regularUserCount": 121,
            "timestamp": "2025-01-27T10:30:00"
        }
        ```

*   **Toggle Maintenance Mode**
    *   **Endpoint:** `/api/super-admin/system/maintenance-mode?enabled={true|false}`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Query Parameters:** `enabled` - boolean value (true/false)
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body:** `"Maintenance mode enabled successfully!"` or `"Maintenance mode disabled successfully!"`

---

#### Audit Endpoints

*   **Get User Activities**
    *   **Endpoint:** `/api/super-admin/audit/user-activities`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`
    *   **Query Parameters (Optional):**
        *   `userId` - Filter by specific user ID
        *   `dateFrom` - Start date for filtering (YYYY-MM-DD)
        *   `dateTo` - End date for filtering (YYYY-MM-DD)
    *   **Expected Status (SUPER_ADMIN):** `200 OK`
    *   **Sample Response Body:** `"User activities retrieved successfully!"`
    *   **Note:** This is a placeholder endpoint for future audit functionality

---

#### Error Responses

*   **Unauthorized Access (Non-SuperAdmin)**
    *   **Expected Status:** `403 Forbidden`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "timestamp": "2025-01-27T10:30:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Access Denied",
            "path": "/api/super-admin/users"
        }
        ```

*   **User Not Found**
    *   **Expected Status:** `400 Bad Request`
    *   **Sample Response Body:** `"Error promoting user: User not found with ID: 999"`

*   **Invalid Role**
    *   **Expected Status:** `400 Bad Request`
    *   **Sample Response Body:** `"Error promoting user: Invalid role specified: INVALID_ROLE"`

---

#### Testing Workflow

1. **Setup SuperAdmin User:**
   ```sql
   -- Manually update database to create first SuperAdmin
   UPDATE user_roles SET roles = 'SUPER_ADMIN' WHERE user_id = {existing_admin_id};
   ```

2. **Login as SuperAdmin:**
   ```
   POST /api/public/login
   Body: {"username": "superadmin", "password": "password"}
   ```

3. **Test User Management:**
   - Get all users to see current state
   - Promote a regular user to admin
   - Check admin list to verify promotion
   - Test system statistics

4. **Test Access Control:**
   - Try accessing endpoints with regular admin token (should fail)
   - Try accessing endpoints with user token (should fail)
   - Verify SuperAdmin can access all admin endpoints

---

#### Security Notes

- SuperAdmins have access to all existing admin endpoints
- SuperAdmins cannot delete other SuperAdmins (safety measure)
- Only SuperAdmins can promote users to SuperAdmin role
- All SuperAdmin actions should be logged for audit purposes
- Limit the number of SuperAdmin accounts (recommended: 2-3 maximum)

---

#### Integration with Existing Endpoints

SuperAdmins now have access to all existing endpoints that previously required `ADMIN` role:
- `/api/admin/*` - All admin endpoints
- `/api/user/profile/*` - All user profiles (not just their own)
- `/api/instructor/profile/*` - All instructor profiles
- All other endpoints with admin restrictions