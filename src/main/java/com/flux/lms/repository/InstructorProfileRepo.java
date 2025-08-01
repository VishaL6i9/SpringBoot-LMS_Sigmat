package com.flux.lms.repository;

import com.flux.lms.models.Instructor;
import com.flux.lms.models.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorProfileRepo extends JpaRepository<InstructorProfile, Long> {
    InstructorProfile findByInstructor(Instructor instructor);
    InstructorProfile findByInstructorInstructorId(Long instructorId);
}