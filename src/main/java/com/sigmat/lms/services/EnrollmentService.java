package com.sigmat.lms.services;

import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.Enrollment;
import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.CourseRepo;
import com.sigmat.lms.repo.EnrollmentRepo;
import com.sigmat.lms.repo.InstructorRepo;
import com.sigmat.lms.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepo enrollmentRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final InstructorRepo instructorRepo;

    @Autowired
    public EnrollmentService(EnrollmentRepo enrollmentRepo, UserRepo userRepo, CourseRepo courseRepo, InstructorRepo instructorRepo) {
        this.enrollmentRepo = enrollmentRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.instructorRepo = instructorRepo;
    }

    public Enrollment enrollUserInCourse(Long userId, Long courseId, Long instructorId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if user is already enrolled in this course
        if (enrollmentRepo.findByUser_IdAndCourse_CourseId(userId, courseId).isPresent()) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        Instructor instructor = null;
        if (instructorId != null) {
            instructor = instructorRepo.findById(instructorId)
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            // Verify if the instructor is actually associated with the course
            if (!course.getInstructors().contains(instructor)) {
                throw new RuntimeException("Instructor is not associated with this course");
            }
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .instructor(instructor)
                .enrollmentDate(LocalDate.now())
                .build();

        return enrollmentRepo.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByUserId(Long userId) {
        return enrollmentRepo.findByUser_Id(userId);
    }

    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepo.findByCourse_CourseId(courseId);
    }

    public Optional<Enrollment> getEnrollmentById(Long enrollmentId) {
        return enrollmentRepo.findById(enrollmentId);
    }

    public void deleteEnrollment(Long enrollmentId) {
        enrollmentRepo.deleteById(enrollmentId);
    }

    @Transactional
    public void deleteEnrollmentsByInstructorId(Long instructorId) {
        enrollmentRepo.deleteByInstructor_InstructorId(instructorId);
    }
}