package com.flux.lms.controllers;

import com.flux.lms.dtos.CertificateDTO;
import com.flux.lms.models.Certificate;
import com.flux.lms.models.Course;
import com.flux.lms.models.Instructor;
import com.flux.lms.models.UserProfile;
import com.flux.lms.repository.CertificateRepo;
import com.flux.lms.repository.CourseRepo;
import com.flux.lms.services.CertificateService;
import com.flux.lms.services.CourseService;
import com.flux.lms.services.InstructorService;
import com.flux.lms.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;
    @Autowired
    private CertificateRepo certificateRepo;
    @Autowired
    private InstructorService instructorService;
    @Autowired
    private UserProfileService userProfileService; // Changed from LearnerService
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRepo courseRepo;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR') or (@certificateService.getCertificate(#id).userProfile.users.id == authentication.principal.id)")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable Long id) {
        Optional<CertificateDTO> certificateDTO = certificateService.getCertificateDtoById(id); 

        return certificateDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public List<CertificateDTO> getAllCertificates() {
        return certificateService.getAllCertificateDtos(); 
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Certificate> createCertificate(
            @RequestParam("userProfileId") Long userProfileId,
            @RequestParam("courseId") Long courseId,
            @RequestParam("instructorId") Long instructorId,
            @RequestParam("dateOfCertificate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfCertificate,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        if (userProfileId == null || courseId == null || instructorId == null || dateOfCertificate == null) {
            return ResponseEntity.badRequest().build(); 
        }

        try { 
            UserProfile userProfile = userProfileService.getUserProfile(userProfileId);
            Course course = courseRepo.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Course not found"));
            Instructor instructor = instructorService.getInstructorById(instructorId).orElseThrow(() -> new EntityNotFoundException("Instructor not found"));

            Certificate.CertificateBuilder builder = Certificate.builder()
                    .userProfile(userProfile)
                    .course(course)
                    .instructor(instructor)
                    .dateOfCertificate(dateOfCertificate);

            if (file != null && !file.isEmpty()) {
                builder.certificate(file.getBytes());
            }

            Certificate certificate = builder.build();
            Certificate createdCertificate = certificateService.saveCertificate(certificate);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificate);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); 
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build(); 
        } 
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Certificate> updateCertificate(
            @PathVariable Long id,
            @RequestPart("certificate") Certificate updatedCertificate,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Certificate certificate = certificateService.getCertificate(id).orElse(null);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }
    
        certificate.setUserProfile(updatedCertificate.getUserProfile());
        certificate.setCourse(updatedCertificate.getCourse());
        certificate.setInstructor(updatedCertificate.getInstructor());
        certificate.setDateOfCertificate(updatedCertificate.getDateOfCertificate());
        
        if (file != null && !file.isEmpty()) {
            certificate.setCertificate(file.getBytes());
        }

        Certificate savedCertificate = certificateService.saveCertificate(certificate);
        return ResponseEntity.ok(savedCertificate);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }
    

}