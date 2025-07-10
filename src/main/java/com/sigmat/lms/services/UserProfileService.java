package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.UserProfileNotFoundException;
import com.sigmat.lms.models.ProfileImage;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.ProfileImageRepo;
import com.sigmat.lms.repo.UserProfileRepo;
import com.sigmat.lms.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRelationshipService relationshipService;

    @Autowired
    public UserProfileService(UserProfileRepo userProfileRepository,
                              UserRepo userRepo,
                              ProfileImageRepo profileImageRepo,
                              PasswordEncoder passwordEncoder,
                              UserRelationshipService relationshipService) {
        this.userProfileRepository = userProfileRepository;
        this.userRepo = userRepo;
        this.profileImageRepo = profileImageRepo;
        this.passwordEncoder = passwordEncoder;
        this.relationshipService = relationshipService;
    }

    // Enhanced method with access control
    public UserProfile getUserProfile(Long userId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Users currentUser = userRepo.findByUsername(userDetails.getUsername());

        // Admin can access any profile
        if (isAdmin(userDetails)) {
            return userProfileRepository.findByUsersId(userId);
        }

        // Users can only access their own profile
        if (isUser(userDetails) && !userId.equals(currentUser.getId())) {
            throw new AccessDeniedException("Not authorized to access this profile");
        }

        // Instructors can access their own profile and their students' profiles
        if (isInstructor(userDetails)) {
            if (userId.equals(currentUser.getId()) || relationshipService.isStudentOfInstructor(userId, currentUser.getId())) {
                return userProfileRepository.findByUsersId(userId);
            }
            throw new AccessDeniedException("Not authorized to access this student profile");
        }

        throw new AccessDeniedException("Unauthorized access");
    }

    // Update methods with access control
    public UserProfile updateUserProfile(UserProfile userProfile, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Users currentUser = userRepo.findByUsername(userDetails.getUsername());

        // Check if user is updating their own profile or is admin
        if (!userProfile.getUsers().getId().equals(currentUser.getId()) && !isAdmin(userDetails)) {
            throw new AccessDeniedException("Not authorized to update this profile");
        }

        return updateUserProfile(userProfile); // call to your existing method
    }

    public void updateUserPassword(Long userId, String newPassword, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Users currentUser = userRepo.findByUsername(userDetails.getUsername());

        // Check if user is updating their own password or is admin
        if (!userId.equals(currentUser.getId()) && !isAdmin(userDetails)) {
            throw new AccessDeniedException("Not authorized to update this password");
        }

        updateUserPassword(userId, newPassword); // call to your existing method
    }

    // Existing methods remain unchanged but can now call the secured versions
    public UserProfile getUserProfile(Long userId) {
        // This is now the internal unsecured version
        return userProfileRepository.findByUsersId(userId);
    }

    public UserProfile updateUserProfile(UserProfile userProfile) {
        // Existing implementation remains unchanged
        UserProfile existingProfile = userProfileRepository.findByUsersId(userProfile.getId());
        Optional<Users> existingUser = Optional.ofNullable(userRepo.findById(userProfile.getId()));

        if (existingProfile != null && existingUser.isPresent()) {
            Users updatedUser = existingUser.get();

            existingProfile.setFirstName(userProfile.getFirstName());
            existingProfile.setLastName(userProfile.getLastName());
            existingProfile.setEmail(userProfile.getEmail());
            existingProfile.setPhone(userProfile.getPhone());
            existingProfile.setTimezone(userProfile.getTimezone());
            existingProfile.setLanguage(userProfile.getLanguage());
            existingProfile.setProfileImage(userProfile.getProfileImage());

            updatedUser.setFirstName(userProfile.getFirstName());
            updatedUser.setLastName(userProfile.getLastName());
            updatedUser.setEmail(userProfile.getEmail());

            userProfileRepository.save(existingProfile);
            userRepo.save(updatedUser);

            return existingProfile;
        } else {
            throw new RuntimeException("User Profile not found with id: " + userProfile.getId());
        }
    }

    public void updateUserPassword(Long userId, String newPassword) {
        // Existing implementation remains unchanged
        Optional<Users> existingUser = Optional.ofNullable(userRepo.findById(userId));
        UserProfile existingProfile = userProfileRepository.findByUsersId(userId);

        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            if (existingProfile != null) {
                existingProfile.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);
                userProfileRepository.save(existingProfile);
            } else {
                throw new RuntimeException("User Profile not found with id: " + userId);
            }
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    // Helper methods for role checking
    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isInstructor(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSTRUCTOR"));
    }

    private boolean isUser(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    }

    // Rest of your existing methods remain exactly the same...
    @Transactional
    public UserProfile saveProfileImage(Long userId, MultipartFile file) throws IOException {
        // Existing implementation remains unchanged
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