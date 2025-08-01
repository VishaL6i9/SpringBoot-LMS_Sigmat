package com.flux.lms.repository;

import com.flux.lms.models.Institute;
import com.flux.lms.models.ParentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentProfileRepository extends JpaRepository<ParentProfile, Long> {
    
    Optional<ParentProfile> findByParentEmail(String parentEmail);
    
    List<ParentProfile> findByInstitute(Institute institute);
    
    List<ParentProfile> findByInstituteAndIsActiveTrue(Institute institute);
    
    @Query("SELECT p FROM ParentProfile p JOIN p.children c WHERE c.id = :studentId")
    List<ParentProfile> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT p FROM ParentProfile p WHERE p.institute.instituteId = :instituteId AND p.isActive = true")
    List<ParentProfile> findActiveParentsByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT p FROM ParentProfile p WHERE p.phoneNumber = :phoneNumber")
    List<ParentProfile> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT p FROM ParentProfile p WHERE p.parentName LIKE %:name%")
    List<ParentProfile> findByParentNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(c) FROM Users c WHERE c.parentProfile.parentId = :parentId")
    Integer countChildrenByParentId(@Param("parentId") Long parentId);
    
    boolean existsByParentEmail(String parentEmail);
    
    boolean existsByPhoneNumber(String phoneNumber);
}