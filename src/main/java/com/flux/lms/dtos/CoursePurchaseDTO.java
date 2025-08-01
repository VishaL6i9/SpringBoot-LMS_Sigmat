package com.flux.lms.dtos;

import com.flux.lms.models.PurchaseStatus;
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
public class CoursePurchaseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private BigDecimal purchasePrice;
    private BigDecimal discountApplied;
    private BigDecimal finalAmount;
    private PurchaseStatus status;
    private String paymentReference;
    private String stripeSessionId;
    private LocalDateTime purchaseDate;
    private LocalDateTime accessGrantedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}