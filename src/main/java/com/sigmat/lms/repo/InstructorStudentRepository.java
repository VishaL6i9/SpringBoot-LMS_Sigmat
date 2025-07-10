package com.sigmat.lms.repo;

import com.sigmat.lms.models.InstructorStudentRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorStudentRepository extends JpaRepository<InstructorStudentRelationship, Long> {

    @Query("SELECT r FROM InstructorStudentRelationship r WHERE r.instructorId = :instructorId AND r.studentId = :studentId AND r.active = true")
    Optional<InstructorStudentRelationship> findByInstructorAndStudent(
            @Param("instructorId") Long instructorId,
            @Param("studentId") Long studentId);

    @Query("SELECT r.studentId FROM InstructorStudentRelationship r WHERE r.instructorId = :instructorId AND r.active = true")
    List<Long> findStudentIdsByInstructorId(@Param("instructorId") Long instructorId);

    boolean existsByInstructorIdAndStudentIdAndActiveTrue(Long instructorId, Long studentId);
}