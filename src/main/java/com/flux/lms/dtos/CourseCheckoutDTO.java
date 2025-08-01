package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCheckoutDTO {
    private Long courseId;
    private Long userId;
    private String successUrl;
    private String cancelUrl;
    private BigDecimal discountApplied;
    private String couponCode;
}