package com.flux.lms.controllers;

import com.flux.lms.dtos.AttendanceDTO;
import com.flux.lms.models.Attendance;
import com.flux.lms.models.Users;
import com.flux.lms.services.AttendanceService;
import com.flux.lms.services.JwtService;
import com.flux.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getUserAttendance(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String username = jwtService.extractUserName(jwt);
            Users user = userService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<Attendance> attendanceRecords = attendanceService.getAttendanceByUser(user);
            List<AttendanceDTO> attendanceDTOs = attendanceRecords.stream()
                    .map(attendance -> new AttendanceDTO(attendance.getId(), attendance.getUser().getId(), attendance.getTimestamp()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(attendanceDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/range")
    public ResponseEntity<List<AttendanceDTO>> getUserAttendanceByRange(
            @RequestHeader("Authorization") String token,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        try {
            String jwt = token.substring(7);
            String username = jwtService.extractUserName(jwt);
            Users user = userService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            LocalDateTime startDate = LocalDateTime.parse(startDateStr, FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, FORMATTER);

            List<Attendance> attendanceRecords = attendanceService.getAttendanceByUserAndDateRange(user, startDate, endDate);
            List<AttendanceDTO> attendanceDTOs = attendanceRecords.stream()
                    .map(attendance -> new AttendanceDTO(attendance.getId(), attendance.getUser().getId(), attendance.getTimestamp()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(attendanceDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
