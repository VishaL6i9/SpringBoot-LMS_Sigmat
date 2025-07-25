# Course Purchase System Testing Guide

## Overview

This guide covers testing the individual course purchase system, which allows users to buy specific courses directly (separate from platform subscriptions).

## Prerequisites

### Test Environment Setup
- Local server running on `http://localhost:8080`
- Valid JWT tokens for different user roles
- Stripe test environment configured
- Test courses with `courseFee` > 0

### Required Test Data
```json
{
  "testUser": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  },
  "testCourse": {
    "courseId": 1,
    "courseName": "Test Course",
    "courseCode": "TEST101",
    "courseFee": 1000
  },
  "adminUser": {
    "id": 2,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

## Test Scenarios

### 1. Happy Path - Complete Course Purchase

#### Step 1: Check Initial Purchase Status
```http
GET /api/courses/1/users/1/has-purchased
Authorization: Bearer {{userToken}}
```
**Expected Response:**
```json
{
  "hasPurchased": false
}
```

#### Step 2: Create Checkout Session
```http
POST /api/courses/1/users/1/checkout
Authorization: Bearer {{userToken}}
Content-Type: application/json

{
  "successUrl": "http://localhost:3000/success",
  "cancelUrl": "http://localhost:3000/cancel",
  "discountApplied": 100.00
}
```
**Expected Response:**
```json
{
  "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_...",
  "purchaseId": 123,
  "courseId": 1,
  "userId": 1,
  "finalAmount": 900.00
}
```

#### Step 3: Simulate Stripe Payment Success
```http
POST /api/courses/checkout/success?sessionId=cs_test_123&userId=1
```
**Expected Response:**
```json
{
  "purchase": {
    "id": 123,
    "status": "COMPLETED",
    "finalAmount": 900.00
  },
  "message": "Course purchased successfully"
}
```

#### Step 4: Verify Purchase Completion
```http
GET /api/courses/1/users/1/has-purchased
Authorization: Bearer {{userToken}}
```
**Expected Response:**
```json
{
  "hasPurchased": true
}
```

### 2. Error Scenarios

#### Test: Duplicate Purchase Prevention
```http
POST /api/courses/1/users/1/checkout
Authorization: Bearer {{userToken}}
```
**Expected Response:** `400 Bad Request`
```json
{
  "error": "User has already purchased this course"
}
```

#### Test: Unauthorized Access
```http
POST /api/courses/1/users/2/checkout
Authorization: Bearer {{userToken}}
```
**Expected Response:** `403 Forbidden`

#### Test: Course Not Found
```http
POST /api/courses/999/users/1/checkout
Authorization: Bearer {{userToken}}
```
**Expected Response:** `404 Not Found`

#### Test: Payment Not Completed
```http
POST /api/courses/checkout/success?sessionId=invalid_session&userId=1
```
**Expected Response:** `400 Bad Request`
```json
{
  "error": "Payment not completed"
}
```

### 3. Purchase History Testing

#### Test: Get User's Purchase History
```http
GET /api/courses/users/1/purchases
Authorization: Bearer {{userToken}}
```
**Expected Response:**
```json
[
  {
    "id": 123,
    "courseId": 1,
    "courseName": "Test Course",
    "status": "COMPLETED",
    "finalAmount": 900.00,
    "purchaseDate": "2024-01-15T10:30:00"
  }
]
```

#### Test: Get Specific Course Purchase
```http
GET /api/courses/1/users/1/purchase
Authorization: Bearer {{userToken}}
```
**Expected Response:**
```json
{
  "id": 123,
  "userId": 1,
  "courseId": 1,
  "status": "COMPLETED",
  "finalAmount": 900.00
}
```

### 4. Analytics Testing (Admin/Instructor Only)

#### Test: Course Purchase Analytics
```http
GET /api/courses/1/purchases
Authorization: Bearer {{adminToken}}
```
**Expected Response:**
```json
[
  {
    "id": 123,
    "userId": 1,
    "username": "testuser",
    "finalAmount": 900.00,
    "purchaseDate": "2024-01-15T10:30:00"
  }
]
```

#### Test: Course Revenue Summary
```http
GET /api/courses/1/revenue
Authorization: Bearer {{adminToken}}
```
**Expected Response:**
```json
{
  "courseId": 1,
  "totalRevenue": 900,
  "totalEnrollments": 1
}
```

## Stripe Integration Testing

### Test Cards for Different Scenarios

#### Successful Payment
- **Card Number:** `4242424242424242`
- **Expiry:** Any future date
- **CVC:** Any 3 digits

#### Payment Requires Authentication
- **Card Number:** `4000002500003155`
- **Expiry:** Any future date
- **CVC:** Any 3 digits

#### Payment Fails
- **Card Number:** `4000000000000002`
- **Expiry:** Any future date
- **CVC:** Any 3 digits

### Webhook Testing

#### Setup Stripe CLI for Local Testing
```bash
stripe listen --forward-to localhost:8080/api/webhook/stripe
```

#### Test Webhook Events
- `checkout.session.completed` - Triggers course purchase completion
- `payment_intent.succeeded` - Confirms payment success
- `payment_intent.payment_failed` - Handles payment failures

## Performance Testing

### Load Testing Scenarios
1. **Concurrent Checkouts:** Multiple users creating checkout sessions simultaneously
2. **Success Handler Load:** Multiple success callbacks processing simultaneously
3. **Purchase History Queries:** High-frequency purchase status checks

### Expected Performance Metrics
- Checkout session creation: < 2 seconds
- Purchase completion: < 1 second
- Purchase status queries: < 500ms
- Revenue calculations: < 1 second

## Security Testing

### Authentication Tests
- [ ] Unauthenticated requests are rejected
- [ ] Users can only purchase courses for themselves
- [ ] Admin users can access all purchase data
- [ ] Instructors can access their course analytics

### Authorization Tests
- [ ] Regular users cannot access admin endpoints
- [ ] Users cannot access other users' purchase history
- [ ] Session validation prevents tampering

### Input Validation Tests
- [ ] Course ID validation prevents injection
- [ ] User ID validation prevents unauthorized access
- [ ] Discount amount validation prevents negative values
- [ ] URL validation prevents malicious redirects

## Integration Testing

### Database Integration
- [ ] Purchase records are created correctly
- [ ] Enrollment records are auto-created
- [ ] Transaction rollback on failures
- [ ] Concurrent purchase prevention

### Stripe Integration
- [ ] Checkout sessions are created with correct metadata
- [ ] Payment status is verified correctly
- [ ] Webhook events are processed properly
- [ ] Failed payments are handled gracefully

### Notification Integration
- [ ] Purchase confirmation notifications are sent
- [ ] Payment failure notifications are sent
- [ ] Admin notifications for new purchases

## Troubleshooting Common Issues

### "Course not found" Error
- Verify course exists in database
- Check course ID parameter
- Ensure course has a valid `courseFee`

### "User has already purchased" Error
- Check purchase status in database
- Verify user ID and course ID combination
- Look for completed purchases

### Stripe Session Creation Failures
- Verify Stripe API key configuration
- Check network connectivity to Stripe
- Validate request parameters

### Purchase Completion Failures
- Check Stripe session status
- Verify webhook configuration
- Check database connectivity

## Test Automation

### Postman Collection Variables
```json
{
  "baseUrl": "http://localhost:8080",
  "userToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "adminToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "1",
  "courseId": "1",
  "testSessionId": "cs_test_example"
}
```

### Pre-request Scripts
```javascript
// Generate test data
pm.globals.set("timestamp", Date.now());
pm.globals.set("testEmail", `test${Date.now()}@example.com`);
```

### Test Scripts
```javascript
// Verify successful purchase creation
pm.test("Purchase created successfully", function () {
    pm.response.to.have.status(200);
    pm.expect(pm.response.json()).to.have.property("purchaseId");
    pm.expect(pm.response.json()).to.have.property("sessionUrl");
});

// Store purchase ID for subsequent tests
pm.test("Store purchase ID", function () {
    const response = pm.response.json();
    pm.globals.set("purchaseId", response.purchaseId);
});
```

## Monitoring and Logging

### Key Metrics to Monitor
- Purchase success rate
- Average purchase value
- Course conversion rates
- Payment processing time
- Error rates by endpoint

### Important Log Events
- Purchase creation attempts
- Payment completions
- Payment failures
- Auto-enrollment events
- Revenue calculations

### Alerting Setup
- High error rates on purchase endpoints
- Stripe webhook failures
- Database connection issues
- Unusual purchase patterns