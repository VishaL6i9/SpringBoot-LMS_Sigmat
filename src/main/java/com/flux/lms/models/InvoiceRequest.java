package com.flux.lms.models;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {
    private String invoiceNumber;
    private String date;
    private String dueDate;
    private UserRequest user;
    private List<InvoiceItemRequest> items;
    private double subtotal;
    private double taxRate;
    private double taxAmount;
    private double discount;
    private double total;
    private String status;
    private String notes;
    
}
