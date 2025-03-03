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

    public Course saveCourse(Course course) {
        return courseRepo.save(course);
    }

    public void deleteCourse(Long courseId) {
        courseRepo.deleteById(courseId);
    }

    public Optional<Course> getCourseById(Long courseId) {
        return courseRepo.findByCourseId(courseId);
    }

}