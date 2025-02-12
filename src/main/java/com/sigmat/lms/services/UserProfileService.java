package com.sigmat.lms.services;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepo userProfileRepository;

    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUsersId(userId);
    }

    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}