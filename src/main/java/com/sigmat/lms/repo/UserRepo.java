package com.sigmat.lms.repo;

import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);
    Optional<Users> findById(Long id);
}