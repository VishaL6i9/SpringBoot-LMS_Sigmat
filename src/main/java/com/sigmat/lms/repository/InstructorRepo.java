package com.sigmat.lms.repository;

import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRepo extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByFirstName(String firstName);
    Optional<Instructor> findByInstructorId(Long instructorId);
    Optional<Instructor> findByUser_Id(Long userId);
    Instructor findByUser(Users user);
}

