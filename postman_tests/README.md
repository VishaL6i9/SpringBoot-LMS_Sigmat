# Postman Test Cases

This directory contains Postman test cases, broken down by controller for better organization and readability.

## Structure

Each `.md` file in this directory corresponds to a specific controller or a logical group of related endpoints. The filename indicates the controller it covers (e.g., `AuthController_Tests.md`, `SubscriptionController_Tests.md`).

## How to Use

1.  Navigate to the relevant `.md` file for the controller you wish to test.
2.  Each file contains detailed instructions, endpoints, request bodies, and expected responses for various API calls.
3.  Ensure you have the necessary setup (e.g., authenticated users, existing data) as described in the "Before you start" section of the original `postman_test_cases.md` (now likely in `Testing_Notes_for_Subscription_System.md` or similar).
4.  Copy the request details into Postman and execute the requests.

## Important Notes

*   **Authentication:** Most endpoints require a JWT token. Obtain this by logging in via the `/api/public/login` endpoint.
*   **Placeholders:** Remember to replace placeholders like `{userId}`, `{courseId}`, `{planId}`, etc., with actual values from your environment.
*   **Subscription System Notes:** Refer to `Testing_Notes_for_Subscription_System.md` for general notes and plan IDs related to the subscription system.
