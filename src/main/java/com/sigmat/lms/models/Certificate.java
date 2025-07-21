package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private UserProfile userProfile;
    @ManyToOne
    @JoinColumn(name = "courseId",referencedColumnName = "courseId")
    private Course course;
    @ManyToOne
    @JoinColumn(name =  "instructor_id", referencedColumnName = "instructor_id")
    private Instructor instructor;
    private LocalDate dateOfCertificate;
    @Lob
    private byte[] certificate;
}
