package com.flux.lms.controllers;

import com.flux.lms.dtos.CourseDTO;
import com.flux.lms.dtos.InstructorDTO;
import com.flux.lms.dtos.UserDTO;
import com.flux.lms.services.InstituteManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutes/{instituteId}/management")
@RequiredArgsConstructor
public class InstituteManagementController {

    private final InstituteManagementService instituteManagementService;

    // Student Management
    @PostMapping("/students/{studentId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> addStudentToInstitute(@PathVariable Long instituteId, @PathVariable Long studentId) {
        instituteManagementService.addStudentToInstitute(instituteId, studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/students/{studentId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> removeStudentFromInstitute(@PathVariable Long studentId) {
        instituteManagementService.removeStudentFromInstitute(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getInstituteStudents(@PathVariable Long instituteId) {
        List<UserDTO> students = instituteManagementService.getInstituteStudents(instituteId);
        return ResponseEntity.ok(students);
    }

    // Instructor Management
    @PostMapping("/instructors/{instructorId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> addInstructorToInstitute(@PathVariable Long instituteId, @PathVariable Long instructorId) {
        instituteManagementService.addInstructorToInstitute(instituteId, instructorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/instructors/{instructorId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> removeInstructorFromInstitute(@PathVariable Long instructorId) {
        instituteManagementService.removeInstructorFromInstitute(instructorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/instructors")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<List<InstructorDTO>> getInstituteInstructors(@PathVariable Long instituteId) {
        List<InstructorDTO> instructors = instituteManagementService.getInstituteInstructors(instituteId);
        return ResponseEntity.ok(instructors);
    }

    // Course Management
    @PostMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> addCourseToInstitute(@PathVariable Long instituteId, @PathVariable Long courseId) {
        instituteManagementService.addCourseToInstitute(instituteId, courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> removeCourseFromInstitute(@PathVariable Long courseId) {
        instituteManagementService.removeCourseFromInstitute(courseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<CourseDTO>> getInstituteCourses(@PathVariable Long instituteId) {
        List<CourseDTO> courses = instituteManagementService.getInstituteCourses(instituteId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/category/{category}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<CourseDTO>> getInstituteCoursesByCategory(@PathVariable Long instituteId, @PathVariable String category) {
        List<CourseDTO> courses = instituteManagementService.getInstituteCoursesByCategory(instituteId, category);
        return ResponseEntity.ok(courses);
    }

    // Admin Management
    @PutMapping("/admin/{newAdminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> changeInstituteAdmin(@PathVariable Long instituteId, @PathVariable Long newAdminId) {
        instituteManagementService.changeInstituteAdmin(instituteId, newAdminId);
        return ResponseEntity.ok().build();
    }

    // Statistics
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<InstituteManagementService.InstituteStatistics> getInstituteStatistics(@PathVariable Long instituteId) {
        InstituteManagementService.InstituteStatistics stats = instituteManagementService.getInstituteStatistics(instituteId);
        return ResponseEntity.ok(stats);
    }
}