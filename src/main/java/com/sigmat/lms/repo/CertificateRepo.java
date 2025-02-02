package com.sigmat.lms.repo;

import com.sigmat.lms.models.Certificate;
import com.sigmat.lms.models.CertificateDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository 
public interface CertificateRepo extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCertificateId(Long certificateId);

    @Query("SELECT new com.sigmat.lms.models.CertificateDTO(" +
            "c.certificateId, l.learnerId, l.firstName, co.courseId, co.courseName, i.instructorId, i.firstName, c.dateOfCertificate) " +
            "FROM Certificate c " +
            "JOIN c.learner l " +
            "JOIN c.course co " +
            "JOIN c.instructor i " +
            "WHERE c.certificateId = :certificateId")
    Optional<CertificateDTO> findCertificateDtoById(@Param("certificateId") Long certificateId);


    @Query("SELECT new com.sigmat.lms.models.CertificateDTO(" +
            "c.certificateId, l.learnerId, l.firstName, co.courseId, co.courseName, i.instructorId, i.firstName, c.dateOfCertificate) " +
            "FROM Certificate c " +
            "JOIN c.learner l " +
            "JOIN c.course co " +
            "JOIN c.instructor i")
    List<CertificateDTO> findAllCertificateDtos();

}