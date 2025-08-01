package com.flux.lms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "instructor_profile")
public class InstructorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private String address;
    private String timezone;
    private String language;
    private String bio;
    private String specialization;
    private LocalDate dateOfJoining;

    // Banking information
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private String accountHolderName;

    // Social media handles
    private String facebookHandle;
    private String linkedinHandle;
    private String youtubeHandle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    private String password;
}