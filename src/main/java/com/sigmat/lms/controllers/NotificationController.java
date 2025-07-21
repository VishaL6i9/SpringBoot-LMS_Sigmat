package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.NotificationDTO;
import com.sigmat.lms.dtos.NotificationStatsDTO;
import com.sigmat.lms.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestBody NotificationDTO notification,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(notificationService.createNotification(notification, userId));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<NotificationDTO>> sendNotificationToUsers(
            @RequestBody NotificationDTO notification,
            @RequestParam List<Long> userIds
    ) {
        return ResponseEntity.ok(notificationService.sendNotificationToUsers(notification, userIds));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("@notificationService.getNotificationById(#id).user.id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/read-all")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/stats")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<NotificationStatsDTO> getNotificationStats(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationStats(userId));
    }
}