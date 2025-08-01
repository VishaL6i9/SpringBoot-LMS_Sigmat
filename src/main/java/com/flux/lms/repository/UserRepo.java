package com.flux.lms.repository;

import com.flux.lms.models.Institute;
import com.flux.lms.models.Role;
import com.flux.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Optional<Users> findById(Long id);
    Users findByVerificationToken(String verificationToken);
    Users findByEmail(String email);
    
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Institute-based queries
    List<Users> findByInstitute(Institute institute);
    
    @Query("SELECT u FROM Users u WHERE u.institute = :institute AND :role MEMBER OF u.roles")
    List<Users> findByInstituteAndRole(@Param("institute") Institute institute, @Param("role") Role role);
    
    @Query("SELECT u FROM Users u WHERE u.institute.instituteId = :instituteId")
    List<Users> findByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT u FROM Users u WHERE u.institute.instituteId = :instituteId AND :role MEMBER OF u.roles")
    List<Users> findByInstituteIdAndRole(@Param("instituteId") Long instituteId, @Param("role") Role role);
}