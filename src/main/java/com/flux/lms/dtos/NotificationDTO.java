package com.flux.lms.dtos;

import com.flux.lms.models.NotificationCategory;
import com.flux.lms.models.NotificationPriority;
import com.flux.lms.models.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Date timestamp;
    private boolean isRead;
    private NotificationCategory category;
    private String actionUrl;
    private NotificationPriority priority;
}
