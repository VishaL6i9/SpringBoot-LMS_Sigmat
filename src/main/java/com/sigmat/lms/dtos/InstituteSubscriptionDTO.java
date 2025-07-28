package com.sigmat.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstituteSubscriptionDTO {
    private Long id;
    private Long instituteId;
    private String instituteName;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private BigDecimal subscriptionPrice;
    private BigDecimal discountApplied;
    private BigDecimal finalAmount;
    private LocalDateTime subscriptionDate;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private boolean autoEnrollStudents;
    private boolean autoEnrollInstructors;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}