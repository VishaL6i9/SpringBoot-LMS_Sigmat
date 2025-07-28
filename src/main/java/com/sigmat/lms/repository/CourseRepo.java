package com.sigmat.lms.repository;

import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseId(Long courseId);
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByInstructors_InstructorId(Long instructorId);

    // Institute-based queries
    List<Course> findByInstitute(Institute institute);
    
    @Query("SELECT c FROM Course c WHERE c.institute.instituteId = :instituteId")
    List<Course> findByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.institute = :institute")
    long countByInstitute(@Param("institute") Institute institute);
    
    @Query("SELECT c FROM Course c WHERE c.institute = :institute AND c.courseCategory = :category")
    List<Course> findByInstituteAndCategory(@Param("institute") Institute institute, @Param("category") String category);

    // @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules m LEFT JOIN FETCH m.lessons l LEFT JOIN FETCH l.articleLesson al WHERE c.courseId = :courseId")
    // Optional<Course> findByCourseIdWithModulesAndLessonsAndArticleContent(@Param("courseId") Long courseId);
}
