package com.sigmat.lms.repo;

import com.sigmat.lms.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseId(Long courseId);
    Optional<Course> findByCourseCode(String courseCode);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules m WHERE c.courseId = :courseId")
    Optional<Course> findByCourseIdWithModulesAndLessonsAndArticleContent(@Param("courseId") Long courseId);
}
