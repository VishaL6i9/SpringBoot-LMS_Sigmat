package com.flux.lms.services;

import com.flux.lms.exceptions.ResourceNotFoundException;
import com.flux.lms.models.*;
import com.flux.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseAccessControlService {
    
    private final CourseRepo courseRepository;
    private final UserRepo userRepository;
    private final InstituteRepository instituteRepository;
    private final InstituteSubscriptionRepository instituteSubscriptionRepository;
    private final EnrollmentRepo enrollmentRepository;
    private final InstructorRepo instructorRepository;

    /**
     * Check if a user can access a specific course
     */
    public boolean canUserAccessCourse(Long userId, Long courseId) {
        Users user = getUserById(userId);
        Course course = getCourseById(courseId);
        
        return canUserAccessCourse(user, course);
    }

    /**
     * Check if a user can access a specific course
     */
    public boolean canUserAccessCourse(Users user, Course course) {
        // Super admins can access all courses
        if (user.getRoles().contains(Role.SUPER_ADMIN)) {
            return true;
        }
        
        switch (course.getCourseScope()) {
            case GLOBAL:
                return canAccessGlobalCourse(user, course);
            case INSTITUTE_ONLY:
                return canAccessInstituteCourse(user, course);
            case RESTRICTED:
                return canAccessRestrictedCourse(user, course);
            default:
                return false;
        }
    }

    /**
     * Get all courses accessible to a user
     */
    public List<Course> getAccessibleCourses(Long userId) {
        Users user = getUserById(userId);
        return getAccessibleCourses(user);
    }

    /**
     * Get all courses accessible to a user
     */
    public List<Course> getAccessibleCourses(Users user) {
        List<Course> allCourses = courseRepository.findAll();
        
        return allCourses.stream()
                .filter(course -> canUserAccessCourse(user, course))
                .collect(Collectors.toList());
    }

    /**
     * Subscribe an institute to a global course
     */
    @Transactional
    public InstituteSubscription subscribeInstituteToGlobalCourse(Long instituteId, Long courseId, 
                                                                 boolean autoEnrollStudents, 
                                                                 boolean autoEnrollInstructors) {
        Institute institute = getInstituteById(instituteId);
        Course course = getCourseById(courseId);
        
        // Validate course is global
        if (course.getCourseScope() != CourseScope.GLOBAL) {
            throw new IllegalArgumentException("Only global courses can be subscribed by institutes");
        }
        
        // Check if already subscribed
        if (instituteSubscriptionRepository.existsByInstituteAndCourseAndIsActiveTrue(institute, course)) {
            throw new IllegalArgumentException("Institute is already subscribed to this course");
        }
        
        // Create subscription
        InstituteSubscription subscription = InstituteSubscription.builder()
                .institute(institute)
                .course(course)
                .subscriptionPrice(course.getCourseFee() != null ? 
                    java.math.BigDecimal.valueOf(course.getCourseFee()) : java.math.BigDecimal.ZERO)
                .finalAmount(course.getCourseFee() != null ? 
                    java.math.BigDecimal.valueOf(course.getCourseFee()) : java.math.BigDecimal.ZERO)
                .autoEnrollStudents(autoEnrollStudents)
                .autoEnrollInstructors(autoEnrollInstructors)
                .isActive(true)
                .build();
        
        InstituteSubscription savedSubscription = instituteSubscriptionRepository.save(subscription);
        
        // Auto-enroll users if requested
        if (autoEnrollStudents) {
            autoEnrollInstituteStudents(institute, course);
        }
        
        if (autoEnrollInstructors) {
            autoEnrollInstituteInstructors(institute, course);
        }
        
        return savedSubscription;
    }

    /**
     * Unsubscribe an institute from a global course
     */
    @Transactional
    public void unsubscribeInstituteFromGlobalCourse(Long instituteId, Long courseId) {
        Institute institute = getInstituteById(instituteId);
        Course course = getCourseById(courseId);
        
        InstituteSubscription subscription = instituteSubscriptionRepository
                .findActiveByInstituteAndCourse(institute, course)
                .orElseThrow(() -> new ResourceNotFoundException("Active subscription not found"));
        
        subscription.setActive(false);
        instituteSubscriptionRepository.save(subscription);
    }

    /**
     * Get all courses subscribed by an institute
     */
    public List<Course> getInstituteSubscribedCourses(Long instituteId) {
        return instituteSubscriptionRepository.findActiveByInstituteId(instituteId)
                .stream()
                .map(InstituteSubscription::getCourse)
                .collect(Collectors.toList());
    }

    /**
     * Auto-enroll all students of an institute to a course
     */
    @Transactional
    public void autoEnrollInstituteStudents(Institute institute, Course course) {
        List<Users> students = userRepository.findByInstituteAndRole(institute, Role.USER);
        
        for (Users student : students) {
            // Check if already enrolled
            if (!enrollmentRepository.existsByUserAndCourse(student, course)) {
                Enrollment enrollment = Enrollment.builder()
                        .user(student)
                        .course(course)
                        .enrollmentDate(java.time.LocalDate.now())
                        .build();
                enrollmentRepository.save(enrollment);
            }
        }
    }

    /**
     * Auto-enroll all instructors of an institute to a course
     */
    @Transactional
    public void autoEnrollInstituteInstructors(Institute institute, Course course) {
        List<Instructor> instructors = instructorRepository.findByInstitute(institute);
        
        for (Instructor instructor : instructors) {
            if (instructor.getUser() != null) {
                // Check if already enrolled
                if (!enrollmentRepository.existsByUserAndCourse(instructor.getUser(), course)) {
                    Enrollment enrollment = Enrollment.builder()
                            .user(instructor.getUser())
                            .course(course)
                            .instructor(instructor)
                            .enrollmentDate(java.time.LocalDate.now())
                            .build();
                    enrollmentRepository.save(enrollment);
                }
            }
        }
    }

    /**
     * Check access to global course
     */
    private boolean canAccessGlobalCourse(Users user, Course course) {
        // If user has no institute, they can access global courses directly
        if (user.getInstitute() == null) {
            return true;
        }
        
        // Check if user's institute has subscribed to this global course
        return instituteSubscriptionRepository
                .existsByInstituteAndCourseAndIsActiveTrue(user.getInstitute(), course);
    }

    /**
     * Check access to institute-only course
     */
    private boolean canAccessInstituteCourse(Users user, Course course) {
        // User must belong to the same institute as the course
        if (user.getInstitute() == null || course.getInstitute() == null) {
            return false;
        }
        
        return user.getInstitute().getInstituteId().equals(course.getInstitute().getInstituteId());
    }

    /**
     * Check access to restricted course
     */
    private boolean canAccessRestrictedCourse(Users user, Course course) {
        // For now, restricted courses follow the same rules as institute courses
        // This can be extended with more complex logic later
        return canAccessInstituteCourse(user, course);
    }

    /**
     * Deactivate expired subscriptions
     */
    @Transactional
    public void deactivateExpiredSubscriptions() {
        List<InstituteSubscription> expiredSubscriptions = 
                instituteSubscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
        
        for (InstituteSubscription subscription : expiredSubscriptions) {
            subscription.setActive(false);
            instituteSubscriptionRepository.save(subscription);
        }
    }

    // Helper methods
    private Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private Institute getInstituteById(Long id) {
        return instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));
    }
}