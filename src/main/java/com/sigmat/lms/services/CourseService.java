package com.sigmat.lms.services;

import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.repo.CourseRepo;
import com.sigmat.lms.repo.InstructorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepo courseRepo;
    private final InstructorRepo instructorRepo; // Inject InstructorRepo

    @Autowired
    public CourseService(CourseRepo courseRepo, InstructorRepo instructorRepo) {
        this.courseRepo = courseRepo;
        this.instructorRepo = instructorRepo;
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public Optional<Long> getCourseIdByCode(String courseCode) {
        Optional<Course> course = courseRepo.findByCourseCode(courseCode);
        return course.map(Course::getCourseId);
    }
    
    @Transactional // Ensure transactional behavior
    public Course saveCourse(Course course) {
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
        return courseRepo.save(course);
    }

    @Transactional // Ensure transactional behavior
    public Course updateCourse(Long courseId, Course courseDetails) {

        Optional<Course> existingCourseOptional = getCourseById(courseId);
        
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
            
            return courseRepo.save(existingCourse);

        } else {
            return null;
        }

    }
    
    public void deleteCourse(Long courseId) {
        courseRepo.deleteById(courseId);
    }

    public Optional<Course> getCourseById(Long courseId) {
        return courseRepo.findByCourseId(courseId);
    }

}