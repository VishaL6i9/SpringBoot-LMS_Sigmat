package com.sigmat.lms.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollmentDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long courseId;
    private String courseName;
    private Long instructorId;
    private String instructorName;
    private LocalDate enrollmentDate;
}
