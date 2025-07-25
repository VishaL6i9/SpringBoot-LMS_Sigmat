package com.sigmat.lms.repository;

import com.sigmat.lms.models.Enrollment;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.models.Course;
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
    List<Enrollment> findByInstructor_InstructorId(Long instructorId);
    void deleteByInstructor_InstructorId(Long instructorId);
}