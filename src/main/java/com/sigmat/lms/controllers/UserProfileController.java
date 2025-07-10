package com.sigmat.lms.controllers;

import com.sigmat.lms.models.ProfileImage;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.ProfileImageRepo;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.services.JwtService;
import com.sigmat.lms.services.ProfileImageService;
import com.sigmat.lms.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "file://*"})
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'INSTRUCTOR')")
public class UserProfileController {

    private final JwtService jwtService;
    private final UserProfileService userProfileService;
    private final UserRepo userRepo;

    @Autowired
    public UserProfileController(JwtService jwtService, UserProfileService userProfileService, UserRepo userRepo, ProfileImageRepo profileImageRepo, ProfileImageService profileImageService) {
        this.jwtService = jwtService;
        this.userProfileService = userProfileService;
        this.userRepo = userRepo;
    }

    //Retrieve User Profile From UserID
    @GetMapping("/profile/{userID}")
    @PreAuthorize("#userID == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userID) {
        UserProfile userProfile = userProfileService.getUserProfile(Long.valueOf(userID));
        return ResponseEntity.ok(userProfile);
    }

    //Update User Profile
    @PutMapping("/profile")
    @PreAuthorize("#userProfile.users.id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserProfile> updateUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile updatedProfile = userProfileService.updateUserProfile(userProfile);
        return ResponseEntity.ok(updatedProfile);
    }

    //Update User Password
    @PutMapping("/profile/password")
    @PreAuthorize("#userID == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserPassword(@RequestParam Long userID, @RequestParam String newPassword) {
        userProfileService.updateUserPassword(userID, newPassword);
        return ResponseEntity.ok().build();
    }

    //Retrieve UserID From JWT
    @GetMapping("/profile/getuserID")
    public ResponseEntity<Long> getUserID(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtService.extractUserName(token);
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users user = userRepo.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user.getId());
    }
    
    //Retrieve ProfileImageID From UserID
    @GetMapping("/profile/getProfileImageID/{userID}")
    @PreAuthorize("#userID == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Long> getProfileImageID(@PathVariable Long userID) {
        Long profileImageID = userProfileService.getProfileImageID(userID);
        return ResponseEntity.ok(profileImageID);
    }
    
    //Save ProfileImage With UserID
    @PostMapping("/profile/pic/upload/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<UserProfile> uploadProfileImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            UserProfile updatedProfile = userProfileService.saveProfileImage(userId, file);

            if (updatedProfile != null) {
                return ResponseEntity.ok(updatedProfile);
            } else {
                return ResponseEntity.notFound().build(); 
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build(); 
        }
    }
    
    @GetMapping("/profile/pic/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> getProfilePic(@PathVariable Long userId) {
        try {
            UserProfile userProfile = userProfileService.getUserProfileWithImage(userId);
    
            if (userProfile == null || userProfile.getProfileImage() == null) {
                return ResponseEntity.notFound().build();
            }
    
            ProfileImage profileImage = userProfile.getProfileImage();
    
            String contentType = profileImage.getContentType();
            if (contentType == null) {
                contentType = "image/jpeg";
            }
    
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(profileImage.getImageData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve profile picture: " + e.getMessage());
        }
    }
}