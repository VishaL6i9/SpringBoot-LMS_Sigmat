package com.sigmat.lms.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutSessionDTO {
    
    // Legacy fields for backward compatibility
    private String tier;
    
    // New subscription fields
    private Long planId;
    private Integer durationMonths;
    private Boolean autoRenew = true;
    
    // Common fields
    private String successUrl;
    private String cancelUrl;
    private Long userId;
    
    private Long instructorId;

}