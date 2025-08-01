package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.EnrollmentDTO;
import com.sigmat.lms.dtos.ProfileImageUploadResponseDTO;
import com.sigmat.lms.dtos.UserProfileDTO;
import com.sigmat.lms.dtos.UserSubscriptionDTO;
import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.ProfileImageRepo;
import com.sigmat.lms.repository.UserRepo;
import com.sigmat.lms.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "file://*"})
public class UserProfileController {

    private final JwtService jwtService;
    private final UserProfileService userProfileService;
    private final UserRepo userRepo;
    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public UserProfileController(JwtService jwtService, UserProfileService userProfileService, UserRepo userRepo, ProfileImageRepo profileImageRepo, ProfileImageService profileImageService, EnrollmentService enrollmentService, UserService userService, SubscriptionService subscriptionService) {
        this.jwtService = jwtService;
        this.userProfileService = userProfileService;
        this.userRepo = userRepo;
        this.enrollmentService = enrollmentService;
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    private boolean isAuthorized(String token, Long resourceUserId) {
        String jwt = token.substring(7);
        String username = jwtService.extractUserName(jwt);
        Users user = userService.findByUsername(username);
        if (user != null) {
            if (user.getRoles().contains(Role.SUPER_ADMIN) || user.getRoles().contains(Role.ADMIN)) {
                return true;
            }
            return user.getId().equals(resourceUserId);
        }
        return false;
    }

    //Retrieve User Profile From UserID
    @GetMapping("/profile/{userID}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userID, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserProfileDTO userProfile = userProfileService.getUserProfileDto(userID);
        return ResponseEntity.ok(userProfile);
    }

    //Update User Profile
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfile userProfile, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserProfile updatedProfile = userProfileService.updateUserProfile(userId, userProfile);
        return ResponseEntity.ok(updatedProfile);
    }

    //Update User Password
    @PutMapping("/profile/password")
    public ResponseEntity<Void> updateUserPassword(@RequestParam Long userID, @RequestParam String newPassword, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    public ResponseEntity<Long> getProfileImageID(@PathVariable Long userID, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userID)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long profileImageID = userProfileService.getProfileImageID(userID);
        return ResponseEntity.ok(profileImageID);
    }
    
    //Save ProfileImage With UserID
    @PostMapping("/profile/pic/upload/{userId}")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            ProfileImageUploadResponseDTO response = userProfileService.saveProfileImageOptimized(userId, file);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                ProfileImageUploadResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ProfileImageUploadResponseDTO.builder()
                    .success(false)
                    .message("Failed to upload image: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @GetMapping("/profile/pic/{userId}")
    public ResponseEntity<?> getProfilePic(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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

    // New endpoint to enroll a user in a course
    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enrollUserInCourse(@RequestParam Long userId, @RequestParam Long courseId, @RequestParam(required = false) Long instructorId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Enrollment enrollment = enrollmentService.enrollUserInCourse(userId, courseId, instructorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Or a more specific error response
        }
    }

    // New endpoint to get all enrollments for a user
    @GetMapping("/enrollments/{userId}")
    public ResponseEntity<List<EnrollmentDTO>> getUserEnrollments(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    // Get user's current subscription
    @GetMapping("/subscription/{userId}")
    public ResponseEntity<UserSubscriptionDTO> getUserCurrentSubscription(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserSubscriptionDTO subscription = subscriptionService.getCurrentSubscription(userId);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        }
        return ResponseEntity.noContent().build();
    }

    // Get all user's subscriptions
    @GetMapping("/subscriptions/{userId}")
    public ResponseEntity<List<UserSubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<UserSubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }
}