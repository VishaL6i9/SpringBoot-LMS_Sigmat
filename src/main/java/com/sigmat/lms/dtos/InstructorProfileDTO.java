package com.sigmat.lms.dtos;

import com.sigmat.lms.models.ProfileImage;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InstructorProfileDTO {
    private Long id;
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
    
    private ProfileImage profileImage;
    private InstructorDTO instructor;
}