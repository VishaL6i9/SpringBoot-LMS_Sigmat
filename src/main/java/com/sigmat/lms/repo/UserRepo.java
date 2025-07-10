package com.sigmat.lms.repo;

import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);
    Users findById(Long id);
    Users findByVerificationToken(String verificationToken);
    boolean existsByIdAndRoles_Name(Long userId, String roleName);

    @Query("SELECT u FROM Users u JOIN u.roles r WHERE u.id = :userId AND r.name = :roleName")
    Optional<Users> findByIdAndRoleName(@Param("userId") Long userId, @Param("roleName") String roleName);
}