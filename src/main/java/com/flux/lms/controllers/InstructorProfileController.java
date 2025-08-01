package com.flux.lms.controllers;

import com.flux.lms.dtos.InstructorProfileDTO;
import com.flux.lms.models.*;
import com.flux.lms.repository.InstructorRepo;
import com.flux.lms.services.InstructorProfileService;
import com.flux.lms.services.InstructorService;
import com.flux.lms.services.JwtService;
import com.flux.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/instructor")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "file://*"})
public class InstructorProfileController {

    private final JwtService jwtService;
    private final InstructorProfileService instructorProfileService;
    private final InstructorRepo instructorRepo;
    private final InstructorService instructorService;
    private final UserService userService;

    @Autowired
    public InstructorProfileController(JwtService jwtService, 
                                     InstructorProfileService instructorProfileService, 
                                     InstructorRepo instructorRepo,
                                     InstructorService instructorService,
                                     UserService userService) {
        this.jwtService = jwtService;
        this.instructorProfileService = instructorProfileService;
        this.instructorRepo = instructorRepo;
        this.instructorService = instructorService;
        this.userService = userService;
    }

    private boolean isAuthorized(String token, Long resourceInstructorId) {
        String jwt = token.substring(7);
        String username = jwtService.extractUserName(jwt);
        Users user = userService.findByUsername(username);
        if (user != null) {
            if (user.getRoles().contains(Role.SUPER_ADMIN) || user.getRoles().contains(Role.ADMIN)) {
                return true;
            }
            // Check if the user is the instructor themselves
            Instructor instructor = instructorRepo.findByUser(user);
            return instructor != null && instructor.getInstructorId().equals(resourceInstructorId);
        }
        return false;
    }

    // Retrieve Instructor Profile From InstructorID
    @GetMapping("/profile/{instructorId}")
    public ResponseEntity<InstructorProfileDTO> getInstructorProfile(@PathVariable Long instructorId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        InstructorProfileDTO instructorProfile = instructorProfileService.getInstructorProfileDto(instructorId);
        if (instructorProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(instructorProfile);
    }

    // Create Instructor Profile
    @PostMapping("/profile/{instructorId}")
    public ResponseEntity<InstructorProfile> createInstructorProfile(@PathVariable Long instructorId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            InstructorProfile createdProfile = instructorProfileService.createInstructorProfile(instructorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update Instructor Profile
    @PutMapping("/profile/{instructorId}")
    public ResponseEntity<InstructorProfile> updateInstructorProfile(@PathVariable Long instructorId, 
                                                                   @RequestBody InstructorProfile instructorProfile, 
                                                                   @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            InstructorProfile updatedProfile = instructorProfileService.updateInstructorProfile(instructorId, instructorProfile);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update Instructor Password
    @PutMapping("/profile/password")
    public ResponseEntity<Void> updateInstructorPassword(@RequestParam Long instructorId, 
                                                        @RequestParam String newPassword, 
                                                        @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            instructorProfileService.updateInstructorPassword(instructorId, newPassword);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Retrieve InstructorID From JWT
    @GetMapping("/profile/getInstructorId")
    public ResponseEntity<Long> getInstructorId(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtService.extractUserName(token);
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Instructor instructor = instructorRepo.findByUser(user);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(instructor.getInstructorId());
    }

    // Retrieve ProfileImageID From InstructorID
    @GetMapping("/profile/getProfileImageID/{instructorId}")
    public ResponseEntity<Long> getProfileImageID(@PathVariable Long instructorId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long profileImageID = instructorProfileService.getProfileImageID(instructorId);
        return ResponseEntity.ok(profileImageID);
    }

    // Save ProfileImage With InstructorID
    @PostMapping("/profile/pic/upload/{instructorId}")
    public ResponseEntity<InstructorProfile> uploadProfileImage(@PathVariable Long instructorId, 
                                                              @RequestParam("file") MultipartFile file, 
                                                              @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            InstructorProfile updatedProfile = instructorProfileService.saveProfileImage(instructorId, file);
            if (updatedProfile != null) {
                return ResponseEntity.ok(updatedProfile);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile/pic/{instructorId}")
    public ResponseEntity<?> getProfilePic(@PathVariable Long instructorId, @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, instructorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            InstructorProfile instructorProfile = instructorProfileService.getInstructorProfileWithImage(instructorId);

            if (instructorProfile == null || instructorProfile.getProfileImage() == null) {
                return ResponseEntity.notFound().build();
            }

            ProfileImage profileImage = instructorProfile.getProfileImage();

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