package com.sigmat.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRegistrationDTO {
    private Long batchId;
    private String batchName;
    private String batchCode;
    private String description;
    
    // Institute relationship
    private Long instituteId;
    private String instituteName;
    
    // Instructor assignment
    private Long instructorId;
    private String instructorName;
    private String instructorEmail;
    
    // Course assignment
    private Long courseId;
    private String courseName;
    
    // Batch details
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxStudents;
    private Integer currentStudents;
    private String semester;
    private String academicYear;
    
    // Students in batch
    private List<Long> studentIds;
    private List<InstitutionalUserDTO> students;
    
    // Status
    private boolean isActive;
    private String status; // PLANNED, ACTIVE, COMPLETED, CANCELLED
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}