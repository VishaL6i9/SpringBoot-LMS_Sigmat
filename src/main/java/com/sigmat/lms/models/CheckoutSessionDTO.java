package com.sigmat.lms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutSessionDTO {
    
    private String tier;
    private String successUrl;
    private String cancelUrl;

}