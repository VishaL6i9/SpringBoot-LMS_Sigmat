package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int certificateId;
    
    @ManyToOne
    @JoinColumn(name = "learner_id",referencedColumnName = "learner_id")
    private Learner learner;
    @ManyToOne
    @JoinColumn(name = "course_id",referencedColumnName = "course_id")
    private Course course;
    private Date dateOfCertificate;
}
