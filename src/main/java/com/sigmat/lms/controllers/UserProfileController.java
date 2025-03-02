package com.sigmat.lms.controllers;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.services.JwtService;
import com.sigmat.lms.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "file://*"})
public class UserProfileController {

    private final JwtService jwtService;
    private final UserProfileService userProfileService;
    private final UserRepo userRepo;

    @Autowired
    public UserProfileController(JwtService jwtService, UserProfileService userProfileService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userProfileService = userProfileService;
        this.userRepo = userRepo;
    }

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

    @PutMapping("/user/profile/password")
    public ResponseEntity<Void> updateUserPassword(@RequestParam Long userID, @RequestParam String newPassword) {
        userProfileService.updateUserPassword(userID, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/profile/getuserID")
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
}