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
                existingProfile.setPassword(encodedPassword);
                userRepo.save(user);
                userProfileRepository.save(existingProfile);
            } else {
                throw new RuntimeException("User  Profile not found with id: " + userId);
            }
        } else {
            throw new RuntimeException("User  not found with id: " + userId);
        }
    }
}