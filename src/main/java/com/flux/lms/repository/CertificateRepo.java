package com.flux.lms.repository;

import com.flux.lms.dtos.CertificateDTO;
import com.flux.lms.models.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface CertificateRepo extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCertificateId(Long certificateId);

    @Query("SELECT new com.flux.lms.dtos.CertificateDTO(" +
            "c.certificateId, up.id, up.firstName, co.courseId, co.courseName, i.instructorId, i.firstName, c.dateOfCertificate) " +
            "FROM Certificate c " +
            "JOIN c.userProfile up " +
            "JOIN c.course co " +
            "JOIN c.instructor i " +
            "WHERE c.certificateId = :certificateId")
    Optional<CertificateDTO> findCertificateDtoById(@Param("certificateId") Long certificateId);

    @Query("SELECT new com.flux.lms.dtos.CertificateDTO(" +
            "c.certificateId, up.id, up.firstName, co.courseId, co.courseName, i.instructorId, i.firstName, c.dateOfCertificate) " +
            "FROM Certificate c " +
            "JOIN c.userProfile up " +
            "JOIN c.course co " +
            "JOIN c.instructor i")
    List<CertificateDTO> findAllCertificateDtos();
}