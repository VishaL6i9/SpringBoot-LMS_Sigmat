package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.UserProfileNotFoundException;
import com.sigmat.lms.models.ProfileImage;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.ProfileImageRepo;
import com.sigmat.lms.repo.UserProfileRepo;
import com.sigmat.lms.repo.UserRepo;
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

    public UserProfile updateUserProfile(UserProfile userProfile) {
        UserProfile existingProfile = userProfileRepository.findByUsersId(userProfile.getId());
        Optional<Users> existingUser  = userRepo.findById(userProfile.getId());

        if (existingProfile != null && existingUser .isPresent()) {
            Users updatedUser  = existingUser .get();

            existingProfile.setFirstName(userProfile.getFirstName());
            existingProfile.setLastName(userProfile.getLastName());
            existingProfile.setEmail(userProfile.getEmail());
            existingProfile.setPhone(userProfile.getPhone());
            existingProfile.setTimezone(userProfile.getTimezone());
            existingProfile.setLanguage(userProfile.getLanguage());
            existingProfile.setProfileImage(userProfile.getProfileImage());

            updatedUser .setFirstName(userProfile.getFirstName());
            updatedUser .setLastName(userProfile.getLastName());
            updatedUser .setEmail(userProfile.getEmail());

            userProfileRepository.save(existingProfile);
            userRepo.save(updatedUser );

            return existingProfile;
        } else {
            throw new RuntimeException("User  Profile not found with id: " + userProfile.getId());
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