package com.sigmat.lms.repository;

import com.sigmat.lms.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByTimestampDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);
    long countByUserIdAndTimestampGreaterThanEqual(Long userId, Date date);

    long countByUserId(Long userId);
}
