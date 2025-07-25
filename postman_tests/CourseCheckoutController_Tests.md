### 20. CourseCheckoutController Tests (`/api/courses`)

**Course Purchase System - Individual course purchases separate from platform subscriptions**

#### Course Purchase Endpoints

*   **Create Course Checkout Session**
    *   **Endpoint:** `/api/courses/{courseId}/users/{userId}/checkout`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN` or Owner (`#userId == authentication.principal.id`)
    *   **Request Body (JSON):**
        ```json
        {
            "successUrl": "http://localhost:3000/course/purchase/success",
            "cancelUrl": "http://localhost:3000/course/purchase/cancel",
            "discountApplied": 100.00,
            "couponCode": "DISCOUNT10"
        }
        ```
    *   **Expected Status (ADMIN or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
            "purchaseId": 123,
            "courseId": 456,
            "userId": 789,
            "finalAmount": 900.00
        }
        ```
    *   **Error Response (Already Purchased):**
        ```json
        {
            "error": "User has already purchased this course"
        }
        ```

*   **Handle Course Checkout Success**
    *   **Endpoint:** `/api/courses/checkout/success?sessionId={sessionId}&userId={userId}`
    *   **Method:** `POST`
    *   **Required Role:** None (validates session internally)
    *   **Expected Status:** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "purchase": {
                "id": 123,
                "userId": 789,
                "username": "john_doe",
                "courseId": 456,
                "courseName": "Advanced Java Programming",
                "courseCode": "JAVA301",
                "purchasePrice": 1000.00,
                "discountApplied": 100.00,
                "finalAmount": 900.00,
                "status": "COMPLETED",
                "paymentReference": "stripe_session_cs_test_...",
                "purchaseDate": "2024-01-15T10:30:00",
                "accessGrantedDate": "2024-01-15T10:30:00",
                "createdAt": "2024-01-15T10:25:00",
                "updatedAt": "2024-01-15T10:30:00"
            },
            "sessionId": "cs_test_...",
            "message": "Course purchased successfully"
        }
        ```
    *   **Error Response (Payment Not Completed):**
        ```json
        {
            "error": "Payment not completed"
        }
        ```

#### Purchase Status and History Endpoints

*   **Get User's Course Purchase**
    *   **Endpoint:** `/api/courses/{courseId}/users/{userId}/purchase`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner (`#userId == authentication.principal.id`)
    *   **Expected Status (ADMIN or Owner):** `200 OK` or `404 Not Found`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 123,
            "userId": 789,
            "username": "john_doe",
            "courseId": 456,
            "courseName": "Advanced Java Programming",
            "courseCode": "JAVA301",
            "purchasePrice": 1000.00,
            "discountApplied": 100.00,
            "finalAmount": 900.00,
            "status": "COMPLETED",
            "paymentReference": "stripe_session_cs_test_...",
            "purchaseDate": "2024-01-15T10:30:00",
            "accessGrantedDate": "2024-01-15T10:30:00"
        }
        ```

*   **Check if User Has Purchased Course**
    *   **Endpoint:** `/api/courses/{courseId}/users/{userId}/has-purchased`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner (`#userId == authentication.principal.id`)
    *   **Expected Status (ADMIN or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "hasPurchased": true
        }
        ```

*   **Get User's All Course Purchases**
    *   **Endpoint:** `/api/courses/users/{userId}/purchases`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or Owner (`#userId == authentication.principal.id`)
    *   **Expected Status (ADMIN or Owner):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 123,
                "userId": 789,
                "username": "john_doe",
                "courseId": 456,
                "courseName": "Advanced Java Programming",
                "courseCode": "JAVA301",
                "purchasePrice": 1000.00,
                "discountApplied": 100.00,
                "finalAmount": 900.00,
                "status": "COMPLETED",
                "purchaseDate": "2024-01-15T10:30:00"
            },
            {
                "id": 124,
                "userId": 789,
                "username": "john_doe",
                "courseId": 457,
                "courseName": "Spring Boot Masterclass",
                "courseCode": "SPRING201",
                "purchasePrice": 1500.00,
                "discountApplied": 0.00,
                "finalAmount": 1500.00,
                "status": "COMPLETED",
                "purchaseDate": "2024-01-20T14:15:00"
            }
        ]
        ```

#### Analytics and Revenue Endpoints (Admin/Instructor Only)

*   **Get Course Purchase Analytics**
    *   **Endpoint:** `/api/courses/{courseId}/purchases`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or `INSTRUCTOR`
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        [
            {
                "id": 123,
                "userId": 789,
                "username": "john_doe",
                "courseId": 456,
                "courseName": "Advanced Java Programming",
                "courseCode": "JAVA301",
                "finalAmount": 900.00,
                "purchaseDate": "2024-01-15T10:30:00"
            },
            {
                "id": 125,
                "userId": 790,
                "username": "jane_smith",
                "courseId": 456,
                "courseName": "Advanced Java Programming",
                "courseCode": "JAVA301",
                "finalAmount": 1000.00,
                "purchaseDate": "2024-01-16T09:45:00"
            }
        ]
        ```

*   **Get Course Revenue Summary**
    *   **Endpoint:** `/api/courses/{courseId}/revenue`
    *   **Method:** `GET`
    *   **Required Role:** `ADMIN` or `INSTRUCTOR`
    *   **Expected Status (ADMIN/INSTRUCTOR):** `200 OK`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "courseId": 456,
            "totalRevenue": 45000,
            "totalEnrollments": 50
        }
        ```

#### Testing Flow

1. **Check Purchase Status**: `GET /api/courses/{courseId}/users/{userId}/has-purchased`
2. **Create Checkout**: `POST /api/courses/{courseId}/users/{userId}/checkout`
3. **Complete Payment**: User completes payment on Stripe checkout page
4. **Handle Success**: `POST /api/courses/checkout/success?sessionId={sessionId}&userId={userId}`
5. **Verify Purchase**: `GET /api/courses/{courseId}/users/{userId}/purchase`
6. **Check Enrollment**: User should be automatically enrolled in the course

#### Error Scenarios

*   **Course Not Found**: `404 Not Found` - "Course not found with id: {courseId}"
*   **User Not Found**: `404 Not Found` - "User not found with id: {userId}"
*   **Already Purchased**: `400 Bad Request` - "User has already purchased this course"
*   **Payment Failed**: `500 Internal Server Error` - "Error processing course purchase"
*   **Unauthorized**: `403 Forbidden` - User can only purchase courses for themselves

#### Integration Notes

- Course purchases automatically create enrollment records
- Users get immediate access to course content after successful payment
- Purchase history is maintained for analytics and support
- Stripe webhooks handle payment completion asynchronously
- Failed purchases are tracked for debugging and retry logic

#### Test Data Requirements

- Valid course with `courseFee` > 0
- Valid user account
- Stripe test card numbers for payment testing
- Valid success/cancel URLs for redirect testing