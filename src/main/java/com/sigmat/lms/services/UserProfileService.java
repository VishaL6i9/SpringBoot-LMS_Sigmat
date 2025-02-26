package com.sigmat.lms.services;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserProfileRepo;
import com.sigmat.lms.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepo userProfileRepository;
    @Autowired
    private UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUsersId(userId);
    }

    public UserProfile updateUserProfile(UserProfile userProfile) {
        
        Optional<UserProfile> existingProfile = Optional.ofNullable(userProfileRepository.findByUsersId(userProfile.getId()));
        Optional<Users> existingUser = Optional.ofNullable(userRepo.findById(userProfile.getId()));

        if (existingProfile.isPresent()) {
            UserProfile updatedProfile = existingProfile.get();
            Users updatedUser = existingUser.get();
            
            updatedProfile.setFirstName(userProfile.getFirstName());
            updatedProfile.setLastName(userProfile.getLastName());
            updatedProfile.setEmail(userProfile.getEmail());
            updatedProfile.setPhone(userProfile.getPhone());
            updatedProfile.setTimezone(userProfile.getTimezone());
            updatedProfile.setLanguage(userProfile.getLanguage());
            updatedProfile.setProfileImage(userProfile.getProfileImage());
            
            updatedUser.setFirstName(userProfile.getFirstName());
            updatedUser.setLastName(userProfile.getLastName());
            updatedUser.setEmail(userProfile.getEmail());
            
            

            if (userProfile.getPassword() != null && !userProfile.getPassword().isEmpty()) {
                updatedProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
                updatedUser.setPassword(passwordEncoder.encode(userProfile.getPassword()));
            }
            return userProfileRepository.save(updatedProfile);
        } else {
            throw new RuntimeException("User Profile not found with id: " + userProfile.getId());
        }
    }
}