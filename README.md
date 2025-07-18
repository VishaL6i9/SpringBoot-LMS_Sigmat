# Sigmat LMS Backend

## Project Overview
The Sigmat Learning Management System (LMS) is a robust backend application built with Spring Boot, designed to manage courses, users, notifications, and certificates. It features a comprehensive Role-Based Access Control (RBAC) system to ensure secure and appropriate access to resources.

## Key Features
*   **User Management:**
    *   User registration and authentication (JWT-based).
    *   Role management for `ADMIN`, `INSTRUCTOR`, and `USER` roles.
    *   User profile management, including password updates and profile image uploads.
*   **Course Management:**
    *   Create, update, view, and delete courses.
    *   Video content management for courses.
*   **Notification System:**
    *   Create and send notifications to users.
    *   Manage notification status (read/unread).
*   **Certificate Management:**
    *   Issue and manage certificates for course completion.
*   **Payment Integration:**
    *   Stripe integration for checkout sessions (e.g., course enrollment).
*   **Role-Based Access Control (RBAC):** Granular control over API access based on user roles.

## Technologies Used
*   **Backend:** Spring Boot, Java
*   **Security:** Spring Security, JWT (JSON Web Tokens)
*   **Database:** MySQL (or H2 for development)
*   **ORM:** Spring Data JPA, Hibernate
*   **Payment Gateway:** Stripe
*   **Build Tool:** Maven
*   **Other:** Flyway (for database migrations)

## Setup and Installation

### Prerequisites
*   Java 17 or higher
*   Maven 3.6+
*   MySQL or PostgreSQL Database (or configure for H2 for quick local setup)

### Steps
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/springboot-sigmat_lms.git
    cd springboot-sigmat_lms
    ```
2.  **Database Configuration:**
    *   Create a MySQL database (e.g., `lms_db`).
    *   Update `src/main/resources/application.properties` (or `application.yml`) with your database credentials:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/lms_db
        spring.datasource.username=your_mysql_username
        spring.datasource.password=your_mysql_password
        spring.jpa.hibernate.ddl-auto=update # or none, if using Flyway for migrations
        ```
    *   Flyway migrations are configured under `src/main/resources/db/migration`. Ensure your database is up-to-date with the schema.
3.  **Stripe Configuration:**
    *   Add your Stripe secret key to `application.properties`:
        ```properties
        stripe.secret.key=sk_test_YOUR_STRIPE_SECRET_KEY
        ```
4.  **Build the project:**
    ```bash
    mvn clean install
    ```
5.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` by default.

## API Endpoints

The API is secured with JWT. Most endpoints require authentication.

### Authentication
*   `POST /api/public/authenticate`: Authenticate a user and receive a JWT token.
*   `GET /api/public/role`: Get the role of the currently logged-in user.

### Admin Controller (`/api/admin`) - Requires `ADMIN` Role
*   `GET /api/admin/users`: Get all users.
*   `DELETE /api/admin/delete/user/{username}`: Delete a user.
*   `PUT /api/admin/user/{userId}/role?newRole={newRole}`: Change a user's role.

### User Profile Controller (`/api/user`) - Requires `ADMIN`, `INSTRUCTOR`, or `USER` Role
*   `GET /api/user/profile/{userID}`: Get a user's profile (self-access or ADMIN).
*   `PUT /api/user/profile`: Update a user's profile (self-access or ADMIN).
*   `PUT /api/user/profile/password?userID={userID}&newPassword={newPassword}`: Update a user's password (self-access or ADMIN).

### Course Controller (`/api/courses`)
*   `POST /api/courses`: Create a course (ADMIN, INSTRUCTOR).
*   `PUT /api/courses/{courseId}`: Update a course (ADMIN, INSTRUCTOR).
*   `GET /api/courses`: Get all courses (ADMIN, INSTRUCTOR, USER).
*   `GET /api/courses/{courseId}`: Get course by ID (ADMIN, INSTRUCTOR, USER).
*   `DELETE /api/courses/{courseId}`: Delete a course (ADMIN).

### Notification Controller (`/api/notifications`)
*   `POST /api/notifications?userId={userId}`: Create a notification (ADMIN, INSTRUCTOR).
*   `GET /api/notifications/user/{userId}`: Get user notifications (self-access or ADMIN).
*   `PUT /api/notifications/{id}/read`: Mark notification as read (self-access or ADMIN).

### Video Controller (`/api/videos`)
*   `POST /api/videos/upload`: Upload a video (ADMIN, INSTRUCTOR).
*   `GET /api/videos`: Get all videos (ADMIN, INSTRUCTOR, USER).
*   `DELETE /api/videos/{id}`: Delete a video (ADMIN).

### Certificate Controller (`/api/certificates`)
*   `POST /api/certificates`: Create a certificate (ADMIN, INSTRUCTOR).
*   `GET /api/certificates/{id}`: Get certificate by ID (self-access or ADMIN/INSTRUCTOR).
*   `GET /api/certificates`: Get all certificates (ADMIN, INSTRUCTOR).
*   `DELETE /api/certificates/{id}`: Delete a certificate (ADMIN).

### Checkout Controller (`/api/checkout`)
*   `POST /api/checkout/create-checkout-session`: Create a Stripe checkout session (USER).

## Role-Based Access Control (RBAC) Details
The application implements a robust RBAC system with the following roles and their general permissions:

*   **ADMIN:** Full access to all administrative and instructor functionalities, including user management, course management, notification creation, video uploads, and certificate management.
*   **INSTRUCTOR:** Can create, update, and view courses; upload videos; create notifications; and manage certificates. They have limited access to user management (e.g., can't delete users or change roles).
*   **USER:** Can view courses, access their own profile and notifications, and initiate checkout sessions. They have no administrative or instructor privileges.

Permissions are managed through `@PreAuthorize` annotations on controller methods, ensuring that only authorized roles can access specific endpoints or perform certain actions.

## Postman Test Cases
Sample test cases for Postman to verify the role-based access control are provided in `postman_test_cases.md`.

**Before you start:**

*   **Ensure you have users with `ADMIN`, `INSTRUCTOR`, and `USER` roles in your database.** If not, you'll need to create them first.
*   **Authentication:** All protected endpoints require a JWT token in the `Authorization` header (e.g., `Bearer <your_jwt_token>`). You'll need to obtain this token by authenticating a user.

## Contributing
Contributions are welcome! Please fork the repository and submit pull requests.
