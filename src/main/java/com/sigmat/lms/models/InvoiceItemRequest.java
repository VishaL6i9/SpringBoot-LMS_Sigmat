package com.sigmat.lms.models;

import lombok.Data;

@Data
public class InvoiceItemRequest {
    // Getters and Setters
    private String id;
    private String description;
    private int quantity;
    private double unitPrice;
    private double total;

    
}
