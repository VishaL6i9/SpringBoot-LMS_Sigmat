package com.flux.lms.repository;

import com.flux.lms.models.Attendance;
import com.flux.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(Users user);
    List<Attendance> findByUserAndTimestampBetween(Users user, LocalDateTime startDate, LocalDateTime endDate);
}
