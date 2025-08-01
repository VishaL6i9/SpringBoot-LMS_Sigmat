package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstituteDTO {
    private Long instituteId;
    private String instituteName;
    private String instituteCode;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private String website;
    private LocalDateTime establishedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    
    // Admin information
    private Long adminId;
    private String adminName;
    private String adminEmail;
    
    // Statistics
    private int totalStudents;
    private int totalInstructors;
    private int totalCourses;
}