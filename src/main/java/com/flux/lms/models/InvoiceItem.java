package com.flux.lms.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "invoice_items")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private int quantity;
    private double unitPrice;
    private double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Invoice invoice;
    
}
