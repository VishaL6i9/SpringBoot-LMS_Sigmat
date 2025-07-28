package com.sigmat.lms.repository;

import com.sigmat.lms.models.Institute;
import com.sigmat.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {
    
    Optional<Institute> findByInstituteName(String instituteName);
    
    Optional<Institute> findByInstituteCode(String instituteCode);
    
    Optional<Institute> findByEmail(String email);
    
    Optional<Institute> findByAdmin(Users admin);
    
    List<Institute> findByIsActiveTrue();
    
    List<Institute> findByIsActiveFalse();
    
    @Query("SELECT i FROM Institute i WHERE i.city = :city AND i.isActive = true")
    List<Institute> findActiveByCityName(@Param("city") String city);
    
    @Query("SELECT i FROM Institute i WHERE i.state = :state AND i.isActive = true")
    List<Institute> findActiveByStateName(@Param("state") String state);
    
    @Query("SELECT i FROM Institute i WHERE i.country = :country AND i.isActive = true")
    List<Institute> findActiveByCountryName(@Param("country") String country);
    
    boolean existsByInstituteName(String instituteName);
    
    boolean existsByInstituteCode(String instituteCode);
    
    boolean existsByEmail(String email);
}