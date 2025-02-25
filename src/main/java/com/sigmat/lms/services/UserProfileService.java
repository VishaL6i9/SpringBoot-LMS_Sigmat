package com.sigmat.lms.services;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepo userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUsersId(userId);
    }

    public UserProfile updateUserProfile(UserProfile userProfile) {
        
        Optional<UserProfile> existingProfile = userProfileRepository.findById(userProfile.getId());

        if (existingProfile.isPresent()) {
            UserProfile updatedProfile = existingProfile.get();
            updatedProfile.setFirstName(userProfile.getFirstName());
            updatedProfile.setLastName(userProfile.getLastName());
            updatedProfile.setEmail(userProfile.getEmail());
            updatedProfile.setPhone(userProfile.getPhone());
            updatedProfile.setTimezone(userProfile.getTimezone());
            updatedProfile.setLanguage(userProfile.getLanguage());
            updatedProfile.setProfileImage(userProfile.getProfileImage());

            if (userProfile.getPassword() != null && !userProfile.getPassword().isEmpty()) {
                updatedProfile.setPassword(passwordEncoder.encode(userProfile.getPassword())); // Hash the password before saving
            }
            return userProfileRepository.save(updatedProfile);
        } else {
            throw new RuntimeException("User  Profile not found with id: " + userProfile.getId());
        }
    }
}