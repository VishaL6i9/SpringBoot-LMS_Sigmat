package com.sigmat.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    
    // User information (instead of full User object)
    private Long userId;
    private String username;
    private String userEmail;
    
    // Institute information (instead of full Institute object)
    private Long instituteId;
    private String instituteName;
}