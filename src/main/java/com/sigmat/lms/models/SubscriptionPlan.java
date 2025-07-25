package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlanType planType;

    @Column(name = "learner_tier")
    @Enumerated(EnumType.STRING)
    private LearnerPlanTier learnerTier;

    @Column(name = "faculty_tier")
    @Enumerated(EnumType.STRING)
    private FacultyPlanTier facultyTier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceInr;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;

    @Column(name = "best_suited_for")
    private String bestSuitedFor;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "minimum_duration_months")
    private Integer minimumDurationMonths = 3;

    @Column(name = "is_custom_pricing")
    private boolean isCustomPricing = false;
}