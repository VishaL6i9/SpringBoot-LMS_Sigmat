### Testing Notes for Subscription System

1. **Plan IDs:** After the application starts, the subscription plans will be automatically loaded. Use the following approximate IDs:
   - Foundation (Learner): ID 1
   - Essential (Learner): ID 2
   - Professional (Learner): ID 3
   - Mastery (Learner): ID 4
   - Institutional (Learner): ID 5
   - Starter (Faculty): ID 6
   - Educator (Faculty): ID 7
   - Mentor (Faculty): ID 8
   - Institutional (Faculty): ID 9
   - *For course-specific plans, you will need to create them via the admin API or directly in the database, associating them with a `courseId`.*

2. **Subscription Status:** Valid values are `ACTIVE`, `INACTIVE`, `EXPIRED`, `CANCELLED`, `PENDING`

3. **Plan Types:** Valid values are `LEARNER`, `FACULTY`

4. **Testing Workflow:**
   - First, get available plans using `/api/subscriptions/plans?courseId={courseId}` (for course-specific) or `/api/subscriptions/plans/learner` or `/api/subscriptions/plans/faculty` (for platform-wide)
   - Subscribe a user using `/api/subscriptions/users/{userId}/subscribe` (for platform-wide) or `/api/subscriptions/courses/{courseId}/users/{userId}/subscribe` (for course-specific)
   - Check subscription status using `/api/subscriptions/users/{userId}/current` (for platform-wide) or `/api/subscriptions/courses/{courseId}/users/{userId}/current` (for course-specific)
   - Test cancellation using `/api/subscriptions/{subscriptionId}/cancel`

5. **Automatic Expiration:** The system runs a scheduled task daily at 2 AM to expire subscriptions. You can manually trigger this using the admin endpoint.
