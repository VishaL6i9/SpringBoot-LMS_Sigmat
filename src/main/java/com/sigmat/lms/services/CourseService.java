package com.sigmat.lms.services;

import com.sigmat.lms.dtos.CourseDTO;
import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.repository.CourseRepo;
import com.sigmat.lms.repository.InstructorRepo;
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

    @Autowired
    public CourseService(CourseRepo courseRepo, InstructorRepo instructorRepo) {
        this.courseRepo = courseRepo;
        this.instructorRepo = instructorRepo;
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
                course.getCourseCategory()
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

}