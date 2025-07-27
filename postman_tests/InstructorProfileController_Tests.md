### InstructorProfileController Tests (`/api/instructor`)

**All endpoints in this controller require a valid JWT token in the `Authorization` header.** The server will programmatically check if the user is a `SUPER_ADMIN`, `ADMIN`, or is the instructor accessing their own profile.

**Before you start:**
- Ensure you have instructor users in the database
- Obtain JWT tokens for different roles (SuperAdmin, Admin, Instructor)
- Create instructor profiles using the create endpoint first

---

#### Profile Management Endpoints

*   **Get Instructor Profile by ID**
    *   **Endpoint:** `/api/instructor/profile/{instructorId}`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Path Parameters:** `instructorId` - ID of the instructor
    *   **Expected Status (Authorized):** `200 OK`
    *   **Expected Status (Not Found):** `404 Not Found`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "phoneNo": "123-456-7890",
            "address": "123 Main St",
            "timezone": "UTC",
            "language": "en",
            "bio": "Experienced instructor in computer science",
            "specialization": "Machine Learning",
            "dateOfJoining": "2025-01-15",
            "bankName": "Example Bank",
            "accountNumber": "1234567890",
            "routingNumber": "123456789",
            "accountHolderName": "John Doe",
            "facebookHandle": "johndoe",
            "linkedinHandle": "john-doe-123",
            "youtubeHandle": "johndoetech",
            "profileImage": {
                "id": 1,
                "imageName": "instructor_profile.jpg",
                "contentType": "image/jpeg",
                "imageData": "..." // Base64 encoded image data
            },
            "instructor": {
                "instructorId": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com",
                "phoneNo": "123-456-7890",
                "dateOfJoining": "2025-01-15",
                "facebookHandle": "johndoe",
                "linkedinHandle": "john-doe-123",
                "youtubeHandle": "johndoetech"
            }
        }
        ```

*   **Create Instructor Profile**
    *   **Endpoint:** `/api/instructor/profile/{instructorId}`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Path Parameters:** `instructorId` - ID of the instructor
    *   **Expected Status (Success):** `201 Created`
    *   **Expected Status (Already Exists):** `400 Bad Request`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "phoneNo": "123-456-7890",
            "dateOfJoining": "2025-01-15",
            "facebookHandle": "johndoe",
            "linkedinHandle": "john-doe-123",
            "youtubeHandle": "johndoetech",
            "instructor": {
                "instructorId": 1,
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com"
            }
        }
        ```

*   **Update Instructor Profile**
    *   **Endpoint:** `/api/instructor/profile/{instructorId}`
    *   **Method:** `PUT`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Path Parameters:** `instructorId` - ID of the instructor
    *   **Request Body (JSON):**
        ```json
        {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john.doe@example.com",
            "phoneNo": "+1234567890",
            "address": "456 Updated St",
            "timezone": "EST",
            "language": "en",
            "bio": "Updated bio - Experienced instructor in computer science with 10+ years",
            "specialization": "Machine Learning and AI",
            "bankName": "Updated Bank",
            "accountNumber": "9876543210",
            "routingNumber": "987654321",
            "accountHolderName": "John Doe",
            "facebookHandle": "johnupdated",
            "linkedinHandle": "john-doe-updated",
            "youtubeHandle": "johnupdatedtech"
        }
        ```
    *   **Expected Status (Success):** `200 OK`
    *   **Expected Status (Not Found):** `400 Bad Request`

*   **Update Instructor Password**
    *   **Endpoint:** `/api/instructor/profile/password?instructorId={instructorId}&newPassword={newPassword}`
    *   **Method:** `PUT`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Query Parameters:**
        *   `instructorId` - ID of the instructor
        *   `newPassword` - New password for the instructor
    *   **Expected Status (Success):** `200 OK`
    *   **Expected Status (Not Found):** `400 Bad Request`

---

#### Utility Endpoints

*   **Get Instructor ID from JWT**
    *   **Endpoint:** `/api/instructor/profile/getInstructorId`
    *   **Method:** `GET`
    *   **Required Role:** Authenticated (any role)
    *   **Headers:** `Authorization: Bearer <instructor_jwt_token>`
    *   **Expected Status (Success):** `200 OK`
    *   **Expected Status (Not Instructor):** `404 Not Found`
    *   **Sample Response Body:** `1` (the instructor's ID)

*   **Get Profile Image ID by Instructor ID**
    *   **Endpoint:** `/api/instructor/profile/getProfileImageID/{instructorId}`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Path Parameters:** `instructorId` - ID of the instructor
    *   **Expected Status (Success):** `200 OK`
    *   **Sample Response Body:** `1` (the profile image ID, or null if not set)

---

#### Profile Image Management

*   **Upload Profile Image**
    *   **Endpoint:** `/api/instructor/profile/pic/upload/{instructorId}`
    *   **Method:** `POST`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Path Parameters:** `instructorId` - ID of the instructor
    *   **Request Body (form-data):**
        *   `file`: Select an image file (JPEG, PNG, GIF)
    *   **Expected Status (Success):** `200 OK`
    *   **Expected Status (Invalid File):** `500 Internal Server Error`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe",
            "profileImage": {
                "id": 1,
                "imageName": "instructor_profile.jpg",
                "contentType": "image/jpeg"
            }
        }
        ```

*   **Get Profile Picture**
    *   **Endpoint:** `/api/instructor/profile/pic/{instructorId}`
    *   **Method:** `GET`
    *   **Required Role:** `SUPER_ADMIN`, `ADMIN`, or Owner (Instructor)
    *   **Path Parameters:** `instructorId` - ID of the instructor
    *   **Expected Status (Success):** `200 OK` (with image data)
    *   **Expected Status (Not Found):** `404 Not Found`
    *   **Response:** Binary image data with appropriate Content-Type header

---

#### Error Responses

*   **Unauthorized Access**
    *   **Expected Status:** `403 Forbidden`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "timestamp": "2025-01-27T10:30:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Access Denied",
            "path": "/api/instructor/profile/1"
        }
        ```

*   **Instructor Profile Not Found**
    *   **Expected Status:** `404 Not Found`
    *   **Sample Response Body:** Empty response body

*   **Invalid File Type**
    *   **Expected Status:** `500 Internal Server Error`
    *   **Sample Response Body:** `"Failed to retrieve profile picture: Invalid file type: text/plain"`

*   **File Upload Error**
    *   **Expected Status:** `500 Internal Server Error`
    *   **Sample Response Body:** `"Failed to retrieve profile picture: File is empty"`

---

#### Testing Workflow

1. **Setup Test Data:**
   - Create instructor users via `/api/instructors` (requires ADMIN)
   - Note the instructor IDs for testing

2. **Create Instructor Profile:**
   ```
   POST /api/instructor/profile/{instructorId}
   Headers: Authorization: Bearer {admin_or_instructor_token}
   ```

3. **Test Profile Management:**
   - Get instructor profile to verify creation
   - Update profile with new information
   - Test password update functionality

4. **Test Image Management:**
   - Upload a profile image
   - Retrieve the uploaded image
   - Verify image metadata

5. **Test Authorization:**
   - Try accessing with different role tokens
   - Verify instructors can only access their own profiles
   - Verify admins and super admins can access all profiles

---

#### Social Media Integration

The instructor profile now supports social media handles:
- **Facebook Handle:** Username or profile identifier for Facebook
- **LinkedIn Handle:** Username or profile identifier for LinkedIn  
- **YouTube Handle:** Channel name or identifier for YouTube

**Example Usage:**
```json
{
    "facebookHandle": "johndoe",
    "linkedinHandle": "john-doe-123", 
    "youtubeHandle": "johndoetech"
}
```

These handles can be used to:
- Display social media links on instructor profiles
- Enable social media integration features
- Provide contact methods for students

---

#### Banking Information Security

The instructor profile includes sensitive banking information:
- Bank Name
- Account Number  
- Routing Number
- Account Holder Name

**Security Considerations:**
- Only authorized users can view/edit banking information
- Consider additional encryption for sensitive fields
- Audit all access to banking information
- Implement field-level permissions if needed

---

#### Integration Notes

- **Detached from UserProfile:** Instructors now have dedicated profiles separate from UserProfile
- **Synchronized Data:** Basic information is synchronized between Instructor and InstructorProfile entities
- **Profile Creation:** Profiles are automatically created during instructor registration
- **Backward Compatibility:** Existing instructor data remains intact

---

#### New Features Summary

1. **Social Media Handles:** Facebook, LinkedIn, YouTube integration
2. **Comprehensive Profile:** Bio, specialization, banking information
3. **Image Management:** Profile picture upload and retrieval
4. **Dedicated System:** Separate from UserProfile for better organization
5. **Enhanced Security:** Role-based access control with SuperAdmin support