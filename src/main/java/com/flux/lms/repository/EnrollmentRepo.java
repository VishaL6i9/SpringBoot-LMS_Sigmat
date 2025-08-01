package com.flux.lms.repository;

import com.flux.lms.models.Course;
import com.flux.lms.models.Enrollment;
import com.flux.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser_Id(Long userId);
    List<Enrollment> findByCourse_CourseId(Long courseId);
    Optional<Enrollment> findByUser_IdAndCourse_CourseId(Long userId, Long courseId);
    Optional<Enrollment> findByUserAndCourse(Users user, Course course);
    boolean existsByUserAndCourse(Users user, Course course);
    List<Enrollment> findByInstructor_InstructorId(Long instructorId);
    void deleteByInstructor_InstructorId(Long instructorId);
}