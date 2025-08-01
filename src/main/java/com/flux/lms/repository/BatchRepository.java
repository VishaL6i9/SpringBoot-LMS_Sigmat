package com.flux.lms.repository;

import com.flux.lms.models.Batch;
import com.flux.lms.models.Institute;
import com.flux.lms.models.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    
    List<Batch> findByInstitute(Institute institute);
    
    List<Batch> findByInstituteAndIsActiveTrue(Institute institute);
    
    List<Batch> findByInstructor(Instructor instructor);
    
    List<Batch> findByInstructorAndIsActiveTrue(Instructor instructor);
    
    Optional<Batch> findByBatchCode(String batchCode);
    
    List<Batch> findByStatus(Batch.BatchStatus status);
    
    List<Batch> findByInstituteAndStatus(Institute institute, Batch.BatchStatus status);
    
    List<Batch> findBySemester(String semester);
    
    List<Batch> findByAcademicYear(String academicYear);
    
    List<Batch> findByInstituteAndSemester(Institute institute, String semester);
    
    List<Batch> findByInstituteAndAcademicYear(Institute institute, String academicYear);
    
    @Query("SELECT b FROM Batch b WHERE b.institute.instituteId = :instituteId AND b.isActive = true")
    List<Batch> findActiveBatchesByInstituteId(@Param("instituteId") Long instituteId);
    
    @Query("SELECT b FROM Batch b WHERE b.instructor.instructorId = :instructorId AND b.isActive = true")
    List<Batch> findActiveBatchesByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT COUNT(s) FROM Users s WHERE s.batch.batchId = :batchId")
    Integer countStudentsByBatchId(@Param("batchId") Long batchId);
    
    @Query("SELECT b FROM Batch b WHERE b.maxStudents > (SELECT COUNT(s) FROM Users s WHERE s.batch = b)")
    List<Batch> findBatchesWithAvailableSlots();
}