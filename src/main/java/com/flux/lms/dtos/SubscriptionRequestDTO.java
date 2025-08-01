package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {
    private Long userId;
    private Long planId;
    private Long courseId;
    private boolean autoRenew;
    private BigDecimal discountApplied;
    private String paymentReference;
    private Integer durationMonths;
}