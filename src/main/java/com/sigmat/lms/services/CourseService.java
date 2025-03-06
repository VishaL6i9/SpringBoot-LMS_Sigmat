// CourseService.java
package com.sigmat.lms.services;

import com.sigmat.lms.models.Course;
import com.sigmat.lms.repo.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepo courseRepo;

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public Optional<Long> getCourseIdByCode(String courseCode) {
        Optional<Course> course = courseRepo.findByCourseCode(courseCode);
        return course.map(Course::getCourseId);
    }
    
    public Course saveCourse(Course course) {
        return courseRepo.save(course);
    }

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
            
            return saveCourse(existingCourse);

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