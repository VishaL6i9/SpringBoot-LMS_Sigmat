package com.flux.lms.dtos;

import com.flux.lms.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionalUserDTO {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
    
    // Institutional specific attributes
    private String rollNumber;        // For students
    private String admissionId;       // For students
    private String staffId;           // For staff/instructors
    private String employeeId;        // For industry L&D
    private String department;        // For industry L&D
    private String jobRole;           // For industry L&D
    
    // Contact details
    private String phoneNumber;
    private String parentContact;     // For educational institutions
    private String emergencyContact;
    
    // Institute relationship
    private Long instituteId;
    private String instituteName;
    
    // Access control
    private boolean isActive;
    private boolean isVerified;
    private LocalDateTime enrollmentDate;
    private LocalDateTime lastLoginDate;
    
    // Academic/Professional details
    private String batch;             // For students
    private String semester;          // For students
    private String grade;             // For students
    private String division;          // For students
    private String courseOfStudy;     // For students
    
    // Parent/Manager information
    private Long parentId;            // For educational institutions
    private String parentName;
    private String parentEmail;
    private Long managerId;           // For industry L&D
    private String managerName;
    private String managerEmail;
    
    // Subscription and access
    private String subscriptionType;
    private LocalDateTime subscriptionExpiry;
    private boolean hasActiveSubscription;
}