package com.sigmat.lms.controllers;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/user/profile/{userID}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userID) {
        UserProfile userProfile = userProfileService.getUserProfile(Long.valueOf(userID));
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/user/profile")
    public ResponseEntity<UserProfile> updateUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile updatedProfile = userProfileService.updateUserProfile(userProfile);
        return ResponseEntity.ok(updatedProfile);
    }
}