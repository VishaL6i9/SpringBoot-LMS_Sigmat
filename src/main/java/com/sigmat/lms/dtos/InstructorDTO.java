package com.sigmat.lms.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InstructorDTO {
    private Long instructorId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private LocalDate dateOfJoining;
    private String facebookHandle;
    private String linkedinHandle;
    private String youtubeHandle;
}