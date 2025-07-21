package com.sigmat.lms.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutSessionDTO {
    
    private String tier;
    private String successUrl;
    private String cancelUrl;
    private Long userId;
    private Long courseId;
    private Long instructorId;

}