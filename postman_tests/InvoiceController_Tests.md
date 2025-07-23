### 16. InvoiceController Tests (`/api/invoices`)

*   **Create Invoice**
    *   **Endpoint:** `/api/invoices`
    *   **Method:** `POST`
    *   **Required Role:** `ADMIN`, `INSTRUCTOR`
    *   **Request Body (JSON):**
        ```json
        {
            "invoiceNumber": "INV-2025-001",
            "date": "2025-07-14",
            "dueDate": "2025-08-14",
            "user": {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "phone": "111-222-3333",
                "address": "123 Main St, Anytown, USA"
            },
            "items": [
                {
                    "description": "Course Enrollment: Advanced Java",
                    "quantity": 1,
                    "unitPrice": 500.00,
                    "total": 500.00
                }
            ],
            "subtotal": 500.00,
            "taxRate": 0.08,
            "taxAmount": 40.00,
            "discount": 0.00,
            "total": 540.00,
            "status": "DRAFT",
            "notes": "Thank you for your business!"
        }
        ```
    *   **Expected Status (ADMIN/INSTRUCTOR):** `201 Created`
    *   **Sample Response Body (JSON):**
        ```json
        {
            "invoice": {
                "id": 1,
                "invoiceNumber": "INV-2025-001",
                "date": "2025-07-14",
                "dueDate": "2025-08-14",
                "subtotal": 500.00,
                "taxRate": 0.08,
                "taxAmount": 40.00,
                "discount": 0.00,
                "total": 540.00,
                "status": "DRAFT",
                "notes": "Thank you for your business!",
                "user": {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com"
                },
                "items": [
                    {
                        "id": 1,
                        "description": "Course Enrollment: Advanced Java",
                        "quantity": 1,
                        "unitPrice": 500.00,
                        "total": 500.00
                    }
                ]
            },
            "stripeInvoiceUrl": "https://invoice.stripe.com/i/..."
        }
        ```
