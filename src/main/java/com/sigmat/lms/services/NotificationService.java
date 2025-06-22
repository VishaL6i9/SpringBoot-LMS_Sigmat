package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.Notification;
import com.sigmat.lms.models.NotificationDTO;
import com.sigmat.lms.models.NotificationStatsDTO;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationDTO createNotification(NotificationDTO dto, Long userId) {
        Users user = userService.getUserById(userId);
        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .timestamp(new Date())
                .isRead(false)
                .category(dto.getCategory())
                .actionUrl(dto.getActionUrl())
                .priority(dto.getPriority())
                .user(user)
                .build();

        return convertToDTO(notificationRepository.save(notification));
    }

    public List<NotificationDTO> sendNotificationToUsers(NotificationDTO dto, List<Long> userIds) {
        return userIds.stream()
                .map(userId -> createNotification(dto, userId))
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);
        unreadNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public NotificationStatsDTO getNotificationStats(Long userId) {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date weekAgo = DateUtils.addDays(today, -7);

        return NotificationStatsDTO.builder()
                .total(notificationRepository.countByUserId(userId))
                .unread(notificationRepository.countByUserIdAndIsReadFalse(userId))
                .today(notificationRepository.countByUserIdAndTimestampGreaterThanEqual(userId, today))
                .thisWeek(notificationRepository.countByUserIdAndTimestampGreaterThanEqual(userId, weekAgo))
                .build();
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .timestamp(notification.getTimestamp())
                .isRead(notification.isRead())
                .category(notification.getCategory())
                .actionUrl(notification.getActionUrl())
                .priority(notification.getPriority())
                .build();
    }
}