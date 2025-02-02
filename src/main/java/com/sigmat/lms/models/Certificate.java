package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long certificateId;
    @ManyToOne
    @JoinColumn(name = "learner_id",referencedColumnName = "learner_id")
    private Learner learner;
    @ManyToOne
    @JoinColumn(name = "course_id",referencedColumnName = "course_id")
    private Course course;
    @ManyToOne
    @JoinColumn(name =  "instructor_id", referencedColumnName = "instructor_id")
    private Instructor instructor;
    private LocalDate dateOfCertificate;
    @Lob
    private byte[] certificate;
}
