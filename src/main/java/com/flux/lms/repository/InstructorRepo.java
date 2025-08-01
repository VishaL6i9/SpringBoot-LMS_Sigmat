package com.flux.lms.repository;

import com.flux.lms.models.Institute;
import com.flux.lms.models.Instructor;
import com.flux.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorRepo extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByFirstName(String firstName);
    Optional<Instructor> findByInstructorId(Long instructorId);
    Optional<Instructor> findByUser_Id(Long userId);
    Instructor findByUser(Users user);
    
    boolean existsByEmail(String email);
    boolean existsByPhoneNo(String phoneNo);
    boolean existsByUser(Users user);
    Optional<Instructor> findByEmail(String email);
    Optional<Instructor> findByPhoneNo(String phoneNo);
    
    // Institute-based queries
    List<Instructor> findByInstitute(Institute institute);
    
    @Query("SELECT i FROM Instructor i WHERE i.institute.instituteId = :instituteId")
    List<Instructor> findByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT COUNT(i) FROM Instructor i WHERE i.institute = :institute")
    long countByInstitute(@Param("institute") Institute institute);
}

