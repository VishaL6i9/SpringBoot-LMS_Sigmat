package com.flux.lms.dtos;

import com.flux.lms.models.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {
    private Long id;
    private Long userId;
    private String username;
    private SubscriptionPlanDTO subscriptionPlan;
    private Long courseId;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean autoRenew;
    private BigDecimal actualPrice;
    private BigDecimal discountApplied;
    private String paymentReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}