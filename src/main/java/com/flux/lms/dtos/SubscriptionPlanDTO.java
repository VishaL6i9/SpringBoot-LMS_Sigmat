package com.flux.lms.dtos;

import com.flux.lms.models.FacultyPlanTier;
import com.flux.lms.models.LearnerPlanTier;
import com.flux.lms.models.SubscriptionPlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private SubscriptionPlanType planType;
    private LearnerPlanTier learnerTier;
    private FacultyPlanTier facultyTier;
    private BigDecimal priceInr;
    private String description;
    private List<String> features;
    private String bestSuitedFor;
    private boolean isActive;
    private Integer minimumDurationMonths;
    private boolean isCustomPricing;
}