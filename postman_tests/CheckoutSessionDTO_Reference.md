# CheckoutSessionDTO Reference

This document provides a comprehensive reference for the enhanced `CheckoutSessionDTO` used in the Stripe checkout integration.

## DTO Structure

The `CheckoutSessionDTO` supports both legacy and new subscription flows with backward compatibility.

```json
{
  // Legacy fields (for backward compatibility)
  "tier": "professional",
  
  // New subscription fields
  "planId": 2,
  "durationMonths": 6,
  "autoRenew": true,
  
  // Common fields
  "successUrl": "https://yourapp.com/success",
  "cancelUrl": "https://yourapp.com/cancel",
  "userId": 123,
  "courseId": 456,
  "instructorId": 789
}
```

## Field Descriptions

### Legacy Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `tier` | String | No | Legacy subscription tier (e.g., "starter", "professional", "enterprise") |

### New Subscription Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `planId` | Long | Yes* | ID of the subscription plan from the database |
| `durationMonths` | Integer | No | Subscription duration in months (defaults to plan's minimum) |
| `autoRenew` | Boolean | No | Whether subscription should auto-renew (defaults to true) |

*Required for new subscription flow

### Common Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `successUrl` | String | Yes | URL to redirect after successful payment |
| `cancelUrl` | String | Yes | URL to redirect after cancelled payment |
| `userId` | Long | No | User ID (used in legacy flow for enrollment) |
| `courseId` | Long | No | Course ID (used in legacy flow for enrollment) |
| `instructorId` | Long | No | Instructor ID (used in legacy flow for enrollment) |

## Usage Examples

### Platform Subscription Checkout

```json
{
  "planId": 2,
  "durationMonths": 6,
  "successUrl": "https://yourapp.com/subscription/success",
  "cancelUrl": "https://yourapp.com/subscription/cancel"
}
```

### Course Subscription Checkout

```json
{
  "planId": 10,
  "durationMonths": 3,
  "successUrl": "https://yourapp.com/course/subscription/success",
  "cancelUrl": "https://yourapp.com/course/subscription/cancel"
}
```

### Legacy Tier-Based Checkout

```json
{
  "tier": "professional",
  "successUrl": "https://yourapp.com/success",
  "cancelUrl": "https://yourapp.com/cancel",
  "userId": 123,
  "courseId": 456,
  "instructorId": null
}
```

### Mixed Legacy/New (Backward Compatibility)

```json
{
  "planId": 2,
  "tier": "professional",
  "durationMonths": 6,
  "successUrl": "https://yourapp.com/success",
  "cancelUrl": "https://yourapp.com/cancel"
}
```
*Note: When both `planId` and `tier` are provided, `planId` takes precedence*

## Validation Rules

### Required Field Validation
- Either `planId` OR `tier` must be provided
- `successUrl` and `cancelUrl` are always required
- URLs must be valid HTTP/HTTPS URLs

### Plan-Based Validation
- `planId` must exist in the database
- Plan must be active (`isActive = true`)
- For course subscriptions, plan must be associated with the specified course

### Duration Validation
- `durationMonths` must be positive integer
- If not provided, defaults to plan's `minimumDurationMonths`
- Cannot be less than plan's minimum duration

### URL Validation
- Must be valid HTTP/HTTPS URLs
- Should not contain sensitive information
- Recommended to use HTTPS for production

## Response Formats

### Successful Checkout Session Creation

```json
{
  "sessionUrl": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
  "planId": 2,
  "userId": 123,
  "courseId": null
}
```

### Error Responses

#### Missing Plan ID
```json
{
  "error": "Plan ID is required for platform subscription"
}
```

#### Invalid Plan
```json
{
  "error": "Subscription plan not found with id: 999"
}
```

#### Inactive Plan
```json
{
  "error": "Subscription plan is not active"
}
```

#### Course Plan Mismatch
```json
{
  "error": "Plan is not valid for the specified course"
}
```

## Endpoint Compatibility

### New Endpoints (Recommended)

| Endpoint | DTO Usage |
|----------|-----------|
| `POST /api/subscriptions/users/{userId}/checkout` | Plan-based fields required |
| `POST /api/subscriptions/courses/{courseId}/users/{userId}/checkout` | Plan-based fields required |

### Legacy Endpoints (Deprecated)

| Endpoint | DTO Usage |
|----------|-----------|
| `POST /api/checkout/create-checkout-session` | Supports both tier and plan-based |
| `POST /api/checkout/platform-subscription` | Redirects to new endpoint |
| `POST /api/checkout/course-subscription` | Redirects to new endpoint |

## Migration Guide

### From Legacy Tier-Based to Plan-Based

1. **Identify Plan Mapping:**
   ```
   "starter" tier → Foundation plan (planId: 1)
   "professional" tier → Essential plan (planId: 2)
   "enterprise" tier → Professional plan (planId: 3)
   ```

2. **Update Request Body:**
   ```json
   // OLD
   {
     "tier": "professional",
     "successUrl": "...",
     "cancelUrl": "..."
   }
   
   // NEW
   {
     "planId": 2,
     "durationMonths": 6,
     "successUrl": "...",
     "cancelUrl": "..."
   }
   ```

3. **Update Endpoint:**
   ```
   OLD: POST /api/checkout/create-checkout-session
   NEW: POST /api/subscriptions/users/{userId}/checkout
   ```

### Gradual Migration Strategy

1. **Phase 1:** Use new plan-based fields with legacy endpoint
2. **Phase 2:** Switch to new endpoints while maintaining plan-based fields
3. **Phase 3:** Remove legacy field usage entirely

## Best Practices

### Security
- Always use HTTPS for success/cancel URLs
- Validate URLs on the server side
- Don't include sensitive data in URLs

### User Experience
- Provide clear success and cancel page experiences
- Handle session expiration gracefully
- Show loading states during checkout creation

### Error Handling
- Validate plan availability before checkout creation
- Handle network failures gracefully
- Provide meaningful error messages to users

### Testing
- Test with Stripe test cards
- Verify metadata is correctly set
- Test both success and failure scenarios
- Validate subscription creation after payment

## Common Pitfalls

1. **Missing Plan Validation:** Always verify plan exists and is active
2. **URL Format Issues:** Ensure URLs are properly formatted and accessible
3. **Duration Conflicts:** Don't set duration less than plan minimum
4. **Course Mismatch:** Verify course-specific plans match the course
5. **Authentication Issues:** Ensure proper JWT token for protected endpoints