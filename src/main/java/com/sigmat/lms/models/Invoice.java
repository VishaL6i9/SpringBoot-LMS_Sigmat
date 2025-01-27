package com.sigmat.lms.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Invoice {
    @Id
    private int invoiceId;
    
    @ManyToOne
    @JoinColumn(name = "transaction_id",referencedColumnName = "transaction_id")
    private Financial transaction;
    
    @ManyToOne
    @JoinColumn(name = "learner_id",referencedColumnName = "learner_id")
    private Learner learner;
    
    private Date invoiceDate;
    private Long invoiceAmount;
    private Long invoiceTaxAmount;
}
