package com.flux.lms.config;

import com.flux.lms.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final SubscriptionService subscriptionService;

    // Run every day at 2 AM to check for expired subscriptions
    @Scheduled(cron = "0 0 2 * * ?")
    public void expireSubscriptions() {
        log.info("Running scheduled task to expire subscriptions");
        try {
            subscriptionService.expireSubscriptions();
            log.info("Successfully processed expired subscriptions");
        } catch (Exception e) {
            log.error("Error processing expired subscriptions", e);
        }
    }
}