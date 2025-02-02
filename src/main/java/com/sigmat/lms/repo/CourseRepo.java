package com.sigmat.lms.repo;

import com.sigmat.lms.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseId(Long courseId);
    Optional<Course> findByCourseName(String courseName);
    
}
