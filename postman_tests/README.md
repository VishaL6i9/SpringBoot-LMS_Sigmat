# Postman Test Cases

This directory contains Postman test cases, broken down by controller for better organization and readability.

## Structure

Each `.md` file in this directory corresponds to a specific controller or a logical group of related endpoints. The filename indicates the controller it covers (e.g., `AuthController_Tests.md`, `SubscriptionController_Tests.md`).

## Test Files Overview

### Core Controllers
- `AuthController_Tests.md` - Authentication and user registration
- `UserProfileController_Tests.md` - User profile management
- `CourseController_Tests.md` - Course CRUD operations
- `InstructorController_Tests.md` - Instructor management
- `LessonController_Tests.md` - Lesson management (polymorphic)

### Subscription System
- `SubscriptionController_Tests.md` - **UPDATED** with Stripe checkout integration
- `AdminSubscriptionController_Tests.md` - Admin subscription management
- `UserProfileController_Subscription_Tests.md` - User subscription integration
- `Testing_Notes_for_Subscription_System.md` - General subscription testing notes

### Payment & Checkout
- `CheckoutController_Tests.md` - **UPDATED** with deprecated endpoint notices
- `StripeCheckoutIntegration_Tests.md` - **NEW** Comprehensive Stripe integration testing
- `InvoiceController_Tests.md` - Invoice management

### Course Management
- `CourseAllotmentController_Tests.md` - **UPDATED** with DTO response format
- `CourseModuleController_Tests.md` - Module management
- `LessonController_Tests.md` - Lesson management

### Other Features
- `NotificationController_Tests.md` - Notification system
- `AssignmentController_Tests.md` - Assignment management
- `QuizController_Tests.md` - Quiz functionality
- `CertificateController_Tests.md` - Certificate generation
- `VideoController_Tests.md` - Video management
- `ProfileImageController_Tests.md` - Profile image handling
- `AdminController_Tests.md` - Admin operations

## Recent Updates (v2.1.0)

### NEW: Stripe Checkout Integration
- **StripeCheckoutIntegration_Tests.md** - Comprehensive testing guide for new Stripe checkout flow
- Platform subscription checkout: `POST /api/subscriptions/users/{userId}/checkout`
- Course subscription checkout: `POST /api/subscriptions/courses/{courseId}/users/{userId}/checkout`
- Success handling: `POST /api/subscriptions/checkout/success`
- Webhook support: `POST /api/subscriptions/checkout/webhook`

### UPDATED: Subscription System
- Enhanced SubscriptionController with checkout session endpoints
- Improved error handling and validation
- Better authorization checks
- Comprehensive metadata tracking

### DEPRECATED: Legacy Checkout Endpoints
- `/api/checkout/platform-subscription` → Use `/api/subscriptions/users/{userId}/checkout`
- `/api/checkout/course-subscription` → Use `/api/subscriptions/courses/{courseId}/users/{userId}/checkout`
- `/api/checkout/success` → Use `/api/subscriptions/checkout/success`

### FIXED: Course Module Serialization
- CourseAllotmentController now returns DTOs to prevent LOB serialization errors
- Enhanced lesson type handling with proper polymorphic serialization

## How to Use

1.  Navigate to the relevant `.md` file for the controller you wish to test.
2.  Each file contains detailed instructions, endpoints, request bodies, and expected responses for various API calls.
3.  Ensure you have the necessary setup (e.g., authenticated users, existing data) as described in the "Before you start" section.
4.  Copy the request details into Postman and execute the requests.

## Testing Workflows

### Basic Setup
1. Start with `AuthController_Tests.md` to create users and obtain JWT tokens
2. Use `UserProfileController_Tests.md` to set up user profiles
3. Create courses using `CourseController_Tests.md`

### Subscription Testing
1. Review `SubscriptionController_Tests.md` for plan management
2. Follow `StripeCheckoutIntegration_Tests.md` for payment flow testing
3. Use test Stripe cards for different payment scenarios

### Course Content Testing
1. Use `CourseAllotmentController_Tests.md` to create course modules
2. Add lessons using `LessonController_Tests.md`
3. Test content delivery and serialization

## Important Notes

### Authentication
- Most endpoints require a JWT token obtained via `/api/public/login`
- Use `Authorization: Bearer {token}` header format
- Admin endpoints require `ADMIN` role
- User-specific endpoints validate ownership or admin access

### Stripe Configuration
- Ensure `stripe.api.key` is configured in `application.properties`
- Use Stripe test keys for development/testing
- Test with Stripe test card numbers (e.g., `4242424242424242`)

### Placeholders
Replace these placeholders with actual values:
- `{userId}` - User ID from authentication
- `{courseId}` - Course ID from course creation
- `{planId}` - Subscription plan ID
- `{sessionId}` - Stripe checkout session ID
- `{subscriptionId}` - Subscription ID

### Error Handling
- Check response status codes and error messages
- Validate authorization for protected endpoints
- Test edge cases and invalid inputs

## Troubleshooting

### Common Issues
1. **401 Unauthorized** - Check JWT token validity and format
2. **403 Forbidden** - Verify user has required role/permissions
3. **404 Not Found** - Ensure resource IDs exist in database
4. **400 Bad Request** - Validate request body format and required fields
5. **LOB Stream Errors** - Use DTO endpoints instead of entity endpoints

### Stripe-Specific Issues
1. **Invalid Session** - Check session ID format and expiration
2. **Payment Not Completed** - Verify payment status in Stripe dashboard
3. **Plan Not Found** - Ensure plan ID exists and is active
4. **Currency Issues** - Verify INR to USD conversion rates

For detailed troubleshooting, refer to the specific test file or `StripeCheckoutIntegration_Tests.md` for payment-related issues.
