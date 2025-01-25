package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Entity
@Getter
@Setter
public class Learner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learner_id", unique = true, nullable = false)
    private int learnerID;
    private String firstName;
    private String lastName;
    private String email;
    private int phoneNo;
    private Date joinDate;
    
}
