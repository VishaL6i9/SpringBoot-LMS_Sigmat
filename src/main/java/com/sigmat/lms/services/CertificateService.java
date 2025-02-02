package com.sigmat.lms.services;

import com.sigmat.lms.models.Certificate;
import com.sigmat.lms.models.CertificateDTO; // Import your DTO
import com.sigmat.lms.repo.CertificateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepo certificateRepository;

    public Optional<Certificate> getCertificate(Long id) {
        return certificateRepository.findById(id);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Certificate saveCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public void deleteCertificate(Long id) {
        certificateRepository.deleteById(id);
    }
    
    public Optional<CertificateDTO> getCertificateDtoById(Long id) {
        return certificateRepository.findCertificateDtoById(id);
    }

    public List<CertificateDTO> getAllCertificateDtos() {
        return certificateRepository.findAllCertificateDtos();
    }
}