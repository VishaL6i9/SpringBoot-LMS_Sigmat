package com.sigmat.lms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor  
@NoArgsConstructor 
public class CertificateDTO {
    private Long certificateId;
    private Long learnerId;
    private String learnerFirstName;
    private Long courseId;
    private String courseName;
    private Long instructorId;
    private String instructorFirstName;
    private LocalDate dateOfCertificate;

    // No certificate bytes in DTO
}