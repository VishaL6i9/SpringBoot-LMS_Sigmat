package com.sigmat.lms.controllers; // Your package name

import com.sigmat.lms.models.*;
import com.sigmat.lms.repo.CertificateRepo;
import com.sigmat.lms.repo.CourseRepo;
import com.sigmat.lms.services.CertificateService;
import com.sigmat.lms.services.CourseService;
import com.sigmat.lms.services.InstructorService;
import com.sigmat.lms.services.LearnerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/public/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;
    @Autowired
    private CertificateRepo certificateRepo;
    @Autowired
    private InstructorService instructorService;
    @Autowired
    private LearnerService learnerService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRepo courseRepo;

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable Long id) {
        Optional<CertificateDTO> certificateDTO = certificateService.getCertificateDtoById(id); 

        return certificateDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CertificateDTO> getAllCertificates() {
        return certificateService.getAllCertificateDtos(); 
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Certificate> createCertificate(
            @RequestParam("learnerId") Long learnerId,
            @RequestParam("courseId") Long courseId,
            @RequestParam("instructorId") Long instructorId,
            @RequestParam("dateOfCertificate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfCertificate,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        if (learnerId == null || courseId == null || instructorId == null || dateOfCertificate == null) {
            return ResponseEntity.badRequest().build(); 
        }

        try { 
            Learner learner = learnerService.getLearnerById(learnerId).orElseThrow(() -> new EntityNotFoundException("Learner not found"));
            Course course = courseService.getCourseById(courseId).orElseThrow(() -> new EntityNotFoundException("Course not found"));
            Instructor instructor = instructorService.getInstructorById(instructorId).orElseThrow(() -> new EntityNotFoundException("Instructor not found"));

            Certificate.CertificateBuilder builder = Certificate.builder()
                    .learner(learner)
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // Generic error handling
        }
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Certificate> updateCertificate(
            @PathVariable Long id,
            @RequestPart("certificate") Certificate updatedCertificate,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Certificate certificate = certificateService.getCertificate(id).orElse(null);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }
    
        certificate.setLearner(updatedCertificate.getLearner());
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
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id, Model model) throws IOException, DocumentException {

        // 1. Retrieve Certificate Data (replace with your actual data retrieval)
        CertificateDTO certificateDTO = certificateRepo.findCertificateDtoById(id).orElseThrow(() -> new EntityNotFoundException("Certificate not found")); // Handle not found
        // 2. Prepare Data for Thymeleaf Template
        model.addAttribute("learnerFirstName", certificateDTO.getLearnerFirstName());
        model.addAttribute("courseName", certificateDTO.getCourseName());
        model.addAttribute("instructorFirstName", certificateDTO.getInstructorFirstName());
        model.addAttribute("dateOfCertificate", certificateDTO.getDateOfCertificate());
        // ... other data for the certificate ...

        // 3. Generate HTML from Thymeleaf Template
        String html = generateHtmlFromTemplate("certificateTemplate", model); // See helper function below

        // 4. Convert HTML to PDF using Flying Saucer
        byte[] pdfBytes = generatePdfFromHtml(html);

        // 5. Return PDF as response
        String filename = String.format("certificate_%d_%s.pdf", id, certificateDTO.getLearnerFirstName().replace(" ", "_"));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDispositionUtils.attachment()
                        .filename(filename, StandardCharsets.UTF_8)
                        .build().toString())
                .body(pdfBytes);
    }

    // Helper function to generate HTML from Thymeleaf template
    private String generateHtmlFromTemplate(String templateName, Model model) {
        // Use Thymeleaf to process the template and return the HTML string.
        // This requires a configured Spring TemplateEngine bean.
        // Example:
        // Context context = new Context();
        // context.setVariables(model.asMap()); // Add model attributes to the context.
        // return templateEngine.process(templateName, context);
        return ""; // Placeholder
    }

    // Helper function to generate PDF from HTML
    private byte[] generatePdfFromHtml(String html) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

}