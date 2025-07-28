package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.CourseDTO;
import com.sigmat.lms.dtos.CourseSubscriptionRequestDTO;
import com.sigmat.lms.dtos.InstituteSubscriptionDTO;
import com.sigmat.lms.models.Course;
import com.sigmat.lms.services.CourseAccessControlService;
import com.sigmat.lms.services.InstituteSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course-access")
@RequiredArgsConstructor
public class CourseAccessController {

    private final CourseAccessControlService courseAccessControlService;
    private final InstituteSubscriptionService instituteSubscriptionService;

    @GetMapping("/user/{userId}/courses")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN') or authentication.principal.id == #userId")
    public ResponseEntity<List<CourseDTO>> getUserAccessibleCourses(@PathVariable Long userId) {
        List<Course> courses = courseAccessControlService.getAccessibleCourses(userId);
        List<CourseDTO> courseDTOs = courses.stream()
                .map(this::convertCourseToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }

    @GetMapping("/user/{userId}/course/{courseId}/access")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN') or authentication.principal.id == #userId")
    public ResponseEntity<Boolean> checkUserCourseAccess(@PathVariable Long userId, @PathVariable Long courseId) {
        boolean hasAccess = courseAccessControlService.canUserAccessCourse(userId, courseId);
        return ResponseEntity.ok(hasAccess);
    }

    @PostMapping("/institute/subscribe")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #request.instituteId)")
    public ResponseEntity<InstituteSubscriptionDTO> subscribeInstituteToGlobalCourse(@RequestBody CourseSubscriptionRequestDTO request) {
        InstituteSubscriptionDTO subscription = instituteSubscriptionService.subscribeToGlobalCourse(request);
        return new ResponseEntity<>(subscription, HttpStatus.CREATED);
    }

    @DeleteMapping("/institute/{instituteId}/course/{courseId}/unsubscribe")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #instituteId)")
    public ResponseEntity<Void> unsubscribeInstituteFromGlobalCourse(@PathVariable Long instituteId, @PathVariable Long courseId) {
        instituteSubscriptionService.unsubscribeFromGlobalCourse(instituteId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/institute/{instituteId}/subscriptions")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<List<InstituteSubscriptionDTO>> getInstituteSubscriptions(@PathVariable Long instituteId) {
        List<InstituteSubscriptionDTO> subscriptions = instituteSubscriptionService.getInstituteSubscriptions(instituteId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/course/{courseId}/subscriptions")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<InstituteSubscriptionDTO>> getCourseSubscriptions(@PathVariable Long courseId) {
        List<InstituteSubscriptionDTO> subscriptions = instituteSubscriptionService.getCourseSubscriptions(courseId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/institute/{instituteId}/subscribed-courses")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<List<CourseDTO>> getInstituteSubscribedCourses(@PathVariable Long instituteId) {
        List<Course> courses = courseAccessControlService.getInstituteSubscribedCourses(instituteId);
        List<CourseDTO> courseDTOs = courses.stream()
                .map(this::convertCourseToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }

    @PutMapping("/subscription/{subscriptionId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION')")
    public ResponseEntity<InstituteSubscriptionDTO> updateSubscription(@PathVariable Long subscriptionId, @RequestBody InstituteSubscriptionDTO dto) {
        InstituteSubscriptionDTO updatedSubscription = instituteSubscriptionService.updateSubscription(subscriptionId, dto);
        return ResponseEntity.ok(updatedSubscription);
    }

    @PostMapping("/maintenance/deactivate-expired")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deactivateExpiredSubscriptions() {
        instituteSubscriptionService.deactivateExpiredSubscriptions();
        return ResponseEntity.ok().build();
    }

    private CourseDTO convertCourseToDTO(Course course) {
        return new CourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCourseDescription(),
                course.getCourseDuration(),
                course.getCourseMode(),
                course.getMaxEnrollments(),
                course.getCourseFee(),
                course.getLanguage(),
                course.getCourseCategory(),
                course.getCourseScope() != null ? course.getCourseScope().name() : "INSTITUTE_ONLY",
                course.getInstitute() != null ? course.getInstitute().getInstituteId() : null,
                course.getInstitute() != null ? course.getInstitute().getInstituteName() : null
        );
    }
}