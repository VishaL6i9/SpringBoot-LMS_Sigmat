package com.sigmat.lms.repository;

import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Optional<Users> findById(Long id);
    Users findByVerificationToken(String verificationToken);
    Users findByEmail(String email);
    
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}