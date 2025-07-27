package com.sigmat.lms.services;

import com.sigmat.lms.dtos.InstructorDTO;
import com.sigmat.lms.dtos.InstructorProfileDTO;
import com.sigmat.lms.exceptions.InstructorProfileNotFoundException;
import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.models.InstructorProfile;
import com.sigmat.lms.models.ProfileImage;
import com.sigmat.lms.repository.InstructorProfileRepo;
import com.sigmat.lms.repository.InstructorRepo;
import com.sigmat.lms.repository.ProfileImageRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class InstructorProfileService {

    private final InstructorProfileRepo instructorProfileRepository;
    private final InstructorRepo instructorRepo;
    private final ProfileImageRepo profileImageRepo;
    private final PasswordEncoder passwordEncoder;

    public InstructorProfileService(InstructorProfileRepo instructorProfileRepository, 
                                  InstructorRepo instructorRepo, 
                                  ProfileImageRepo profileImageRepo, 
                                  PasswordEncoder passwordEncoder) {
        this.instructorProfileRepository = instructorProfileRepository;
        this.instructorRepo = instructorRepo;
        this.profileImageRepo = profileImageRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public InstructorProfile getInstructorProfile(Long instructorId) {
        return instructorProfileRepository.findByInstructorInstructorId(instructorId);
    }

    public InstructorProfileDTO getInstructorProfileDto(Long instructorId) {
        InstructorProfile instructorProfile = getInstructorProfile(instructorId);
        if (instructorProfile == null) {
            return null;
        }
        return convertToDto(instructorProfile);
    }

    private InstructorProfileDTO convertToDto(InstructorProfile instructorProfile) {
        InstructorProfileDTO dto = new InstructorProfileDTO();
        dto.setId(instructorProfile.getId());
        dto.setFirstName(instructorProfile.getFirstName());
        dto.setLastName(instructorProfile.getLastName());
        dto.setEmail(instructorProfile.getEmail());
        dto.setPhoneNo(instructorProfile.getPhoneNo());
        dto.setAddress(instructorProfile.getAddress());
        dto.setLanguage(instructorProfile.getLanguage());
        dto.setTimezone(instructorProfile.getTimezone());
        dto.setBio(instructorProfile.getBio());
        dto.setSpecialization(instructorProfile.getSpecialization());
        dto.setDateOfJoining(instructorProfile.getDateOfJoining());
        dto.setBankName(instructorProfile.getBankName());
        dto.setAccountNumber(instructorProfile.getAccountNumber());
        dto.setRoutingNumber(instructorProfile.getRoutingNumber());
        dto.setAccountHolderName(instructorProfile.getAccountHolderName());
        dto.setFacebookHandle(instructorProfile.getFacebookHandle());
        dto.setLinkedinHandle(instructorProfile.getLinkedinHandle());
        dto.setYoutubeHandle(instructorProfile.getYoutubeHandle());
        dto.setProfileImage(instructorProfile.getProfileImage());

        if (instructorProfile.getInstructor() != null) {
            InstructorDTO instructorDto = new InstructorDTO();
            Instructor instructor = instructorProfile.getInstructor();
            instructorDto.setInstructorId(instructor.getInstructorId());
            instructorDto.setFirstName(instructor.getFirstName());
            instructorDto.setLastName(instructor.getLastName());
            instructorDto.setEmail(instructor.getEmail());
            instructorDto.setPhoneNo(instructor.getPhoneNo());
            instructorDto.setDateOfJoining(instructor.getDateOfJoining());
            instructorDto.setFacebookHandle(instructor.getFacebookHandle());
            instructorDto.setLinkedinHandle(instructor.getLinkedinHandle());
            instructorDto.setYoutubeHandle(instructor.getYoutubeHandle());
            dto.setInstructor(instructorDto);
        }

        return dto;
    }

    public InstructorProfile updateInstructorProfile(Long instructorId, InstructorProfile instructorProfile) {
        InstructorProfile existingProfile = instructorProfileRepository.findByInstructorInstructorId(instructorId);
        Optional<Instructor> existingInstructor = instructorRepo.findByInstructorId(instructorId);

        if (existingProfile != null && existingInstructor.isPresent()) {
            Instructor updatedInstructor = existingInstructor.get();

            // Update profile fields
            existingProfile.setFirstName(instructorProfile.getFirstName());
            existingProfile.setLastName(instructorProfile.getLastName());
            existingProfile.setEmail(instructorProfile.getEmail());
            existingProfile.setPhoneNo(instructorProfile.getPhoneNo());
            existingProfile.setAddress(instructorProfile.getAddress());
            existingProfile.setTimezone(instructorProfile.getTimezone());
            existingProfile.setLanguage(instructorProfile.getLanguage());
            existingProfile.setBio(instructorProfile.getBio());
            existingProfile.setSpecialization(instructorProfile.getSpecialization());
            existingProfile.setBankName(instructorProfile.getBankName());
            existingProfile.setAccountNumber(instructorProfile.getAccountNumber());
            existingProfile.setRoutingNumber(instructorProfile.getRoutingNumber());
            existingProfile.setAccountHolderName(instructorProfile.getAccountHolderName());
            existingProfile.setFacebookHandle(instructorProfile.getFacebookHandle());
            existingProfile.setLinkedinHandle(instructorProfile.getLinkedinHandle());
            existingProfile.setYoutubeHandle(instructorProfile.getYoutubeHandle());

            // Update instructor entity
            updatedInstructor.setFirstName(instructorProfile.getFirstName());
            updatedInstructor.setLastName(instructorProfile.getLastName());
            updatedInstructor.setEmail(instructorProfile.getEmail());
            updatedInstructor.setPhoneNo(instructorProfile.getPhoneNo());
            updatedInstructor.setFacebookHandle(instructorProfile.getFacebookHandle());
            updatedInstructor.setLinkedinHandle(instructorProfile.getLinkedinHandle());
            updatedInstructor.setYoutubeHandle(instructorProfile.getYoutubeHandle());

            instructorProfileRepository.save(existingProfile);
            instructorRepo.save(updatedInstructor);

            return existingProfile;
        } else {
            throw new RuntimeException("Instructor Profile not found with id: " + instructorId);
        }
    }

    public void updateInstructorPassword(Long instructorId, String newPassword) {
        InstructorProfile existingProfile = instructorProfileRepository.findByInstructorInstructorId(instructorId);

        if (existingProfile != null) {
            existingProfile.setPassword(passwordEncoder.encode(newPassword));
            instructorProfileRepository.save(existingProfile);
        } else {
            throw new RuntimeException("Instructor Profile not found with id: " + instructorId);
        }
    }

    @Transactional
    public InstructorProfile saveProfileImage(Long instructorId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new RuntimeException("Invalid file type: " + contentType);
        }

        InstructorProfile instructorProfile = instructorProfileRepository.findByInstructorInstructorId(instructorId);
        if (instructorProfile == null) {
            throw new InstructorProfileNotFoundException("Instructor Profile not found with id: " + instructorId);
        }

        ProfileImage profileImage = new ProfileImage();
        profileImage.setImageData(file.getBytes());
        profileImage.setImageName(file.getOriginalFilename());
        profileImage.setContentType(contentType);
        profileImage = profileImageRepo.save(profileImage);
        instructorProfile.setProfileImage(profileImage);

        return instructorProfileRepository.save(instructorProfile);
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    public Long getProfileImageID(Long instructorId) {
        InstructorProfile instructorProfile = instructorProfileRepository.findByInstructorInstructorId(instructorId);
        if (instructorProfile != null) {
            ProfileImage profileImage = instructorProfile.getProfileImage();
            if (profileImage != null) {
                return profileImage.getId();
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public InstructorProfile getInstructorProfileWithImage(Long instructorId) {
        InstructorProfile profile = instructorProfileRepository.findByInstructorInstructorId(instructorId);
        if (profile != null && profile.getProfileImage() != null) {
            ProfileImage img = profile.getProfileImage();
            if (img.getImageData() != null) {
                int dataLength = img.getImageData().length;
            }
        }
        return profile;
    }

    public InstructorProfile createInstructorProfile(Long instructorId) {
        Optional<Instructor> instructorOpt = instructorRepo.findByInstructorId(instructorId);
        if (instructorOpt.isPresent()) {
            Instructor instructor = instructorOpt.get();
            
            InstructorProfile profile = new InstructorProfile();
            profile.setInstructor(instructor);
            profile.setFirstName(instructor.getFirstName());
            profile.setLastName(instructor.getLastName());
            profile.setEmail(instructor.getEmail());
            profile.setPhoneNo(instructor.getPhoneNo());
            profile.setDateOfJoining(instructor.getDateOfJoining());
            profile.setFacebookHandle(instructor.getFacebookHandle());
            profile.setLinkedinHandle(instructor.getLinkedinHandle());
            profile.setYoutubeHandle(instructor.getYoutubeHandle());
            
            return instructorProfileRepository.save(profile);
        } else {
            throw new RuntimeException("Instructor not found with id: " + instructorId);
        }
    }
}