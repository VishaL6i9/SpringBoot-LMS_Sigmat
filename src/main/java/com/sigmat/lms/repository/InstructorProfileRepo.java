package com.sigmat.lms.repository;

import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.models.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorProfileRepo extends JpaRepository<InstructorProfile, Long> {
    InstructorProfile findByInstructor(Instructor instructor);
    InstructorProfile findByInstructorInstructorId(Long instructorId);
}