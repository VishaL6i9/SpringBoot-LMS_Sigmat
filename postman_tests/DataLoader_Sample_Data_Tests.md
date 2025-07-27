### DataLoader Sample Data Tests

**This document describes the sample data created by the DataLoader configuration and how to test it.**

**Before you start:**
- Ensure the application has been started at least once to trigger DataLoader
- Sample data is only created if it doesn't already exist in the database
- Use these credentials to test different role functionalities

---

#### Sample Users Created

*   **SuperAdmin User**
    *   **Username:** `superadmin`
    *   **Password:** `superadminpass`
    *   **Email:** `superadmin@example.com`
    *   **Role:** `SUPER_ADMIN`
    *   **Status:** Verified (ready to use)

*   **Admin User**
    *   **Username:** `admin`
    *   **Password:** `adminpass`
    *   **Email:** `admin@example.com`
    *   **Role:** `ADMIN`
    *   **Status:** Verified (ready to use)

*   **Instructor User**
    *   **Username:** `instructor`
    *   **Password:** `instructorpass`
    *   **Email:** `instructor@example.com`
    *   **Role:** `INSTRUCTOR`
    *   **Status:** Verified (ready to use)

*   **Regular User**
    *   **Username:** `user`
    *   **Password:** `userpass`
    *   **Email:** `user@example.com`
    *   **Role:** `USER`
    *   **Status:** Verified (ready to use)

---

#### Sample Instructor Data

*   **Instructor Entity**
    *   **Name:** Instructor Demo
    *   **Email:** instructor@example.com
    *   **Phone:** 555-0123
    *   **Date of Joining:** Current date
    *   **Social Media:**
        *   Facebook: `instructordemo`
        *   LinkedIn: `instructor-demo`
        *   YouTube: `instructordemotech`

*   **Instructor Profile**
    *   **Bio:** "Experienced instructor with expertise in programming and software development."
    *   **Specialization:** "Software Development"
    *   **Address:** "123 Education Street, Tech City, TC 12345"
    *   **Timezone:** UTC
    *   **Language:** English
    *   **Banking Info:**
        *   Bank Name: Demo Bank
        *   Account Number: 1234567890
        *   Routing Number: 123456789
        *   Account Holder: Instructor Demo

---

#### Sample Course Data

*   **Course: Introduction to Java Programming**
    *   **Course Code:** JAVA101
    *   **Description:** "A comprehensive introduction to Java programming language and object-oriented concepts."
    *   **Duration:** 8 weeks
    *   **Mode:** Online
    *   **Max Enrollments:** 50
    *   **Fee:** $99
    *   **Language:** English
    *   **Category:** Programming
    *   **Instructor:** Instructor Demo

*   **Course Module: Getting Started with Java**
    *   **Description:** "Learn the basics of Java programming language and environment setup."
    *   **Order:** 1

*   **Lessons in Module:**
    1. **Article Lesson:** "Introduction to Java" (Order: 1)
    2. **Video Lesson:** "Setting Up Java Development Environment" (Order: 2)
    3. **Quiz:** "Java Basics Quiz" (Order: 3)
    4. **Assignment:** "Java Hello World Program" (Order: 4)

---

#### Testing Authentication with Sample Data

*   **Login as SuperAdmin**
    ```json
    POST /api/public/login
    Content-Type: application/json
    
    {
        "username": "superadmin",
        "password": "superadminpass"
    }
    ```
    *   **Expected Response:** JWT token with SUPER_ADMIN role
    *   **Use for:** Testing all SuperAdmin endpoints

*   **Login as Admin**
    ```json
    POST /api/public/login
    Content-Type: application/json
    
    {
        "username": "admin",
        "password": "adminpass"
    }
    ```
    *   **Expected Response:** JWT token with ADMIN role
    *   **Use for:** Testing admin endpoints (but not SuperAdmin exclusive)

*   **Login as Instructor**
    ```json
    POST /api/public/login
    Content-Type: application/json
    
    {
        "username": "instructor",
        "password": "instructorpass"
    }
    ```
    *   **Expected Response:** JWT token with INSTRUCTOR role
    *   **Use for:** Testing instructor-specific functionality

*   **Login as Regular User**
    ```json
    POST /api/public/login
    Content-Type: application/json
    
    {
        "username": "user",
        "password": "userpass"
    }
    ```
    *   **Expected Response:** JWT token with USER role
    *   **Use for:** Testing user-level functionality

---

#### Testing Sample Data Endpoints

*   **Verify SuperAdmin System Stats**
    ```
    GET /api/super-admin/system/stats
    Authorization: Bearer {superadmin_token}
    ```
    *   **Expected Response:**
        ```json
        {
            "totalUsers": 4,
            "adminCount": 1,
            "superAdminCount": 1,
            "instructorCount": 1,
            "regularUserCount": 1,
            "timestamp": "2025-01-27T..."
        }
        ```

*   **Verify Instructor Profile**
    ```
    GET /api/instructor/profile/{instructorId}
    Authorization: Bearer {instructor_token}
    ```
    *   **Expected Response:** Complete instructor profile with bio, specialization, and social media handles

*   **Verify Course Data**
    ```
    GET /api/courses
    Authorization: Bearer {any_token}
    ```
    *   **Expected Response:** Array containing "Introduction to Java Programming" course

*   **Verify Course Modules**
    ```
    GET /api/courses/{courseId}/modules
    Authorization: Bearer {any_token}
    ```
    *   **Expected Response:** Array containing "Getting Started with Java" module

---

#### Testing Role Hierarchy

*   **SuperAdmin Access Test**
    ```
    GET /api/admin/users
    Authorization: Bearer {superadmin_token}
    ```
    *   **Expected:** Success (200 OK) - SuperAdmin can access admin endpoints

*   **Admin Access Test**
    ```
    GET /api/super-admin/users
    Authorization: Bearer {admin_token}
    ```
    *   **Expected:** Forbidden (403) - Admin cannot access SuperAdmin endpoints

*   **User Access Test**
    ```
    GET /api/admin/users
    Authorization: Bearer {user_token}
    ```
    *   **Expected:** Forbidden (403) - User cannot access admin endpoints

---

#### Testing Instructor Profile System

*   **Get Instructor ID from JWT**
    ```
    GET /api/instructor/profile/getInstructorId
    Authorization: Bearer {instructor_token}
    ```
    *   **Expected:** Instructor ID number

*   **Update Instructor Profile**
    ```
    PUT /api/instructor/profile/{instructorId}
    Authorization: Bearer {instructor_token}
    Content-Type: application/json
    
    {
        "bio": "Updated bio with new information",
        "specialization": "Advanced Java Development",
        "facebookHandle": "updatedinstructor"
    }
    ```
    *   **Expected:** Success (200 OK) with updated profile

---

#### Troubleshooting Sample Data

*   **If sample data is not created:**
    1. Check application logs for DataLoader execution
    2. Verify database connection is working
    3. Ensure no existing data conflicts with sample data

*   **If authentication fails:**
    1. Verify user was created successfully in logs
    2. Check password encoding is working correctly
    3. Ensure JWT configuration is properly set up

*   **If instructor profile creation fails:**
    1. Check that InstructorProfileService is properly injected
    2. Verify instructor entity was created first
    3. Check for any database constraint violations

---

#### Sample Data Cleanup

To reset sample data for testing:

```sql
-- Remove sample users (be careful in production!)
DELETE FROM user_roles WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('superadmin', 'admin', 'instructor', 'user')
);
DELETE FROM users WHERE username IN ('superadmin', 'admin', 'instructor', 'user');

-- Remove sample course data
DELETE FROM courses WHERE course_code = 'JAVA101';

-- Remove sample instructor data
DELETE FROM instructors WHERE email = 'instructor@example.com';
```

**Note:** Only run cleanup commands in development/testing environments!

---

#### Integration Testing Workflow

1. **Start Application:** Verify DataLoader runs successfully
2. **Authentication Test:** Login with all four sample users
3. **Role Verification:** Test role-based access control
4. **Profile Testing:** Verify instructor profile functionality
5. **Course Testing:** Test course and module data
6. **SuperAdmin Testing:** Verify SuperAdmin exclusive functionality
7. **Cross-Role Testing:** Test interactions between different roles

This sample data provides a complete foundation for testing all aspects of the LMS system with proper role hierarchy and realistic data relationships.