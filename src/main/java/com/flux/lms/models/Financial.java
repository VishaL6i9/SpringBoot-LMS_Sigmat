package com.flux.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Financial {
    @Id
    @Column(name = "transaction_id")
    private int transactionID;
    @ManyToOne
    @JoinColumn(name = "learner_id",referencedColumnName = "learner_id")
    private Learner learner;
    private Long amountPaid;
    private Date txnDate;
    private String paymentMethod;
    private String status;
}
