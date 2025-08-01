package com.flux.lms.services;

import com.flux.lms.models.Attendance;
import com.flux.lms.models.Users;
import com.flux.lms.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void recordAttendance(Users user) {
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByUser(Users user) {
        return attendanceRepository.findByUser(user);
    }

    public List<Attendance> getAttendanceByUserAndDateRange(Users user, LocalDateTime startDate, LocalDateTime endDate) {
        return attendanceRepository.findByUserAndTimestampBetween(user, startDate, endDate);
    }
}
