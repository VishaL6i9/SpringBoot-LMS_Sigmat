package com.sigmat.lms.repository;

import com.sigmat.lms.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseId(Long courseId);
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByInstructors_InstructorId(Long instructorId);

    // @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules m LEFT JOIN FETCH m.lessons l LEFT JOIN FETCH l.articleLesson al WHERE c.courseId = :courseId")
    // Optional<Course> findByCourseIdWithModulesAndLessonsAndArticleContent(@Param("courseId") Long courseId);
}
