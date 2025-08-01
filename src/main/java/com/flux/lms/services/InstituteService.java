package com.flux.lms.services;

import com.flux.lms.dtos.InstituteDTO;
import com.flux.lms.exceptions.ResourceNotFoundException;
import com.flux.lms.models.Institute;
import com.flux.lms.models.Role;
import com.flux.lms.models.Users;
import com.flux.lms.repository.InstituteRepository;
import com.flux.lms.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstituteService {
    
    private final InstituteRepository instituteRepository;
    private final UserRepo userRepository;

    @Transactional
    public InstituteDTO createInstitute(InstituteDTO instituteDTO) {
        // Validate admin user exists and has appropriate role
        Users admin = null;
        if (instituteDTO.getAdminId() != null) {
            admin = userRepository.findById(instituteDTO.getAdminId())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
            
            // Add INSTITUTION role to admin if not present
            admin.getRoles().add(Role.INSTITUTION);
            userRepository.save(admin);
        }

        Institute institute = Institute.builder()
                .instituteName(instituteDTO.getInstituteName())
                .instituteCode(instituteDTO.getInstituteCode())
                .description(instituteDTO.getDescription())
                .address(instituteDTO.getAddress())
                .city(instituteDTO.getCity())
                .state(instituteDTO.getState())
                .country(instituteDTO.getCountry())
                .postalCode(instituteDTO.getPostalCode())
                .email(instituteDTO.getEmail())
                .phoneNumber(instituteDTO.getPhoneNumber())
                .website(instituteDTO.getWebsite())
                .establishedDate(instituteDTO.getEstablishedDate())
                .isActive(true)
                .admin(admin)
                .build();

        Institute savedInstitute = instituteRepository.save(institute);
        
        // Update admin's institute reference
        if (admin != null) {
            admin.setInstitute(savedInstitute);
            userRepository.save(admin);
        }

        return convertToDTO(savedInstitute);
    }

    public List<InstituteDTO> getAllInstitutes() {
        return instituteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InstituteDTO> getActiveInstitutes() {
        return instituteRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InstituteDTO getInstituteById(Long id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));
        return convertToDTO(institute);
    }

    public InstituteDTO getInstituteByCode(String code) {
        Institute institute = instituteRepository.findByInstituteCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with code: " + code));
        return convertToDTO(institute);
    }

    public InstituteDTO getInstituteByAdmin(Long adminId) {
        Users admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
        
        Institute institute = instituteRepository.findByAdmin(admin)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found for admin: " + adminId));
        
        return convertToDTO(institute);
    }

    @Transactional
    public InstituteDTO updateInstitute(Long id, InstituteDTO instituteDTO) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));

        institute.setInstituteName(instituteDTO.getInstituteName());
        institute.setInstituteCode(instituteDTO.getInstituteCode());
        institute.setDescription(instituteDTO.getDescription());
        institute.setAddress(instituteDTO.getAddress());
        institute.setCity(instituteDTO.getCity());
        institute.setState(instituteDTO.getState());
        institute.setCountry(instituteDTO.getCountry());
        institute.setPostalCode(instituteDTO.getPostalCode());
        institute.setEmail(instituteDTO.getEmail());
        institute.setPhoneNumber(instituteDTO.getPhoneNumber());
        institute.setWebsite(instituteDTO.getWebsite());
        institute.setEstablishedDate(instituteDTO.getEstablishedDate());

        Institute updatedInstitute = instituteRepository.save(institute);
        return convertToDTO(updatedInstitute);
    }

    @Transactional
    public void deactivateInstitute(Long id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));
        
        institute.setActive(false);
        instituteRepository.save(institute);
    }

    @Transactional
    public void activateInstitute(Long id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));
        
        institute.setActive(true);
        instituteRepository.save(institute);
    }

    @Transactional
    public void deleteInstitute(Long id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));
        
        // Remove institute reference from admin
        if (institute.getAdmin() != null) {
            Users admin = institute.getAdmin();
            admin.setInstitute(null);
            admin.getRoles().remove(Role.INSTITUTION);
            userRepository.save(admin);
        }
        
        instituteRepository.delete(institute);
    }

    private InstituteDTO convertToDTO(Institute institute) {
        InstituteDTO.InstituteDTOBuilder builder = InstituteDTO.builder()
                .instituteId(institute.getInstituteId())
                .instituteName(institute.getInstituteName())
                .instituteCode(institute.getInstituteCode())
                .description(institute.getDescription())
                .address(institute.getAddress())
                .city(institute.getCity())
                .state(institute.getState())
                .country(institute.getCountry())
                .postalCode(institute.getPostalCode())
                .email(institute.getEmail())
                .phoneNumber(institute.getPhoneNumber())
                .website(institute.getWebsite())
                .establishedDate(institute.getEstablishedDate())
                .createdAt(institute.getCreatedAt())
                .updatedAt(institute.getUpdatedAt())
                .isActive(institute.isActive());

        // Add admin information if present
        if (institute.getAdmin() != null) {
            Users admin = institute.getAdmin();
            builder.adminId(admin.getId())
                   .adminName(admin.getFirstName() + " " + admin.getLastName())
                   .adminEmail(admin.getEmail());
        }

        // Add statistics
        builder.totalStudents(institute.getStudents() != null ? institute.getStudents().size() : 0)
               .totalInstructors(institute.getInstructors() != null ? institute.getInstructors().size() : 0)
               .totalCourses(institute.getCourses() != null ? institute.getCourses().size() : 0);

        return builder.build();
    }
}