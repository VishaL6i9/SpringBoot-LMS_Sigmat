package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEventDTO {
    private String sessionId;
    private String subscriptionId;
    private String invoiceId;
    private Long userId;
    private Long planId;
    
    private Integer durationMonths;
    private Double amount;
    private String status;
    private String reason;
}