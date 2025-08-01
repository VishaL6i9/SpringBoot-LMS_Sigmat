package com.flux.lms.services;

import com.flux.lms.dtos.CourseDTO;
import com.flux.lms.models.Course;
import com.flux.lms.models.Instructor;
import com.flux.lms.repository.CourseRepo;
import com.flux.lms.repository.InstructorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepo courseRepo;
    private final InstructorRepo instructorRepo; // Inject InstructorRepo
    private final CourseAccessControlService courseAccessControlService;

    @Autowired
    public CourseService(CourseRepo courseRepo, InstructorRepo instructorRepo, CourseAccessControlService courseAccessControlService) {
        this.courseRepo = courseRepo;
        this.instructorRepo = instructorRepo;
        this.courseAccessControlService = courseAccessControlService;
    }

    

    public List<CourseDTO> getAllCoursesAsDTOs() {
        return courseRepo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private CourseDTO convertToDTO(Course course) {
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

    public Optional<Long> getCourseIdByCode(String courseCode) {
        Optional<Course> course = courseRepo.findByCourseCode(courseCode);
        return course.map(Course::getCourseId);
    }
    
    @Transactional // Ensure transactional behavior
    public CourseDTO saveCourse(Course course) {
        Set<Instructor> managedInstructors = new HashSet<>();
        if (course.getInstructors() != null) {
            for (Instructor instructor : course.getInstructors()) {
                if (instructor.getInstructorId() != null) {
                    Instructor existingInstructor = instructorRepo.findById(instructor.getInstructorId())
                            .orElseThrow(() -> new RuntimeException("Instructor not found with ID: " + instructor.getInstructorId()));
                    managedInstructors.add(existingInstructor);
                } else {
                    // Handle case where instructor is new or not fully provided (e.g., throw error or save new instructor)
                    throw new RuntimeException("Instructor ID must be provided for existing instructors.");
                }
            }
        }
        course.setInstructors(managedInstructors);
        return convertToDTO(courseRepo.save(course));
    }

    @Transactional // Ensure transactional behavior
    public CourseDTO updateCourse(Long courseId, Course courseDetails) {

        Optional<Course> existingCourseOptional = courseRepo.findByCourseId(courseId);
        
        if (existingCourseOptional.isPresent()) {

            Course existingCourse = existingCourseOptional.get();

            existingCourse.setCourseName(courseDetails.getCourseName());
            existingCourse.setCourseCode(courseDetails.getCourseCode());
            existingCourse.setCourseDescription(courseDetails.getCourseDescription());
            existingCourse.setCourseDuration(courseDetails.getCourseDuration());
            existingCourse.setCourseMode(courseDetails.getCourseMode());
            existingCourse.setMaxEnrollments(courseDetails.getMaxEnrollments());
            existingCourse.setCourseFee(courseDetails.getCourseFee());
            existingCourse.setLanguage(courseDetails.getLanguage());
            existingCourse.setCourseCategory(courseDetails.getCourseCategory());
            
            // Handle instructors for update: fetch managed entities
            Set<Instructor> managedInstructors = new HashSet<>();
            if (courseDetails.getInstructors() != null) {
                for (Instructor instructor : courseDetails.getInstructors()) {
                    if (instructor.getInstructorId() != null) {
                        Instructor existingInstructor = instructorRepo.findById(instructor.getInstructorId())
                                .orElseThrow(() -> new RuntimeException("Instructor not found with ID: " + instructor.getInstructorId()));
                        managedInstructors.add(existingInstructor);
                    } else {
                        throw new RuntimeException("Instructor ID must be provided for existing instructors during update.");
                    }
                }
            }
            existingCourse.setInstructors(managedInstructors);
            
            return convertToDTO(courseRepo.save(existingCourse));

        } else {
            return null;
        }

    }
    
    public void deleteCourse(Long courseId) {
        courseRepo.deleteById(courseId);
    }

    public Optional<CourseDTO> getCourseById(Long courseId) {
        return courseRepo.findByCourseId(courseId).map(this::convertToDTO);
    }

    public List<CourseDTO> getCoursesByUserId(Long userId) {
        Optional<Instructor> instructorOptional = instructorRepo.findByUser_Id(userId);
        if (instructorOptional.isPresent()) {
            return courseRepo.findByInstructors_InstructorId(instructorOptional.get().getInstructorId()).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return List.of(); // Return an empty list if no instructor found for the user ID
        }
    }

    /**
     * Get courses accessible to a specific user based on institute access control
     */
    public List<CourseDTO> getAccessibleCoursesByUserId(Long userId) {
        return courseAccessControlService.getAccessibleCourses(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if a user can access a specific course
     */
    public boolean canUserAccessCourse(Long userId, Long courseId) {
        return courseAccessControlService.canUserAccessCourse(userId, courseId);
    }

}