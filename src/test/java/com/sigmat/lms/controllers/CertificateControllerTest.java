package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Certificate;
import com.sigmat.lms.models.CertificateDTO;
import com.sigmat.lms.services.CertificateService;
import com.sigmat.lms.services.CourseService;
import com.sigmat.lms.services.InstructorService;
import com.sigmat.lms.services.LearnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CertificateControllerTest {

    @InjectMocks
    private CertificateController certificateController;

    @Mock
    private CertificateService certificateService;

    @Mock
    private LearnerService learnerService;

    @Mock
    private CourseService courseService;

    @Mock
    private InstructorService instructorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCertificate_Found() {
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setCertificateId(1L);
        certificateDTO.setLearnerId(1L);
        certificateDTO.setCourseId(1L);
        certificateDTO.setInstructorId(1L);

        when(certificateService.getCertificateDtoById(1L)).thenReturn(Optional.of(certificateDTO));

        ResponseEntity<CertificateDTO> response = certificateController.getCertificate(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(certificateDTO, response.getBody());
    }

    @Test
    public void testGetCertificate_NotFound() {
        when(certificateService.getCertificateDtoById(1L)).thenReturn(Optional.empty());

        ResponseEntity<CertificateDTO> response = certificateController.getCertificate(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllCertificates() {
        CertificateDTO certificate1 = new CertificateDTO();
        certificate1.setCertificateId(1L);
        CertificateDTO certificate2 = new CertificateDTO();
        certificate2.setCertificateId(2L);

        List<CertificateDTO> certificates = Arrays.asList(certificate1, certificate2);
        when(certificateService.getAllCertificateDtos()).thenReturn(certificates);

        List<CertificateDTO> response = certificateController.getAllCertificates();

        assertEquals(certificates, response);
    }

    @Test
    public void testUpdateCertificate_Success() throws IOException {
        Long certificateId = 1L;
        Certificate existingCertificate = new Certificate();
        existingCertificate.setCertificateId(certificateId);
        existingCertificate.setLearner(null); 
        existingCertificate.setCourse(null); 
        existingCertificate.setInstructor(null);

        Certificate updatedCertificate = new Certificate();
        updatedCertificate.setLearner(null); 
        updatedCertificate.setCourse(null); 
        updatedCertificate.setInstructor(null); 

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(new byte[0]);

        when(certificateService.getCertificate(certificateId)).thenReturn(Optional.of(existingCertificate));
        when(certificateService.saveCertificate(any(Certificate.class))).thenReturn(existingCertificate);

        ResponseEntity<Certificate> response = certificateController.updateCertificate(certificateId, updatedCertificate, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingCertificate, response.getBody());
    }

    @Test
    public void testUpdateCertificate_NotFound() throws IOException {
        Long certificateId = 1L;
        Certificate updatedCertificate = new Certificate();

        when(certificateService.getCertificate(certificateId)).thenReturn(Optional.empty());

        ResponseEntity<Certificate> response = certificateController.updateCertificate(certificateId, updatedCertificate, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteCertificate() {
        Long certificateId = 1L;

        doNothing().when(certificateService).deleteCertificate(certificateId);

        ResponseEntity<Void> response = certificateController.deleteCertificate(certificateId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(certificateService, times(1)). deleteCertificate(certificateId);
    }
}