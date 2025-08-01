package com.flux.lms.controllers;

import com.flux.lms.dtos.CourseDTO;
import com.flux.lms.models.Course;
import com.flux.lms.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody Course course) {
        CourseDTO savedCourse = courseService.saveCourse(course);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId, @RequestBody Course courseDetails) {
        CourseDTO updatedCourse = courseService.updateCourse(courseId, courseDetails);

        if (updatedCourse != null) {
            return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCoursesAsDTOs();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        Optional<CourseDTO> course = courseService.getCourseById(courseId);
        return course.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{courseCode}/id")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<Long> getCourseIdByName(@PathVariable String courseCode) {
        Optional<Long> courseId = courseService.getCourseIdByCode(courseCode);
        return courseId.map(id -> ResponseEntity.ok(id))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<List<CourseDTO>> getCoursesByUserId(@PathVariable Long userId) {
        List<CourseDTO> courses = courseService.getCoursesByUserId(userId);
        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(courses);
        }
    }

    @GetMapping("/user/{userId}/accessible")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'USER') and (hasRole('SUPER_ADMIN') or authentication.principal.id == #userId)")
    public ResponseEntity<List<CourseDTO>> getAccessibleCoursesByUserId(@PathVariable Long userId) {
        List<CourseDTO> courses = courseService.getAccessibleCoursesByUserId(userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/user/{userId}/course/{courseId}/access")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'USER') and (hasRole('SUPER_ADMIN') or authentication.principal.id == #userId)")
    public ResponseEntity<Boolean> checkUserCourseAccess(@PathVariable Long userId, @PathVariable Long courseId) {
        boolean hasAccess = courseService.canUserAccessCourse(userId, courseId);
        return ResponseEntity.ok(hasAccess);
    }
}