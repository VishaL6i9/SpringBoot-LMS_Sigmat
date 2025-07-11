package com.sigmat.lms.repo;

import com.sigmat.lms.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser_Id(Long userId);
    List<Enrollment> findByCourse_CourseId(Long courseId);
    Optional<Enrollment> findByUser_IdAndCourse_CourseId(Long userId, Long courseId);
    List<Enrollment> findByInstructor_InstructorId(Long instructorId);
    void deleteByInstructor_InstructorId(Long instructorId);
}