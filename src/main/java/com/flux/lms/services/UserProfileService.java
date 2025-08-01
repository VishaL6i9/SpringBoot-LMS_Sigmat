package com.flux.lms.services;

import com.flux.lms.dtos.ProfileImageUploadResponseDTO;
import com.flux.lms.dtos.UserDTO;
import com.flux.lms.dtos.UserProfileDTO;
import com.flux.lms.exceptions.UserProfileNotFoundException;
import com.flux.lms.models.ProfileImage;
import com.flux.lms.models.UserProfile;
import com.flux.lms.models.Users;
import com.flux.lms.repository.ProfileImageRepo;
import com.flux.lms.repository.UserProfileRepo;
import com.flux.lms.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepo userProfileRepository;
    private final UserRepo userRepo;
    private final ProfileImageRepo profileImageRepo;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(UserProfileRepo userProfileRepository, UserRepo userRepo, ProfileImageRepo profileImageRepo, PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.userRepo = userRepo;
        this.profileImageRepo = profileImageRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUsersId(userId);
    }

    public UserProfileDTO getUserProfileDto(Long userId) {
        UserProfile userProfile = getUserProfile(userId);
        if (userProfile == null) {
            return null;
        }
        return convertToDto(userProfile);
    }

    private UserProfileDTO convertToDto(UserProfile userProfile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(userProfile.getId());
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setEmail(userProfile.getEmail());
        dto.setPhone(userProfile.getPhone());
        dto.setAddress(userProfile.getAddress());
        dto.setLanguage(userProfile.getLanguage());
        dto.setTimezone(userProfile.getTimezone());
        dto.setProfileImage(userProfile.getProfileImage());

        if (userProfile.getUsers() != null) {
            UserDTO userDto = new UserDTO();
            userDto.setId(userProfile.getUsers().getId());
            userDto.setUsername(userProfile.getUsers().getUsername());
            userDto.setEmail(userProfile.getUsers().getEmail());
            userDto.setFirstName(userProfile.getUsers().getFirstName());
            userDto.setLastName(userProfile.getUsers().getLastName());
            dto.setUser(userDto);
        }

        return dto;
    }
    
    public UserProfile updateUserProfile(Long userId, UserProfile userProfile) {
        UserProfile existingProfile = userProfileRepository.findByUsersId(userId);
        Optional<Users> existingUser  = userRepo.findById(userId);

        if (existingProfile != null && existingUser.isPresent()) {
            Users updatedUser  = existingUser.get();

            existingProfile.setFirstName(userProfile.getFirstName());
            existingProfile.setLastName(userProfile.getLastName());
            existingProfile.setEmail(userProfile.getEmail());
            existingProfile.setPhone(userProfile.getPhone());
            existingProfile.setTimezone(userProfile.getTimezone());
            existingProfile.setLanguage(userProfile.getLanguage());

            updatedUser.setFirstName(userProfile.getFirstName());
            updatedUser.setLastName(userProfile.getLastName());
            updatedUser.setEmail(userProfile.getEmail());

            userProfileRepository.save(existingProfile);
            userRepo.save(updatedUser);

            return existingProfile;
        } else {
            throw new RuntimeException("User Profile not found with id: " + userId);
        }
    }

    public void updateUserPassword(Long userId, String newPassword) {
        Optional<Users> existingUser  = userRepo.findById(userId);
        UserProfile existingProfile = userProfileRepository.findByUsersId(userId);

        if (existingUser .isPresent()) {
            Users user = existingUser .get();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            if (existingProfile != null) {
                existingProfile.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);
                userProfileRepository.save(existingProfile);
            } else {
                throw new RuntimeException("User  Profile not found with id: " + userId);
            }
        } else {
            throw new RuntimeException("User  not found with id: " + userId);
        }
    }

 
    @Transactional
    public UserProfile saveProfileImage(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
    
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new RuntimeException("Invalid file type: " + contentType);
        }
    
        UserProfile userProfile = userProfileRepository.findByUsersId(userId);
        if (userProfile == null) {
            throw new UserProfileNotFoundException("User Profile not found with id: " + userId);
        }
        
        ProfileImage profileImage = new ProfileImage();
        profileImage.setImageData(file.getBytes());
        profileImage.setImageName(file.getOriginalFilename());
        profileImage.setContentType(contentType);
        profileImage = profileImageRepo.save(profileImage);
        userProfile.setProfileImage(profileImage);
    
        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public ProfileImageUploadResponseDTO saveProfileImageOptimized(Long userId, MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Check file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }
    
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new RuntimeException("Invalid file type. Only JPEG, PNG, and GIF are allowed");
        }

        // Validate filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            originalFilename = "profile_image_" + System.currentTimeMillis();
        }
    
        UserProfile userProfile = userProfileRepository.findByUsersId(userId);
        if (userProfile == null) {
            throw new UserProfileNotFoundException("User Profile not found with id: " + userId);
        }
        
        // Delete old profile image if exists
        if (userProfile.getProfileImage() != null) {
            ProfileImage oldImage = userProfile.getProfileImage();
            userProfile.setProfileImage(null);
            userProfileRepository.save(userProfile);
            profileImageRepo.delete(oldImage);
        }
        
        // Save new profile image
        ProfileImage profileImage = new ProfileImage();
        profileImage.setImageData(file.getBytes());
        profileImage.setImageName(originalFilename);
        profileImage.setContentType(contentType);
        profileImage = profileImageRepo.save(profileImage);
        
        // Update user profile
        userProfile.setProfileImage(profileImage);
        userProfileRepository.save(userProfile);

        return ProfileImageUploadResponseDTO.builder()
                .profileImageId(profileImage.getId())
                .imageName(profileImage.getImageName())
                .contentType(profileImage.getContentType())
                .message("Profile image uploaded successfully")
                .success(true)
                .build();
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    public Long getProfileImageID(Long userID) {
        UserProfile userProfile = userProfileRepository.findByUsersId(userID);
        if (userProfile != null) {
            ProfileImage profileImage = userProfile.getProfileImage();
            if (profileImage != null) {
                return profileImage.getId();
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfileWithImage(Long userId) {
        UserProfile profile = userProfileRepository.findByUsersId(userId);
        if (profile != null && profile.getProfileImage() != null) {
            
            ProfileImage img = profile.getProfileImage();
            if (img.getImageData() != null) {
                int dataLength = img.getImageData().length;
            }
        }
        return profile;
    }
}