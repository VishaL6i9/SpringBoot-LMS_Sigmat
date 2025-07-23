package com.sigmat.lms.repository;

import com.sigmat.lms.models.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRepo extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByFirstName(String firstName);
    Optional<Instructor> findByInstructorId(Long instructorId);
    
}
